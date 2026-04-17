package no.nav.emottak.test.client

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.core.raise.result
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import com.nimbusds.jwt.SignedJWT
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.serialization.Configuration
import io.ktor.serialization.kotlinx.serialization
import io.ktor.server.application.Application
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.json.Json
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
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI

private val log = LoggerFactory.getLogger("no.nav.emottak.test.client.App")

const val AZURE_AD_AUTH = "AZURE_AD"

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
    val httpClientProvider = scopedAuthHttpClient(SMTP_TRANSPORT_SCOPE)
    return SmtpTransportClient(applicationConfig.smtpTransportUrl, httpClientProvider.invoke())
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

val SMTP_TRANSPORT_SCOPE = getEnvVar(
    "SMTP_TRANSPORT_SCOPE",
    "api://" + getEnvVar("NAIS_CLUSTER_NAME", "dev-fss") + ".team-emottak.smtp-transport/.default"
)

fun scopedAuthHttpClient(
    scope: String
): () -> HttpClient {
    return {
        HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                jsonLenient()
            }
            install(Auth) {
                bearer {
                    refreshTokens {
                        proxiedHttpClient().post(
                            getEnvVar(
                                "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
                                "http://localhost:3344/$AZURE_AD_AUTH/token"
                            )
                        ) {
                            headers {
                                header("Content-Type", "application/x-www-form-urlencoded")
                            }
                            setBody(
                                "client_id=" + getEnvVar("AZURE_APP_CLIENT_ID", "dummyclient") +
                                    "&client_secret=" + getEnvVar("AZURE_APP_CLIENT_SECRET", "dummysecret") +
                                    "&scope=" + scope +
                                    "&grant_type=client_credentials"
                            )
                        }.bodyAsText()
                            .let { tokenResponseString ->
                                log.info("The token response string we received was: $tokenResponseString")
                                SignedJWT.parse(
                                    LENIENT_JSON_PARSER.decodeFromString<Map<String, String>>(tokenResponseString)["access_token"] as String
                                )
                            }
                            .let { parsedJwt ->
                                log.info("After parsing it, we got: $parsedJwt")
                                BearerTokens(parsedJwt.serialize(), "refresh token is unused")
                            }
                    }
                    sendWithoutRequest {
                        true
                    }
                }
            }
        }
    }
}

private fun proxiedHttpClient() = HttpClient(CIO) {
    engine {
        val httpProxyUrl = getEnvVar("HTTP_PROXY", "")
        if (httpProxyUrl.isNotBlank()) {
            proxy = Proxy(
                Proxy.Type.HTTP,
                InetSocketAddress(URI(httpProxyUrl).toURL().host, URI(httpProxyUrl).toURL().port)
            )
        }
    }
}

val LENIENT_JSON_PARSER = Json {
    encodeDefaults = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    prettyPrint = false
    useArrayPolymorphism = false
    ignoreUnknownKeys = true
}

fun Configuration.jsonLenient(
    json: Json = LENIENT_JSON_PARSER,
    contentType: ContentType = ContentType.Application.Json
) {
    serialization(contentType, json)
}
