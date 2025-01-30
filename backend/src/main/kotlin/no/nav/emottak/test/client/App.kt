package no.nav.emottak.test.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.nav.emottak.test.client.adapters.ebxml.controller.ebxmlFrikortRoute
import no.nav.emottak.test.client.application.ebxml.usecases.SendEbxmlMessageUseCase
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import no.nav.emottak.test.client.infrastructure.config.applicationConfig
import no.nav.emottak.test.client.infrastructure.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 13001, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    configureSerialization()

    val applicationConfig: ApplicationConfig = applicationConfig()
    val sendEbxmlMessageUseCase = createSendEbxmlMessageUseCase(applicationConfig)
    registerEbxmlFrikortRoute(sendEbxmlMessageUseCase)

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

private fun Application.registerEbxmlFrikortRoute(sendEbxmlMessageUseCase: SendEbxmlMessageUseCase) {
    routing {
        ebxmlFrikortRoute(sendEbxmlMessageUseCase)
    }
}

private fun Application.configureHelloWorldRouting() {
    routing {
        get("/") {
            call.respondText("Hello World from emottak-test-client")
        }
    }
}
