package no.nav.emottak.test.client.application

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
import no.nav.emottak.test.client.adapters.ebxml.EbxmlFrikortServiceAdapter
import no.nav.emottak.test.client.application.routing.configureEbxmlFrikortRouting
import no.nav.emottak.test.client.domain.EbxmlService


fun main() {
    embeddedServer(Netty, port = 13001, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    val ebxmlFrikortService = setupEbxmlFrikortService()
    configureEbxmlFrikortRouting(ebxmlFrikortService)
    routing {
        get("/") {
            call.respondText("Hello World from emottak-test-client")
        }
    }

}

private fun setupEbxmlFrikortService(): EbxmlService {
    val httpClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.DEFAULT
        }
    }
    return EbxmlFrikortServiceAdapter(httpClient)
}