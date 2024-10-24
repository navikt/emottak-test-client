package no.nav.emottak.test.client.adapters.ebxml

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.emottak.test.client.domain.EbxmlResult
import no.nav.emottak.test.client.domain.EbxmlService
import org.slf4j.LoggerFactory

class EbxmlFrikortServiceAdapter(private val client: HttpClient) : EbxmlService {

    private val log = LoggerFactory.getLogger(EbxmlFrikortServiceAdapter::class.java)

    override suspend fun sendEbxmlRequest(): EbxmlResult {
        val ebxmlXmlMessage = """
            <?xml version="1.0" encoding="UTF-8"?>
            <SOAP:Envelope xsi:schemaLocation="http://schemas.xmlsoap.org/soap/envelope/ http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" xmlns:SOAP="http://schemas.xmlsoap.org/soap/envelope/" xmlns:eb="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><SOAP:Header><eb:MessageHeader SOAP:mustUnderstand="1" eb:version="2.0"><eb:From><eb:PartyId eb:type="HER">8141253</eb:PartyId><eb:Role>Behandler</eb:Role></eb:From><eb:To><eb:PartyId eb:type="HER">79768</eb:PartyId><eb:Role>Frikortregister</eb:Role></eb:To><eb:CPAId>nav:qass:35065</eb:CPAId><eb:ConversationId>dbeeaae0-c5d7-4665-8322-f4a0edf03c71</eb:ConversationId><eb:Service eb:type="string">HarBorgerFrikort</eb:Service><eb:Action>EgenandelForesporsel</eb:Action><eb:MessageData><eb:MessageId>abd21c73-5032-476c-9891-de8d17ba03f2</eb:MessageId><eb:Timestamp>2024-03-13T15:08:43.4508874Z</eb:Timestamp></eb:MessageData></eb:MessageHeader><eb:SyncReply SOAP:actor="http://schemas.xmlsoap.org/soap/actor/next" SOAP:mustUnderstand="1" eb:version="2.0"> </eb:SyncReply><ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
              <ds:SignedInfo>
                <ds:CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
                <ds:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
                <ds:Reference URI="">
                  <ds:Transforms>
                    <ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
                    <ds:Transform Algorithm="http://www.w3.org/TR/1999/REC-xpath-19991116">
                      <ds:XPath xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">not(ancestor-or-self::node()[@SOAP-ENV:actor="urn:oasis:names:tc:ebxml-msg:actor:nextMSH"] | ancestor-or-self::node()[@SOAP-ENV:actor="http://schemas.xmlsoap.org/soap/actor/next"])
                    </ds:XPath></ds:Transform>
                    <ds:Transform Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
                  </ds:Transforms>
                  <ds:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
                  <ds:DigestValue>ABuxNSBUUvsqvUMM4Dh0C2maYIbdiZUzazEAV42LPDk=</ds:DigestValue>
                </ds:Reference>
                <ds:Reference URI="cid:MZ8R25SYIMU4.E2IQHRBKTNX43@laptop-3f6plfro">
                  <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
                  <ds:DigestValue>2M1mHJpSCYTYe2ByhR5Z5VBSkZc=</ds:DigestValue>
                </ds:Reference>
              </ds:SignedInfo>
              <ds:SignatureValue>eyyyPJJd285ayyR+0EBLLaGWgLRG/F0uAEShioezjogpx63iYk/DcAJKSVEDQAmB31tjFBw+qzxMGKgucfW0+dhEhaSZ3qLdqDO2kDbOUUN1odU/j6Rh7/mRnFgRXzkyZdV2FqA9rBgXRYJ4ycljK3FXQhqJPtk67NOdRf/TM9/VXj+d0bbT4cezEO5PIzOJsvla1KGTOTh2E0jrKs8B5xOx2O+N+AHvSQn5bm00PdLSBx0KljGHaV9art1/pOCpAjzZZUuNHcPgp3N+lI8OspkBKFUOhAr9FZBzFckLxZ/d5AIua1rZE5UBtt6JoQcfRQXUtXW97EUhcZdlY1FZ0R7dRw302DkLZRV0ZbMo7Fgmwu6nVRrKDu+ZMq3fJyILopHKD2y6jHNVZsuSQ+pLnI6szyVXuZfRfb8yauz1iFieEhm/5ygYZWQ/XtqUIixv76x9xnM3IH9Giw/GVzC7F0JOtlxYN0heggUOQSuXlaKH2ZnFLqGPjfUCGmvl5/IZ</ds:SignatureValue>
              <ds:KeyInfo>
                <ds:X509Data>
                  <ds:X509Certificate>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</ds:X509Certificate>
                </ds:X509Data>
              </ds:KeyInfo>
            </ds:Signature></SOAP:Header><SOAP:Body><eb:Manifest eb:version="2.0"><eb:Reference xlink:type="simple" xlink:href="cid:MZ8R25SYIMU4.E2IQHRBKTNX43@laptop-3f6plfro"/></eb:Manifest></SOAP:Body></SOAP:Envelope>
        """.trimIndent()

        val payloadBase64Content = """
            PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjxuczpNc2dIZWFkIHhtbG5z
            OnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIHhtbG5zOnJh
            d1hzZD0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEiIHhzaTpzY2hlbWFMb2NhdGlv
            bj0iaHR0cDovL3d3dy5raXRoLm5vL3htbHN0ZHMvbXNnaGVhZC8yMDA2LTA1LTI0IE1zZ0hlYWQt
            djFfMi5yYXdYc2QiIHhtbG5zOm5zPSJodHRwOi8vd3d3LmtpdGgubm8veG1sc3Rkcy9tc2doZWFk
            LzIwMDYtMDUtMjQiPg0KICAgIDxuczpNc2dJbmZvPg0KICAgICAgICA8bnM6VHlwZSBWPSJFZ2Vu
            YW5kZWxGb3Jlc3BvcnNlbCIgRE49IkZvcmVzcMO4cnNlbCBvbSBlZ2VuYW5kZWwiLz4NCiAgICAg
            ICAgPG5zOk1JR3ZlcnNpb24+djEuMiAyMDA2LTA1LTI0PC9uczpNSUd2ZXJzaW9uPg0KICAgICAg
            ICA8bnM6R2VuRGF0ZT4yMDI0LTAzLTEzVDE2OjA4OjQzPC9uczpHZW5EYXRlPg0KICAgICAgICA8
            bnM6TXNnSWQ+M2I5MzhjNzEtMGNjZi00YWNlLTg2ZjEtN2EyYWFlZmE2ZjJiPC9uczpNc2dJZD4N
            CiAgICAgICAgPG5zOlNlbmRlcj4NCiAgICAgICAgICAgIDxuczpPcmdhbmlzYXRpb24+DQogICAg
            ICAgICAgICAgICAgPG5zOk9yZ2FuaXNhdGlvbk5hbWU+U3BlYXJlIEFTPC9uczpPcmdhbmlzYXRp
            b25OYW1lPg0KICAgICAgICAgICAgICAgIDxuczpJZGVudD4NCiAgICAgICAgICAgICAgICAgICAg
            PG5zOklkPjgxNDEyNTM8L25zOklkPg0KICAgICAgICAgICAgICAgICAgICA8bnM6VHlwZUlkIFY9
            IkhFUiIgRE49IkhFUi1pZCIgUz0iMi4xNi41NzguMS4xMi40LjEuMS45MDUxIi8+DQogICAgICAg
            ICAgICAgICAgPC9uczpJZGVudD4NCiAgICAgICAgICAgICAgICA8bnM6SWRlbnQ+DQogICAgICAg
            ICAgICAgICAgICAgIDxuczpJZD45OTM5NTQ4OTY8L25zOklkPg0KICAgICAgICAgICAgICAgICAg
            ICA8bnM6VHlwZUlkIFY9IkVOSCIgRE49Ik9yZ2FuaXNhc2pvbnNudW1tZXJldCBpIEVuaGV0c3Jl
            Z2lzdGVyIiBTPSIyLjE2LjU3OC4xLjEyLjQuMS4xLjkwNTEiLz4NCiAgICAgICAgICAgICAgICA8
            L25zOklkZW50Pg0KICAgICAgICAgICAgPC9uczpPcmdhbmlzYXRpb24+DQogICAgICAgIDwvbnM6
            U2VuZGVyPg0KICAgICAgICA8bnM6UmVjZWl2ZXI+DQogICAgICAgICAgICA8bnM6T3JnYW5pc2F0
            aW9uPg0KICAgICAgICAgICAgICAgIDxuczpPcmdhbmlzYXRpb25OYW1lPk5BVjwvbnM6T3JnYW5p
            c2F0aW9uTmFtZT4NCiAgICAgICAgICAgICAgICA8bnM6SWRlbnQ+DQogICAgICAgICAgICAgICAg
            ICAgIDxuczpJZD43OTc2ODwvbnM6SWQ+DQogICAgICAgICAgICAgICAgICAgIDxuczpUeXBlSWQg
            Vj0iSEVSIiBETj0iSEVSLWlkIiBTPSIyLjE2LjU3OC4xLjEyLjQuMS4xLjkwNTEiLz4NCiAgICAg
            ICAgICAgICAgICA8L25zOklkZW50Pg0KICAgICAgICAgICAgICAgIDxuczpJZGVudD4NCiAgICAg
            ICAgICAgICAgICAgICAgPG5zOklkPjg4OTY0MDc4MjwvbnM6SWQ+DQogICAgICAgICAgICAgICAg
            ICAgIDxuczpUeXBlSWQgVj0iRU5IIiBETj0iT3JnYW5pc2Fzam9uc251bW1lcmV0IGkgRW5oZXRz
            cmVnaXN0ZXIiIFM9IjIuMTYuNTc4LjEuMTIuNC4xLjEuOTA1MSIvPg0KICAgICAgICAgICAgICAg
            IDwvbnM6SWRlbnQ+DQogICAgICAgICAgICA8L25zOk9yZ2FuaXNhdGlvbj4NCiAgICAgICAgPC9u
            czpSZWNlaXZlcj4NCiAgICA8L25zOk1zZ0luZm8+DQogICAgPG5zOkRvY3VtZW50Pg0KICAgICAg
            ICA8bnM6RG9jdW1lbnRDb25uZWN0aW9uIFY9IkgiIEROPSJIb3ZlZGRva3VtZW50Ii8+DQogICAg
            ICAgIDxuczpSZWZEb2M+DQogICAgICAgICAgICA8bnM6TXNnVHlwZSBWPSJYTUwiIEROPSJYTUwt
            aW5zdGFucyIvPg0KICAgICAgICAgICAgPG5zOk1pbWVUeXBlPnRleHQvcmF3WG1sPC9uczpNaW1l
            VHlwZT4NCiAgICAgICAgICAgIDxuczpEZXNjcmlwdGlvbj5FZ2VuYW5kZWxGb3Jlc3BvcnNlbDwv
            bnM6RGVzY3JpcHRpb24+DQogICAgICAgICAgICA8bnM6Q29udGVudD4NCiAgICAgICAgICAgICAg
            ICA8RWdlbmFuZGVsRm9yZXNwb3JzZWxWMiB4bWxucz0iaHR0cDovL3d3dy5raXRoLm5vL3htbHN0
            ZHMvbmF2L2VnZW5hbmRlbC8yMDE2LTA2LTEwIj4NCiAgICAgICAgICAgICAgICAgICAgPEhhckJv
            cmdlckZyaWtvcnQ+DQogICAgICAgICAgICAgICAgICAgICAgICA8Qm9yZ2VyRm5yPjA1MDU4NzA1
            MDY1PC9Cb3JnZXJGbnI+DQogICAgICAgICAgICAgICAgICAgICAgICA8RGF0bz4yMDIyLTEyLTAx
            PC9EYXRvPg0KICAgICAgICAgICAgICAgICAgICAgICAgPFRqZW5lc3RldHlwZUtvZGU+UFM8L1Rq
            ZW5lc3RldHlwZUtvZGU+DQogICAgICAgICAgICAgICAgICAgIDwvSGFyQm9yZ2VyRnJpa29ydD4N
            CiAgICAgICAgICAgICAgICA8L0VnZW5hbmRlbEZvcmVzcG9yc2VsVjI+DQogICAgICAgICAgICA8
            L25zOkNvbnRlbnQ+DQogICAgICAgIDwvbnM6UmVmRG9jPg0KICAgIDwvbnM6RG9jdW1lbnQ+DQo8
            L25zOk1zZ0hlYWQ+DQo=
        """.trimIndent()


        val boundary = "47437c36-e6ed-47a9-8bc8-40c12fc2abe3"

        val contentType = """
                multipart/related; type="text/xml"; boundary="$boundary"; start="<N51645SYIMU4.Y3FNJBIL1H5K@speare.no>; charset=utf-8"
            """.trimIndent()


        return try {

            val partData: List<PartData> = listOf(
                PartData.FormItem(ebxmlXmlMessage, {}, Headers.build {
                    append("Content-Type", "text/xml; charset=UTF-8")
                    append("Content-Id", "<N51645SYIMU4.Y3FNJBIL1H5K@speare.no>")
                    append("Content-Transfer-Encoding", "binary")
                }),
                PartData.FormItem(payloadBase64Content, {}, Headers.build {
                    append("Content-Type", "text/xml; charset=utf-8")
                    append("Content-Id", "<MZ8R25SYIMU4.E2IQHRBKTNX43@laptop-3f6plfro>")
                    append("Content-Transfer-Encoding", "base64")
                })
            )

            val url = "https://ebms-sync-router.dev.intern.nav.no/ebxml/msh"

            val response = withContext(Dispatchers.IO) {
                client.post(url) {
                    headers {
                        append("Content-Type", contentType)
                        append("SOAPAction", "ebXML")
                        append("MIME-Version", "1.0")
                        append("X_SEND_TO", "ny")
                        append("Message-Id", "1234567123")
                        append("Accept", "*/*")
                    }
                    setBody(
                        MultiPartFormDataContent(
                            partData,
                            boundary,
                            ContentType.parse(contentType)
                        )
                    )
                }
            }


//            log.info("Response Status: ${response.status}")
//            log.info("Response Headers: ${response.headers}")

            val responseBody = response.bodyAsText()
//            log.info("Response Body: $responseBody")

            if (response.status == HttpStatusCode.OK) {
                EbxmlResult.Success(responseBody)
            } else {
                log.error("Failed request with status: ${response.status}")
                EbxmlResult.Failure(
                    "Unexpected status code: ${response.status} and response: ${response.bodyAsText()}",
                    response.status.value
                )
            }

        } catch (e: Exception) {
            log.error("Error while sending ebXML request: ${e.message}", e)
            EbxmlResult.Failure("Exception: ${e.message}")
        }
    }
}
