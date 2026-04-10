package no.nav.emottak.test.client.application.ebxml.usecases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import no.nav.emottak.test.client.domain.EbxmlResult
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import java.util.Base64

// Integration tests for manual testing
class SendEbxmlMessageUseCaseIntegrationTest : FunSpec({

    lateinit var testConfig: ApplicationConfig
    val httpClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.DEFAULT
        }
    }

    beforeTest {
        testConfig = testApplicationConfig()
    }

    xtest("Send Frikort Integration Test") {
        val frikortPayload = """
        <?xml version="1.0" encoding="utf-8"?>
        <ns:MsgHead xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rawXsd="http://www.w3.org/2001/XMLSchema" xsi:schemaLocation="http://www.kith.no/xmlstds/msghead/2006-05-24 MsgHead-v1_2.rawXsd" xmlns:ns="http://www.kith.no/xmlstds/msghead/2006-05-24">
            <ns:MsgInfo>
                <ns:Type V="EgenandelForesporsel" DN="Forespørsel om egenandel"/>
                <ns:MIGversion>v1.2 2006-05-24</ns:MIGversion>
                <ns:GenDate>2024-03-13T16:08:43</ns:GenDate>
                <ns:MsgId>3b938c71-0ccf-4ace-86f1-7a2aaefa6f2b</ns:MsgId>
                <ns:Sender>
                    <ns:Organisation>
                        <ns:OrganisationName>Speare AS</ns:OrganisationName>
                        <ns:Ident>
                            <ns:Id>8141253</ns:Id>
                            <ns:TypeId V="HER" DN="HER-id" S="2.16.578.1.12.4.1.1.9051"/>
                        </ns:Ident>
                        <ns:Ident>
                            <ns:Id>993954896</ns:Id>
                            <ns:TypeId V="ENH" DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051"/>
                        </ns:Ident>
                    </ns:Organisation>
                </ns:Sender>
                <ns:Receiver>
                    <ns:Organisation>
                        <ns:OrganisationName>NAV</ns:OrganisationName>
                        <ns:Ident>
                            <ns:Id>79768</ns:Id>
                            <ns:TypeId V="HER" DN="HER-id" S="2.16.578.1.12.4.1.1.9051"/>
                        </ns:Ident>
                        <ns:Ident>
                            <ns:Id>889640782</ns:Id>
                            <ns:TypeId V="ENH" DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051"/>
                        </ns:Ident>
                    </ns:Organisation>
                </ns:Receiver>
            </ns:MsgInfo>
            <ns:Document>
                <ns:DocumentConnection V="H" DN="Hoveddokument"/>
                <ns:RefDoc>
                    <ns:MsgType V="XML" DN="XML-instans"/>
                    <ns:MimeType>text/rawXml</ns:MimeType>
                    <ns:Description>EgenandelForesporsel</ns:Description>
                    <ns:Content>
                        <EgenandelForesporselV2 xmlns="http://www.kith.no/xmlstds/nav/egenandel/2016-06-10">
                            <HarBorgerFrikort>
                                <BorgerFnr>05058705065</BorgerFnr>
                                <Dato>2022-12-01</Dato>
                                <TjenestetypeKode>PS</TjenestetypeKode>
                            </HarBorgerFrikort>
                        </EgenandelForesporselV2>
                    </ns:Content>
                </ns:RefDoc>
            </ns:Document>
        </ns:MsgHead>
        """.trimIndent()

        val payloadBase64 = Base64.getEncoder().encodeToString(frikortPayload.toByteArray())

        val requestJson = """
        {
          "fromPartyId": "13579",
          "fromRole": "Behandler",
          "toPartyId": "79768",
          "toRole": "Frikortregister",
          "cpaId": "nav:qass:36666",
          "service": "HarBorgerFrikort",
          "action": "EgenandelForesporsel",
          "ebxmlPayload": {
            "base64Content": "$payloadBase64"
          }
        }
        """.trimIndent()

        val requestDto = Json.decodeFromString<EbxmlRequest>(requestJson)
        val useCase = SendEbxmlMessageUseCase(testConfig, httpClient)

        val result = useCase.sendEbxmlMessage(requestDto)

        result.shouldBeInstanceOf<EbxmlResult.Success>()
    }

})
