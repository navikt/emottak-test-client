package no.nav.emottak.test.client.application.ebxml.usecases.builders

import no.nav.emottak.test.client.domain.Payload
import java.util.Base64

class PayloadBuilder {
    fun buildPayload(ebxmlPayload: no.nav.emottak.test.client.application.ebxml.usecases.EbxmlPayload): Payload {
        val payloadAsBytes: ByteArray = Base64.getDecoder().decode(ebxmlPayload.base64Content.trim())
        val contentId = if (ebxmlPayload.contentId.startsWith("cid:")) {
            ebxmlPayload.contentId
        } else {
            "cid:${ebxmlPayload.contentId}"
        }

        println(contentId)
        println(payloadAsBytes.size)
        return Payload(
            bytes = payloadAsBytes,
            contentType = "application/xml",
            contentId = contentId
        )
    }
}
