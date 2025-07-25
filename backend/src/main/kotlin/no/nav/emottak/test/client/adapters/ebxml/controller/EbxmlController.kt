package no.nav.emottak.test.client.adapters.ebxml.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.nav.emottak.test.client.application.ebxml.usecases.EbxmlPayload
import no.nav.emottak.test.client.application.ebxml.usecases.EbxmlRequest
import no.nav.emottak.test.client.application.ebxml.usecases.SendEbxmlMessageUseCase
import no.nav.emottak.test.client.domain.EbxmlResult
import org.slf4j.LoggerFactory
import java.util.UUID

fun Routing.sendEbxmlMessageRoute(sendEbxmlMessageUseCase: SendEbxmlMessageUseCase) {
    val log = LoggerFactory.getLogger("no.nav.emottak.test.client.adapters.ebxml.controller.sendEbxmlMessageRoute")

    post("/ebxml/send-cpa") {
        try {
            val requestBody = call.receiveText()
            log.debug("/ebxml/send-cpa request received: $requestBody")

            val dto = Json.decodeFromString<EbxmlRequestDto>(requestBody)
            val ebxmlRequest = dto.toDomain()

            val result = sendEbxmlMessageUseCase.sendEbxmlMessage(ebxmlRequest)

            log.debug("/ebxml/send-cpa is returning response: $result")

            when (result) {
                is EbxmlResult.Success -> {
                    call.respond(HttpStatusCode.OK, result)
                }
                is EbxmlResult.Failure -> {
                    call.respond(HttpStatusCode.BadRequest, result)
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
        }
    }
}

@Serializable
data class EbxmlRequestDto(
    val conversationId: String = UUID.randomUUID().toString(),
    val messageId: String = UUID.randomUUID().toString(),
    val fromPartyId: String,
    val fromRole: String,
    val toPartyId: String,
    val toRole: String,
    val cpaId: String,
    val service: String,
    val action: String,
    val ebxmlPayload: PayloadDto? = null,
    val signPayload: Boolean = false
) {
    @Serializable
    data class PayloadDto(
        val base64Content: String? = null,
        val contentId: String? = null
    )

    fun toDomain(): EbxmlRequest {
        return EbxmlRequest(
            conversationId = conversationId,
            messageId = messageId,
            fromPartyId = fromPartyId,
            fromRole = fromRole,
            toPartyId = toPartyId,
            toRole = toRole,
            cpaId = cpaId,
            service = service,
            action = action,
            ebxmlPayload = ebxmlPayload?.let {
                EbxmlPayload(
                    base64Content = it.base64Content ?: "",
                    contentId = it.contentId ?: "${UUID.randomUUID()}@emottak-test-payload.nav.no"
                )
            }
        )
    }
}
