package no.nav.emottak.test.client.application.ebxml.usecases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.mockk.mockk
import kotlinx.serialization.json.Json
import no.nav.emottak.test.client.domain.EbxmlResult
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig

class SendEbxmlMessageUseCaseTest : FunSpec({

    val mockApplicationConfig = mockk<ApplicationConfig>()

    test("Returns failure if required fields are missing") {
        val httpClient = HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }
        }

        val incompleteRequestJson = """
        {
          "messageId": "",
          "conversationId": "",
          "fromPartyId": "",
          "fromRole": "",
          "toPartyId": "",
          "toRole": "",
          "cpaId": "",
          "service": "",
          "action": ""
        }
        """.trimIndent()

        val requestDto = Json.decodeFromString<EbxmlRequest>(incompleteRequestJson)

        val useCase = SendEbxmlMessageUseCase(mockApplicationConfig, httpClient)

        val result = useCase.sendEbxmlMessage(requestDto)

        result.shouldBeInstanceOf<EbxmlResult.Failure>()
        result.error.apply {
            shouldContain("messageId cannot be blank")
            shouldContain("conversationId cannot be blank")
            shouldContain("fromPartyId cannot be blank")
            shouldContain("fromRole cannot be blank")
            shouldContain("toPartyId cannot be blank")
            shouldContain("toRole cannot be blank")
            shouldContain("cpaId cannot be blank")
            shouldContain("service cannot be blank")
            shouldContain("action cannot be blank")
        }
    }
})
