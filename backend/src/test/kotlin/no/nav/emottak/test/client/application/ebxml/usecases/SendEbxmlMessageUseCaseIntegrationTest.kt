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
import no.nav.emottak.test.client.infrastructure.config.applicationConfig
import java.util.Base64

// Integration tests for manual testing
class SendEbxmlMessageUseCaseIntegrationTest : FunSpec({

    val testConfig = testApplicationConfig()
    val httpClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.DEFAULT
        }
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

    xtest("Send Pasientliste Integration Test") {

        val hentPasientlistePayload = """
        <?xml version="1.0" encoding="UTF-8"?>
        <MsgHead xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24"
                 xsi:schemaLocation="http://www.kith.no/xmlstds/msghead/2006-05-24 MsgHead-v1_2.xsd"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <MsgInfo>
                <Type DN="Hent pasientliste" V="HentPasientliste"/>
                <MIGversion>v1.2 2006-05-24</MIGversion>
                <GenDate>2021-01-18T14:58:14</GenDate>
                <MsgId>1ed4dd1b-ec4f-4fc3-b152-1737555b47b9</MsgId>
                <ProcessingStatus DN="Opplæring" V="T"/>
                <Sender>
                    <Organisation>
                        <OrganisationName>Tveita Legesenter</OrganisationName>
                        <Ident>
                            <Id>1382</Id>
                            <TypeId V="HER" DN="HER-id" S="2.16.578.1.12.4.1.1.9051"/>
                        </Ident>
                        <HealthcareProfessional>
                            <FamilyName>Toska</FamilyName>
                            <GivenName>Emil</GivenName>
                            <Ident>
                                <Id>17087000133</Id>
                                <TypeId DN="Fødselsnummer" S="2.16.578.1.12.4.1.1.8116" V="FNR"/>
                            </Ident>
                        </HealthcareProfessional>
                    </Organisation>
                </Sender>
                <Receiver>
                    <Organisation>
                        <OrganisationName>NAV Arbeidsog velferdsdirektoratet</OrganisationName>
                        <Ident>
                            <Id>90128</Id>
                            <TypeId V="HER" DN="HER-id" S="2.16.578.1.12.4.1.1.9051"/>
                        </Ident>
                        <Organisation>
                            <OrganisationName>Samhandling Arbeids- og velferdsetaten</OrganisationName>
                            <Ident>
                                <Id>79768</Id>
                                <TypeId V="HER" DN="HER-id" S="2.16.578.1.12.4.1.1.9051"/>
                            </Ident>
                        </Organisation>
                    </Organisation>
                </Receiver>
            </MsgInfo>
            <Document>
                <RefDoc>
                    <MsgType DN="Hent pasientliste" V="HentPasientliste"/>
                    <Content>
                        <ep2:PasientlisteForesporsel xmlns:ep2="http://www.kith.no/xmlstds/nav/pasientliste/2010-02-01">
                            <ep2:HentPasientliste>
                                <ep2:FnrLege>17087000133</ep2:FnrLege>
                                <ep2:KommuneNr>0301</ep2:KommuneNr>
                                <ep2:Format DN="PasientInformasjon" V="PI"/>
                            </ep2:HentPasientliste>
                        </ep2:PasientlisteForesporsel>
                    </Content>
                </RefDoc>
            </Document>
            <dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">
                <dsig:SignedInfo>
                    <dsig:CanonicalizationMethod
                            Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"></dsig:CanonicalizationMethod>
                    <dsig:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"></dsig:SignatureMethod>
                    <dsig:Reference URI="">
                        <dsig:Transforms>
                            <dsig:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"></dsig:Transform>
                            <dsig:Transform Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"></dsig:Transform>
                        </dsig:Transforms>
                        <dsig:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"></dsig:DigestMethod>
                        <dsig:DigestValue>YcNcAnTeHNLKvU38/aurCUppyv9bLhatfroV4OodtP4=</dsig:DigestValue>
                    </dsig:Reference>
                </dsig:SignedInfo>
                <dsig:SignatureValue>RucQnGbNf2pRTDnDBhRN+S46oCj1nUedDkVgQsWRbX/7pjwS+B/RprcGqIeAMqEDOJW9CJJF
                    M58exQ/s/Ke1qP+3Dhrw5HZtLc4spBblfKk4hENCphICcFsRTv5Aw7hVqk/VsMBR2YfQahwf
                    JjTZ75SIrmYbG3dzy+x60FOqN71nPFF9+Gi10Dn4Oymh1W0ttttBegkumhnx5MQ2dD+MkXGe
                    +yIqfbZk4ADyRxBno+QfOjgpmPhYRiHCE2y2eWgLoS3yxZKpun1+OK4/aZymtBxfTLlG4dlK
                    wnL+QbkjwQHqsyiyMtMS/FvEADb64mcWi7GXRe2sxekBEhZoNh8HLQ==
                </dsig:SignatureValue>
                <dsig:KeyInfo>
                    <dsig:X509Data>
                        <dsig:X509Certificate>
                            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
                        </dsig:X509Certificate>
                    </dsig:X509Data>
                </dsig:KeyInfo>
            </dsig:Signature>
        </MsgHead><!-- Version: 2.80.240903 User: ML-DEV-GSE - Machine: ML-UTV-GSE-001 - IP: 10.70.69.220 -->
        """.trimIndent()

        val payloadBase64 = Base64.getEncoder().encodeToString(hentPasientlistePayload.toByteArray())

        val requestJson = """
        {
          "fromPartyId": "13579",
          "fromRole": "Fastlege",
          "toPartyId": "79768",
          "toRole": "Fastlegeregister",
          "cpaId": "nav:qass:36666",
          "service": "PasientlisteForesporsel",
          "action": "HentPasientliste",
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
