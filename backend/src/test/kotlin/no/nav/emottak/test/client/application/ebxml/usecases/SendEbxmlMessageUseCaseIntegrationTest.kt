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

    val testConfig = applicationConfig()
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

        val rawBase64Payload = """
           PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48TXNnSGVhZCB4bWxucz0i
           aHR0cDovL3d3dy5raXRoLm5vL3htbHN0ZHMvbXNnaGVhZC8yMDA2LTA1LTI0IiB4c2k6c2No
           ZW1hTG9jYXRpb249Imh0dHA6Ly93d3cua2l0aC5uby94bWxzdGRzL21zZ2hlYWQvMjAwNi0w
           NS0yNCBNc2dIZWFkLXYxXzIueHNkIiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIw
           MDEvWE1MU2NoZW1hLWluc3RhbmNlIj48TXNnSW5mbz48VHlwZSBETj0iSGVudCBwYXNpZW50
           bGlzdGUiIFY9IkhlbnRQYXNpZW50bGlzdGUiLz48TUlHdmVyc2lvbj52MS4yIDIwMDYtMDUt
           MjQ8L01JR3ZlcnNpb24+PEdlbkRhdGU+MjAyMS0wMS0xOFQxNDo1ODoxNDwvR2VuRGF0ZT48
           TXNnSWQ+MWVkNGRkMWItZWM0Zi00ZmMzLWIxNTItMTczNzU1NWI0N2I5PC9Nc2dJZD48UHJv
           Y2Vzc2luZ1N0YXR1cyBETj0iT3BwbMOmcmluZyIgVj0iVCIvPjxTZW5kZXI+PE9yZ2FuaXNh
           dGlvbj48T3JnYW5pc2F0aW9uTmFtZT5UdmVpdGEgTGVnZXNlbnRlcjwvT3JnYW5pc2F0aW9u
           TmFtZT48SWRlbnQ+PElkPjEzODI8L0lkPjxUeXBlSWQgVj0iSEVSIiBETj0iSEVSLWlkIiBT
           PSIyLjE2LjU3OC4xLjEyLjQuMS4xLjkwNTEiLz48L0lkZW50PjxIZWFsdGhjYXJlUHJvZmVz
           c2lvbmFsPjxGYW1pbHlOYW1lPlRvc2thPC9GYW1pbHlOYW1lPjxHaXZlbk5hbWU+RW1pbDwv
           R2l2ZW5OYW1lPjxJZGVudD48SWQ+MTcwODcwMDAxMzM8L0lkPjxUeXBlSWQgRE49IkbDuGRz
           ZWxzbnVtbWVyIiBTPSIyLjE2LjU3OC4xLjEyLjQuMS4xLjgxMTYiIFY9IkZOUiIvPjwvSWRl
           bnQ+PC9IZWFsdGhjYXJlUHJvZmVzc2lvbmFsPjwvT3JnYW5pc2F0aW9uPjwvU2VuZGVyPjxS
           ZWNlaXZlcj48T3JnYW5pc2F0aW9uPjxPcmdhbmlzYXRpb25OYW1lPk5BViBBcmJlaWRzb2cg
           dmVsZmVyZHNkaXJla3RvcmF0ZXQ8L09yZ2FuaXNhdGlvbk5hbWU+PElkZW50PjxJZD45MDEy
           ODwvSWQ+PFR5cGVJZCBWPSJIRVIiIEROPSJIRVItaWQiIFM9IjIuMTYuNTc4LjEuMTIuNC4x
           LjEuOTA1MSIvPjwvSWRlbnQ+PE9yZ2FuaXNhdGlvbj48T3JnYW5pc2F0aW9uTmFtZT5TYW1o
           YW5kbGluZyBBcmJlaWRzLSBvZyB2ZWxmZXJkc2V0YXRlbjwvT3JnYW5pc2F0aW9uTmFtZT48
           SWRlbnQ+PElkPjc5NzY4PC9JZD48VHlwZUlkIFY9IkhFUiIgRE49IkhFUi1pZCIgUz0iMi4x
           Ni41NzguMS4xMi40LjEuMS45MDUxIi8+PC9JZGVudD48L09yZ2FuaXNhdGlvbj48L09yZ2Fu
           aXNhdGlvbj48L1JlY2VpdmVyPjwvTXNnSW5mbz48RG9jdW1lbnQ+PFJlZkRvYz48TXNnVHlw
           ZSBETj0iSGVudCBwYXNpZW50bGlzdGUiIFY9IkhlbnRQYXNpZW50bGlzdGUiLz48Q29udGVu
           dD48ZXAyOlBhc2llbnRsaXN0ZUZvcmVzcG9yc2VsIHhtbG5zOmVwMj0iaHR0cDovL3d3dy5r
           aXRoLm5vL3htbHN0ZHMvbmF2L3Bhc2llbnRsaXN0ZS8yMDEwLTAyLTAxIj48ZXAyOkhlbnRQ
           YXNpZW50bGlzdGU+PGVwMjpGbnJMZWdlPjE3MDg3MDAwMTMzPC9lcDI6Rm5yTGVnZT48ZXAy
           OktvbW11bmVOcj4wMzAxPC9lcDI6S29tbXVuZU5yPjxlcDI6Rm9ybWF0IEROPSJQYXNpZW50
           SW5mb3JtYXNqb24iIFY9IlBJIi8+PC9lcDI6SGVudFBhc2llbnRsaXN0ZT48L2VwMjpQYXNp
           ZW50bGlzdGVGb3Jlc3BvcnNlbD48L0NvbnRlbnQ+PC9SZWZEb2M+PC9Eb2N1bWVudD48ZHNp
           ZzpTaWduYXR1cmUgeG1sbnM6ZHNpZz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxk
           c2lnIyI+PGRzaWc6U2lnbmVkSW5mbz48ZHNpZzpDYW5vbmljYWxpemF0aW9uTWV0aG9kIEFs
           Z29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvVFIvMjAwMS9SRUMteG1sLWMxNG4tMjAwMTAz
           MTUiPjwvZHNpZzpDYW5vbmljYWxpemF0aW9uTWV0aG9kPjxkc2lnOlNpZ25hdHVyZU1ldGhv
           ZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZHNpZy1tb3JlI3Jz
           YS1zaGEyNTYiPjwvZHNpZzpTaWduYXR1cmVNZXRob2Q+PGRzaWc6UmVmZXJlbmNlIFVSST0i
           Ij48ZHNpZzpUcmFuc2Zvcm1zPjxkc2lnOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93
           d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIj48L2RzaWc6
           VHJhbnNmb3JtPjxkc2lnOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3Jn
           L1RSLzIwMDEvUkVDLXhtbC1jMTRuLTIwMDEwMzE1Ij48L2RzaWc6VHJhbnNmb3JtPjwvZHNp
           ZzpUcmFuc2Zvcm1zPjxkc2lnOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cu
           dzMub3JnLzIwMDEvMDQveG1sZW5jI3NoYTI1NiI+PC9kc2lnOkRpZ2VzdE1ldGhvZD48ZHNp
           ZzpEaWdlc3RWYWx1ZT5ZY05jQW5UZUhOTEt2VTM4L2F1ckNVcHB5djliTGhhdGZyb1Y0T29k
           dFA0PTwvZHNpZzpEaWdlc3RWYWx1ZT48L2RzaWc6UmVmZXJlbmNlPjwvZHNpZzpTaWduZWRJ
           bmZvPjxkc2lnOlNpZ25hdHVyZVZhbHVlPlJ1Y1FuR2JOZjJwUlREbkRCaFJOK1M0Nm9DajFu
           VWVkRGtWZ1FzV1JiWC83cGp3UytCL1JwcmNHcUllQU1xRURPSlc5Q0pKRg0KTTU4ZXhRL3Mv
           S2UxcVArM0Rocnc1SFp0TGM0c3BCYmxmS2s0aEVOQ3BoSUNjRnNSVHY1QXc3aFZxay9Wc01C
           UjJZZlFhaHdmDQpKalRaNzVTSXJtWWJHM2R6eSt4NjBGT3FONzFuUEZGOStHaTEwRG40T3lt
           aDFXMHR0dHRCZWdrdW1obng1TVEyZEQrTWtYR2UNCit5SXFmYlprNEFEeVJ4Qm5vK1FmT2pn
           cG1QaFlSaUhDRTJ5MmVXZ0xvUzN5eFpLcHVuMStPSzQvYVp5bXRCeGZUTGxHNGRsSw0Kd25M
           K1Fia2p3UUhxc3lpeU10TVMvRnZFQURiNjRtY1dpN0dYUmUyc3hla0JFaFpvTmg4SExRPT08
           L2RzaWc6U2lnbmF0dXJlVmFsdWU+PGRzaWc6S2V5SW5mbz48ZHNpZzpYNTA5RGF0YT48ZHNp
           ZzpYNTA5Q2VydGlmaWNhdGU+TUlJR1dEQ0NCRUNnQXdJQkFnSUxCQkF6aENCSVpKdkhicFl3
           RFFZSktvWklodmNOQVFFTEJRQXdiREVMTUFrR0ExVUVCaE1DVGs4eEdEQVdCZ05WQkdFTUQw
           NVVVazVQTFRrNE16RTJNek15TnpFVE1CRUdBMVVFQ2d3S1FuVjVjR0Z6Y3lCQlV6RXVNQ3dH
           QTFVRUF3d2xRblY1Y0dGemN5QkRiR0Z6Y3lBeklGUmxjM1EwSUVOQklFY3lJRWhVSUZCbGNu
           TnZiakFlRncweU16RXdNekV4TVRVM01qVmFGdzB5TmpFd016RXlNalU1TURCYU1Ha3hDekFK
           QmdOVkJBWVRBazVQTVE0d0RBWURWUVFFREFWVVQxTkxRVEVSTUE4R0ExVUVLZ3dJUlUxSlRD
           QlhSVUl4RXpBUkJnTlZCQU1NQ2tWTlNVd2dWRTlUUzBFeElqQWdCZ05WQkFVVEdWVk9PazVQ
           TFRrMU56Z3ROREExTUMweE1EVXpNekUzT1Rjd2dnRWlNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0
           SUJEd0F3Z2dFS0FvSUJBUUN0ZmRpVkFEa1JSRkcySno1T1dyMW4vamg1NG11a2Uvcm9UOXJU
           Mzl0YkRPWHZPQm5vQXNxRGVwV1hKZGZWWDlxNTljZitlSndiRHMrSHcrNGljT3JJdk04T0o4
           YU9PVXdJNmNlRFVUWGdIUjJ6OHpnMFVqMmFXN0dxY0tDbWpjbUFzazNIWXpaZ1Z0SGRIdmZr
           dUg2ZXA0N24yd3UxcW5lRTNCNkdWcGF6OHZyS29UYkNzdzJaMHlhaGZZR0xNUnlEK3dPYVM3
           WHVyZlhOMTJjMWkvd1hEV09FS2hjUHU3MStKdnRPdUJPZStjK3REalRsMnNLaVppNW0yTjFm
           NituMStwVncvSkNIMlBSa3BqeUtyQklJNm14d0tEbHF5NWYya1A3MzNmRXZFWEFrWnErcTdU
           Q0RpczQxOHN1STJsMWRsK2hLNG12bUpYaTNVQVVLbkVjRkFnTUJBQUdqZ2dIOE1JSUIrREFK
           QmdOVkhSTUVBakFBTUI4R0ExVWRJd1FZTUJhQUZHQXRHRTEvS3VOWmZtY2dJUHlmRTVlSDNu
           TTFNQjBHQTFVZERnUVdCQlRETS9rbWgzZ0tZVklsdkFlRFFSTkhEVW0yOURBT0JnTlZIUThC
           QWY4RUJBTUNCa0F3SUFZRFZSMGdCQmt3RnpBS0JnaGdoRUlCR2dFREFUQUpCZ2NFQUl2c1FB
           RUFNRUVHQTFVZEh3UTZNRGd3TnFBMG9ES0dNR2gwZEhBNkx5OWpjbXd1ZEdWemREUXVZblY1
           Y0dGemMyTmhMbU52YlM5Q1VFTnNNME5oUnpKSVZGQlRMbU55YkRCN0JnZ3JCZ0VGQlFjQkFR
           UnZNRzB3TFFZSUt3WUJCUVVITUFHR0lXaDBkSEE2THk5dlkzTndjSE11ZEdWemREUXVZblY1
           Y0dGemMyTmhMbU52YlRBOEJnZ3JCZ0VGQlFjd0FvWXdhSFIwY0RvdkwyTnlkQzUwWlhOME5D
           NWlkWGx3WVhOelkyRXVZMjl0TDBKUVEyd3pRMkZITWtoVVVGTXVZMlZ5TUlHNEJnZ3JCZ0VG
           QlFjQkF3U0JxekNCcURCT0JnZ3JCZ0VGQlFjTEFqQkNCZ2NFQUl2c1NRRUJNRGVHTldoMGRI
           QnpPaTh2ZDNkM0xtNXJiMjB1Ym04dlpXNW5iR2x6YUM5dVlXMWxVbVZuYVhOMGNtRjBhVzl1
           UVhWMGFHOXlhWFI1TUFnR0JnUUFqa1lCQVRBVEJnWUVBSTVHQVFZd0NRWUhCQUNPUmdFR0FU
           QTNCZ1lFQUk1R0FRVXdMVEFyRmlWb2RIUndjem92TDNkM2R5NWlkWGx3WVhOekxtNXZMM0Jr
           Y3k5d1pITmZaVzR1Y0dSbUV3SmxiakFOQmdrcWhraUc5dzBCQVFzRkFBT0NBZ0VBdkZoOHdH
           OStkOTRYRDVSS2dMaEo0RS9MVWo0U1FvTW1mN2xnTWNOSkhCdmVkeVFtTjFndDhHK3MyNm9G
           ZGkyMHFtZklNR05kcWVRR1g3M1NNYXgwaW1XM2YxTWplQ1lEZDVXU3N1eVBPTWhYNWZuM2k2
           cVJEWS9zQ2t6bk9EYmpiZXJyaUZ0VysyQksvRzY3UGJrK0I4SGUwUDF0Q05NUnBlcFZFL1hD
           NHNRY0t4c0dMdFROWVVsNkZ4Y0pNZEo5UWZFZlRDamhlVXRHdXlVM3pzTS8xUzdNOFAzSHg5
           Y2dzRy9QeXdxSjM4cTRDd0tlRFA0U0lwQXVHUW9PdlVlTE9JNWs1REhESUZaZUxYTTJWb3Bw
           Q0NYMVZJOHR2Ykt6ZUhlMU1EdXJvZVY3WnIrSTZJclM1WGVXbmFJQU8vRlJ0ZGkxQ003SlEx
           YThkRnVJbnk0KzF3Z0FKb3VCRmFzNmQ3c3RiY1kwRTR0c0VjZjhML1hEamgxK0V1SUd3dDhh
           bkhBOG45WnFHQ2w2ZXZGeEN2ZzA3U2JFNEZnU3ZScnZKNHZGejNxMmg5UzlINEtCdWZEYkNr
           SjJVaFpEYkhPcDJOQ0pKK05UMVBIOXlMem9Wd0lVNGpLY1pzMTJneGtjQVRDR0FDUWZpU21q
           cTB0cXpNMGVyZkVwVUZFUm15WnlXZEhOc2dPQm1Ec0hPcUZ0N2RNenRab2paQXpEdjllazB4
           ejdsZHNEcWxLcmo0eEdIcnY5aTc3Ukc1bU5HZmRjclhEVzJaa1ltV3RFMHhjR3M2dHBSbjVn
           T2NpTTdpMk5OckIrM1ZjNGpxNDBhL2RxOXlsQjduS3NkTHRES0lUQXR6NGJCZDI2Z1c1MUhY
           M0NBYWJqYWZlVlQ0VXo3eS9xNFBtSGlMSEhrbTA9PC9kc2lnOlg1MDlDZXJ0aWZpY2F0ZT48
           L2RzaWc6WDUwOURhdGE+PC9kc2lnOktleUluZm8+PC9kc2lnOlNpZ25hdHVyZT48L01zZ0hl
           YWQ+PCEtLSBWZXJzaW9uOiAyLjgwLjI0MDkwMyBVc2VyOiBNTC1ERVYtR1NFIC0gTWFjaGlu
           ZTogTUwtVVRWLUdTRS0wMDEgLSBJUDogMTAuNzAuNjkuMjIwIC0tPg0K
        """.trimIndent().replace("\n", "")

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
          "payload": {
            "base64Content": "$rawBase64Payload"
          }
        }
        """.trimIndent()

        val requestDto = Json.decodeFromString<EbxmlRequest>(requestJson)
        val useCase = SendEbxmlMessageUseCase(testConfig, httpClient)

        val result = useCase.sendEbxmlMessage(requestDto)

        result.shouldBeInstanceOf<EbxmlResult.Success>()
    }
})
