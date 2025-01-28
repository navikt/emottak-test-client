package no.nav.emottak.test.client.infrastructure.xml

import java.io.ByteArrayInputStream
import javax.xml.crypto.Data
import javax.xml.crypto.OctetStreamData
import javax.xml.crypto.URIDereferencer
import javax.xml.crypto.URIReference
import javax.xml.crypto.XMLCryptoContext
import no.nav.emottak.test.client.domain.Payload

private const val CID_PREFIX = "cid:"

class EbmsAttachmentURIDereferencer(
    private val attachments: List<Payload>,
    private val defaultResolver: URIDereferencer
) : URIDereferencer {
    override fun dereference(uriReference: URIReference, context: XMLCryptoContext): Data {
        val uri = uriReference.uri
        println("Dereferencing URI: ${uriReference.uri}")

        if (uri.isNullOrEmpty()) {
            println("Delegating empty Dereference URI to the default resolver")
            return defaultResolver.dereference(uriReference, context)
        }

        if (!uri.startsWith(CID_PREFIX)) {
            println("Delegating found a URI that is not supported. Must start with $CID_PREFIX, but was $uri")
            throw IllegalArgumentException("Dereference URI must start with $CID_PREFIX, but was: $uri")
        }

        val matchingAttachment = attachments.find { it.contentId == uri }
            ?: throw IllegalArgumentException("Dereference did not find any attachment with with uri: $uri")

        println("Dereference resolved attachment for URI: ${matchingAttachment.contentId}")
        return OctetStreamData(
            ByteArrayInputStream(matchingAttachment.bytes),
            matchingAttachment.contentId,
            matchingAttachment.contentType
        )
    }
}
