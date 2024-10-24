package no.nav.emottak.test.client.application.routing

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.nav.emottak.test.client.domain.EbxmlResult
import no.nav.emottak.test.client.domain.EbxmlService

fun Application.configureEbxmlFrikortRouting(ebxmlService: EbxmlService) {
    routing {
        get("/frikort") {
            when (val result = ebxmlService.sendEbxmlRequest()) {
                is EbxmlResult.Success -> call.respondText(result.message)
                is EbxmlResult.Failure -> call.respondText(
                    "Failed: ${result.error} with status ${result.statusCode}",
                    status = io.ktor.http.HttpStatusCode.InternalServerError
                )
            }
        }
    }
}