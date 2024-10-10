package no.nav.emottak.test.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.nav.emottak.test.client.melding.MeldingService

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(MeldingService().getMessage().toString())
        }

        get("/test") {
            // Do a test API CAll to https://ebms-provider-fss.intern.dev.nav.no
            val client = HttpClient(CIO)
            try {
                val response: HttpResponse = client.get("https://ebms-provider-fss.intern.dev.nav.no")
                call.respondText { "response.status: ${response.status}, response.body: ${response.body<String>()}" }
            } catch (ex: Exception) {
                call.respondText { "Failed to reach the API: ${ex.localizedMessage}" }
            }
            call.respondText { "test" }
        }
    }
}
