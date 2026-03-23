package no.nav.emottak.test.client

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.core.raise.result
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.awaitCancellation
import no.nav.emottak.test.client.adapters.ebxml.controller.sendEbxmlMessageRoute
import no.nav.emottak.test.client.application.ebxml.usecases.SendEbxmlMessageAsyncUseCase
import no.nav.emottak.test.client.application.ebxml.usecases.SendEbxmlMessageUseCase
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import no.nav.emottak.test.client.infrastructure.config.applicationConfig
import no.nav.emottak.test.client.infrastructure.kafka.KafkaProducerService
import no.nav.emottak.test.client.infrastructure.plugins.configureSerialization
import no.nav.emottak.test.client.infrastructure.smtp.SmtpTransportClient
import no.nav.emottak.utils.environment.getEnvVar
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("no.nav.emottak.test.client.App")

fun main() = SuspendApp {
    result {
        resourceScope {
            setupServer()
            awaitCancellation()
        }
    }.onFailure { error ->
        if (error !is CancellationException) {
            log.error("Shutdown due to: ${error.stackTraceToString()}")
        }
    }
}

suspend fun ResourceScope.setupServer() {
    val serverConfig = applicationConfig().server

    server(
        Netty,
        port = serverConfig.port.value,
        preWait = serverConfig.preWait,
        module = { testClientModule() }
    )
}

fun Application.testClientModule() {
    configureSerialization()

    val applicationConfig: ApplicationConfig = applicationConfig()
    val sendEbxmlMessageUseCase = createSendEbxmlMessageUseCase(applicationConfig)
    val kafkaProducerService = KafkaProducerService(applicationConfig.kafka.bootstrapServers)
    val smtpTransportClient = createSmtpTransportClient(applicationConfig)
    val sendEbxmlMessageAsyncUseCase = SendEbxmlMessageAsyncUseCase(
        applicationConfig,
        kafkaProducerService,
        smtpTransportClient
    )

    registerSendEbxmlMessageRoute(sendEbxmlMessageUseCase, sendEbxmlMessageAsyncUseCase)
    configureHelloWorldRouting()
}

private fun createSendEbxmlMessageUseCase(applicationConfig: ApplicationConfig): SendEbxmlMessageUseCase {
    val httpClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.DEFAULT
        }
    }
    return SendEbxmlMessageUseCase(applicationConfig, httpClient)
}

private fun createSmtpTransportClient(applicationConfig: ApplicationConfig): SmtpTransportClient {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.DEFAULT
        }
    }
    val tokenProvider: suspend () -> String = {
        fetchAzureAdToken(
            tokenEndpoint = getEnvVar("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", "http://localhost:3344/azure-ad/token"),
            clientId = getEnvVar("AZURE_APP_CLIENT_ID", "dummyclient"),
            clientSecret = getEnvVar("AZURE_APP_CLIENT_SECRET", "dummysecret"),
            scope = getEnvVar(
                "SMTP_TRANSPORT_SCOPE",
                "api://${getEnvVar("NAIS_CLUSTER_NAME", "dev-fss")}.team-emottak.smtp-transport/.default"
            ),
            httpClient = httpClient
        )
    }
    return SmtpTransportClient(applicationConfig.smtpTransportUrl, httpClient, tokenProvider)
}

private suspend fun fetchAzureAdToken(
    tokenEndpoint: String,
    clientId: String,
    clientSecret: String,
    scope: String,
    httpClient: HttpClient
): String {
    val response = httpClient.post(tokenEndpoint) {
        headers { header("Content-Type", "application/x-www-form-urlencoded") }
        setBody("client_id=$clientId&client_secret=$clientSecret&scope=$scope&grant_type=client_credentials")
    }
    val body = kotlinx.serialization.json.Json.decodeFromString<Map<String, String>>(response.bodyAsText())
    return body["access_token"] ?: error("No access_token in response from $tokenEndpoint")
}

private fun Application.registerSendEbxmlMessageRoute(
    sendEbxmlMessageUseCase: SendEbxmlMessageUseCase,
    sendEbxmlMessageAsyncUseCase: SendEbxmlMessageAsyncUseCase
) {
    routing {
        sendEbxmlMessageRoute(sendEbxmlMessageUseCase, sendEbxmlMessageAsyncUseCase)
    }
}

private fun Application.configureHelloWorldRouting() {
    routing {
        get("/") {
            call.respondText("Hello World from emottak-test-client (the backend)")
        }
    }
}
