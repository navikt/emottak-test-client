package no.nav.emottak.test.client.infrastructure.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.Properties

class KafkaProducerService(bootstrapServers: String) : AutoCloseable {

    private val log = LoggerFactory.getLogger(KafkaProducerService::class.java)

    private val producer: KafkaProducer<String, ByteArray> = KafkaProducer(
        Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer::class.java.name)
            put(ProducerConfig.ACKS_CONFIG, "all")
            configureSslIfAvailable(this)
        }
    )

    fun publish(topic: String, key: String, value: ByteArray, senderAddress: String = "test@emottak-test.nav.no") {
        val record = ProducerRecord<String, ByteArray>(topic, key, value).apply {
            headers().add(RecordHeader("SENDER_ADDRESS", senderAddress.toByteArray()))
        }
        producer.send(record).get()
        log.info("Published message with key=$key to topic=$topic")
    }

    override fun close() {
        producer.close()
    }

    private fun configureSslIfAvailable(props: Properties) {
        val keystorePath = System.getenv("KAFKA_KEYSTORE_PATH") ?: return
        val credstorePassword = System.getenv("KAFKA_CREDSTORE_PASSWORD") ?: return
        val truststorePath = System.getenv("KAFKA_TRUSTSTORE_PATH") ?: return

        props[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SSL"
        props[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] = keystorePath
        props[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] = credstorePassword
        props[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] = truststorePath
        props[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = credstorePassword
        props[SslConfigs.SSL_KEY_PASSWORD_CONFIG] = credstorePassword
        log.info("Kafka SSL configured from NAIS environment variables")
    }
}
