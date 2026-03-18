package no.nav.emottak.test.client.infrastructure.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceSource
import no.nav.emottak.utils.config.Server

@OptIn(ExperimentalHoplite::class)
fun applicationConfig() = ConfigLoader.builder()
    .addEnvironmentSource()
    .addResourceSource("/application.yml")
    .withExplicitSealedTypes()
    .build()
    .loadConfigOrThrow<ApplicationConfig>()

data class ApplicationConfig(
    val hostName: String,
    val ebmsSyncRouterUrl: String,
    val signing: SigningConfig,
    val server: Server,
    val kafka: KafkaConfig = KafkaConfig()
)

data class SigningConfig(
    val key: String,
    val password: String,
    val path: String
)

data class KafkaConfig(
    val bootstrapServers: String = "localhost:9092",
    val topic: String = "team-emottak.smtp.in.ebxml.payload"
)
