package no.nav.emottak.test.client.adapters.ebxml.kafka

import no.nav.emottak.utils.config.Kafka
import no.nav.emottak.utils.config.toProperties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.Properties

const val MESSAGE_FORMAT_HEADER = "messageFormat"
const val SOAP_WITH_ATTACHMENTS_FORMAT = "SoapWithAttachments"

class EbxmlKafkaProducer(private val kafka: Kafka, private val topic: String) {

    private val log = LoggerFactory.getLogger(EbxmlKafkaProducer::class.java)

    private val producer: KafkaProducer<String, ByteArray> by lazy {
        KafkaProducer(producerProperties())
    }

    fun sendSoapWithAttachments(key: String, value: ByteArray) {
        val record = ProducerRecord(
            topic,
            null,
            key,
            value,
            listOf(
                RecordHeader(MESSAGE_FORMAT_HEADER, SOAP_WITH_ATTACHMENTS_FORMAT.toByteArray())
            )
        )
        producer.send(record) { metadata, exception ->
            if (exception == null) {
                log.info(
                    "SoapWithAttachments message sent to topic: {}, partition: {}, offset: {}",
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
                )
            } else {
                log.error("Failed to send SoapWithAttachments message: {}", exception.message, exception)
            }
        }
        producer.flush()
    }

    private fun producerProperties(): Properties = kafka.toProperties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.bootstrapServers)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer::class.java.name)
    }
}
