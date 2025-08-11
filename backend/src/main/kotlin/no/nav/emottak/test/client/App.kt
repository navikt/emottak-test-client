package no.nav.emottak.test.client

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.core.raise.result
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.server.application.Application
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.awaitCancellation
import no.nav.emottak.test.client.adapters.ebxml.controller.sendEbxmlMessageRoute
import no.nav.emottak.test.client.application.ebxml.usecases.SendEbxmlMessageUseCase
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import no.nav.emottak.test.client.infrastructure.config.applicationConfig
import no.nav.emottak.test.client.infrastructure.plugins.configureSerialization
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
    registerSendEbxmlMessageRoute(sendEbxmlMessageUseCase)

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

private fun Application.registerSendEbxmlMessageRoute(sendEbxmlMessageUseCase: SendEbxmlMessageUseCase) {
    routing {
        sendEbxmlMessageRoute(sendEbxmlMessageUseCase)
    }
}

private fun Application.configureHelloWorldRouting() {
    routing {
        get("/") {
            call.respondText("Hello World from emottak-test-client (the backend)")
        }
    }
}
