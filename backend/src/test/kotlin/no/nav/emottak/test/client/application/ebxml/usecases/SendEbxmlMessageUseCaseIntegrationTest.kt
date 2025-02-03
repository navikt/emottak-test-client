package no.nav.emottak.test.client.application.ebxml.usecases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import java.util.Base64
import kotlinx.serialization.json.Json
import no.nav.emottak.test.client.domain.EbxmlResult

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
        <?xml version="1.0" encoding="utf-8"?>
        <MsgHead xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xsi:schemaLocation="http://www.kith.no/xmlstds/msghead/2006-05-24 MsgHead-v1_2.xsd" xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24">
          <MsgInfo>
            <Type V="PasientlisteForesporsel" DN="Pasientliste abonnement" />
            <MIGversion>v1.2 2006-05-24</MIGversion>
            <GenDate>2024-08-30T14:50:45+02:00</GenDate>
            <MsgId>fd36e9d0-0e7f-409d-94b4-73771c4c4d26</MsgId>
            <Ack V="J" DN="Ja" />
            <Sender>
              <Organisation>
                <OrganisationName>WebMed Test HelseNorge</OrganisationName>
                <Ident>
                  <Id>8095699</Id>
                  <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id" />
                </Ident>
                <Ident>
                  <Id>100112140</Id>
                  <TypeId V="ENH" S="2.16.578.1.12.4.1.1.9051" DN="Organisasjonsnummeret i Enhetsregister" />
                </Ident>
                <Address>
                  <StreetAdr>Søren Bulls vei 25</StreetAdr>
                  <PostalCode>1051</PostalCode>
                  <City>Oslo</City>
                </Address>
                <TeleCom>
                  <TeleAddress V="tel:73521234" />
                </TeleCom>
                <TeleCom>
                  <TeleAddress V="fax:73524322" />
                </TeleCom>
                <TeleCom>
                  <TeleAddress V="mailto:katskin@noline.no" />
                </TeleCom>
                <HealthcareProfessional>
                  <TypeHealthcareProfessional V="LE" DN="Lege" />
                  <RoleToPatient V="6" S="2.16.578.1.12.4.1.1.9034" DN="Fastlege" />
                  <FamilyName>Koman</FamilyName>
                  <GivenName>Magnar (ddfl) Dev</GivenName>
                  <Ident>
                    <Id>9144889</Id>
                    <TypeId V="HPR" S="2.16.578.1.12.4.1.1.8116" DN="HPR-nummer" />
                  </Ident>
                  <Ident>
                    <Id>8133984</Id>
                    <TypeId V="HER" S="2.16.578.1.12.4.1.1.8116" DN="HER-id" />
                  </Ident>
                </HealthcareProfessional>
              </Organisation>
            </Sender>
            <Receiver>
              <Organisation>
                <OrganisationName>NAV</OrganisationName>
                <Ident>
                  <Id>79768</Id>
                  <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id" />
                </Ident>
              </Organisation>
            </Receiver>
          </MsgInfo>
          <Document>
            <DocumentConnection V="H" DN="Hoveddokument" />
            <RefDoc>
              <MsgType V="XML" DN="XML-instans" />
              <Content>
                <PasientlisteForesporsel xmlns="http://www.kith.no/xmlstds/nav/pasientliste/2010-02-01">
                  <HentPasientliste>
                    <FnrLege>04056600324</FnrLege>
                    <KommuneNr>0301</KommuneNr>
                    <Format V="PI" />
                  </HentPasientliste>
                </PasientlisteForesporsel>
              </Content>
            </RefDoc>
          </Document>
        <Signature xmlns="http://www.w3.org/2000/09/xmldsig#"><SignedInfo><CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315" /><SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256" /><Reference URI=""><Transforms><Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" /></Transforms><DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256" /><DigestValue>Mw3JAcUQ7hNH2DOpqA27Dgxpe1WBijaKh34ZM6+5OcI=</DigestValue></Reference></SignedInfo><SignatureValue>erj7FCX/WSN5UNejZYSnAVFg9pPk/u+C5vthLg+rwD7cXL2NuVsqq8qGMRps0p6X4dbawnk9STdGwhaxg/WQT+0TYBlbAntLXxLAlk+qWYEC2HieTWlfTkvbn2w8hjB872biGdqlsJFk13CWVebRxFOfVt8JbNQYkm3P+XPvLSVzBCKLPTNl9Qy+lM+Nrtb8k7aFHBYqpDJ1h7PccrC8UIQeTSFKHsWi8JTCZqrk7hF6AZmApVKuFy4lbS6/I4tt1TIcw4U8GTnJBOhzUC1HIVW0PoEc0XICLaSwj+AZy8aCRTn4eHBYAQiEzc/q6KFZUgXW97L6aGmuJzQ9qon1bw==</SignatureValue><KeyInfo><X509Data><X509Certificate>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</X509Certificate></X509Data></KeyInfo></Signature></MsgHead>
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
