package no.nav.emottak.test.client.application.ebxml.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import no.nav.emottak.test.client.application.ebxml.usecases.builders.EbxmlDocumentBuilder
import no.nav.emottak.test.client.application.ebxml.usecases.builders.Encrypting
import no.nav.emottak.test.client.domain.EbxmlResult
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import no.nav.emottak.test.client.infrastructure.kafka.KafkaProducerService
import no.nav.emottak.test.client.infrastructure.kafka.SendInProducerService
import no.nav.emottak.test.client.infrastructure.smtp.PayloadDto
import no.nav.emottak.test.client.infrastructure.smtp.SmtpTransportClient
import no.nav.emottak.test.client.infrastructure.xml.xmlMarshaller
import no.nav.emottak.utils.common.model.Addressing
import no.nav.emottak.utils.common.model.EbmsProcessing
import no.nav.emottak.utils.common.model.Party
import no.nav.emottak.utils.common.model.PartyId
import no.nav.emottak.utils.common.model.SendInRequest
import org.slf4j.LoggerFactory
import java.util.Base64

class SendEbxmlMessageAsyncUseCase(
    private val applicationConfig: ApplicationConfig,
    private val kafkaProducerService: KafkaProducerService,
    private val sendInProducerService: SendInProducerService,
    private val smtpTransportClient: SmtpTransportClient,
    private val encrypting: Encrypting = Encrypting()
) {

    private val log = LoggerFactory.getLogger(SendEbxmlMessageAsyncUseCase::class.java)

    suspend fun sendEbxmlMessage(requestDto: EbxmlRequest): EbxmlResult {
        if (requestDto.cpaId == "CPA_NO_EBXML") {
            log.info("Sending CPA_NO_EBXML request, directly to async processing")
            return sendSendInRequest(requestDto)
        }
        return try {
            log.info("Validating ASYNC request")
            validateRequestDto(requestDto)

            val builder = EbxmlDocumentBuilder(applicationConfig, requestDto)
            val signedDoc = builder.buildAndSign()
            val envelopeXml = xmlMarshaller.documentToString(signedDoc)
            val envelopeBytes = envelopeXml.toByteArray(Charsets.UTF_8)

            // Lagre payload i smtp-transport (referenceId = messageId)
            val payload = builder.payload
            if (payload != null) {
                log.info("Storing ASYNC payload in SMTP DB")
                val contentId = payload.contentId.removePrefix("cid:")
                withContext(Dispatchers.IO) {
                    smtpTransportClient.storePayload(
                        listOf(
                            PayloadDto(
                                referenceId = requestDto.messageId,
                                contentId = contentId,
                                contentType = payload.contentType,
                                content = payload.bytes
                            )
                        )
                    )
                }
                log.info("Stored payload with referenceId=${requestDto.messageId}, contentId=$contentId")
            }

            // Publiser konvolutten til Kafka
            log.info("Publishing envelope for ASYNC payload to EBXML IN topic")
            withContext(Dispatchers.IO) {
                kafkaProducerService.publish(
                    topic = applicationConfig.kafka.topic,
                    key = requestDto.messageId,
                    value = envelopeBytes
                )
            }

            log.info("Published async ebXML envelope to Kafka topic ${applicationConfig.kafka.topic}, messageId=${requestDto.messageId}")
            EbxmlResult.Success(
                "Message published to Kafka topic: ${applicationConfig.kafka.topic}",
                envelopeXml
            )
        } catch (ex: IllegalArgumentException) {
            log.error("Validation failed: ${ex.message}", ex)
            EbxmlResult.Failure("Validation failed: ${ex.message}")
        } catch (ex: Exception) {
            log.error("Error while sending async ebXML message: ${ex.message}", ex)
            EbxmlResult.Failure("Failed to send async message: ${ex.message}")
        }
    }

    private fun sendSendInRequest(requestDto: EbxmlRequest): EbxmlResult {
        return try {
            log.info("Validating ASYNC request")
            validateRequestDto(requestDto)

            val payload: ByteArray = Base64.getDecoder().decode(requestDto.ebxmlPayload!!.base64Content.trim())
            val contentId = requestDto.ebxmlPayload.contentId

            val sendInRequest = SendInRequest(
                messageId = requestDto.messageId,
                conversationId = requestDto.conversationId,
                payloadId = contentId.removePrefix("cid:"),
                payload = payload,
                addressing = Addressing(
                    Party(listOf(PartyId("HER", requestDto.toPartyId)), requestDto.toRole),
                    Party(listOf(PartyId("HER", requestDto.fromPartyId)), requestDto.fromRole),
                    requestDto.service,
                    requestDto.action
                ),
                cpaId = requestDto.cpaId,
                ebmsProcessing = EbmsProcessing(), // ikke i bruk
                signedOf = "TODO", // requestDto.ebxmlPayload.signedBy, legg evt inn Fnr
                requestId = requestDto.messageId, // todo evt gen uuid
                partnerId = 0L // ikke i bruk
            )
            val key = sendInRequest.requestId
            val json = Json.encodeToString<SendInRequest>(sendInRequest)
            log.info("Sending SendInRequest: $json")
            val value = json.toByteArray()

            sendInProducerService.publish(
                topic = applicationConfig.kafkaSendIn.topic,
                key = key,
                value = value
            )
            EbxmlResult.Success(
                "Message published to Kafka topic: ${applicationConfig.kafkaSendIn.topic}",
                "" // value.toString(Charsets.UTF_8)
            )
        } catch (ex: IllegalArgumentException) {
            log.error("Validation failed: ${ex.message}", ex)
            EbxmlResult.Failure("Validation failed: ${ex.message}")
        } catch (ex: Exception) {
            log.error("Error while sending async SnedIn message: ${ex.message}", ex)
            EbxmlResult.Failure("Failed to send async SendIn message: ${ex.message}")
        }
    }

    private fun validateRequestDto(requestDto: EbxmlRequest) {
        val errors = mutableListOf<String>()
        if (requestDto.messageId.isBlank()) errors.add("messageId cannot be blank")
        if (requestDto.conversationId.isBlank()) errors.add("conversationId cannot be blank")
        if (requestDto.fromPartyId.isBlank()) errors.add("fromPartyId cannot be blank")
        if (requestDto.fromRole.isBlank()) errors.add("fromRole cannot be blank")
        if (requestDto.toPartyId.isBlank()) errors.add("toPartyId cannot be blank")
        if (requestDto.toRole.isBlank()) errors.add("toRole cannot be blank")
        if (requestDto.cpaId.isBlank()) errors.add("cpaId cannot be blank")
        if (requestDto.service.isBlank()) errors.add("service cannot be blank")
        if (requestDto.action.isBlank()) errors.add("action cannot be blank")

        if (errors.isNotEmpty()) {
            throw IllegalArgumentException(errors.joinToString("; "))
        }
    }
}
