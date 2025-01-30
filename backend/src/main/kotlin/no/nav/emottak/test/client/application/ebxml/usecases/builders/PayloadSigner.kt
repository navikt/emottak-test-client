package no.nav.emottak.test.client.application.ebxml.usecases.builders

import no.nav.emottak.test.client.domain.Payload
import no.nav.emottak.test.client.infrastructure.xml.EbmsAttachmentURIDereferencer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.w3c.dom.Document
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.X509Certificate
import java.util.Base64
import javax.xml.crypto.XMLStructure
import javax.xml.crypto.dom.DOMStructure
import javax.xml.crypto.dsig.SignedInfo
import javax.xml.crypto.dsig.Transform
import javax.xml.crypto.dsig.XMLSignatureFactory
import javax.xml.crypto.dsig.dom.DOMSignContext
import javax.xml.crypto.dsig.keyinfo.KeyInfo
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory
import javax.xml.crypto.dsig.keyinfo.X509Data
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec
import javax.xml.crypto.dsig.spec.TransformParameterSpec

class PayloadSigner(keystoreBase64: String, keystorePassword: CharArray, alias: String) {

    private val signingKey: PrivateKey
    private val signingCertificate: X509Certificate
    private val factory: XMLSignatureFactory = XMLSignatureFactory.getInstance("DOM")
    private val digestAlgorithm = "http://www.w3.org/2001/04/xmlenc#sha256"
    private val canonicalizationMethod = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
    private val signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"

    init {
        Security.addProvider(BouncyCastleProvider())
        val keystoreData = decodeBase64KeyStore(keystoreBase64)
        val keyStore = KeyStore.getInstance("PKCS12").apply {
            load(keystoreData, keystorePassword)
        }
        signingKey = keyStore.getKey(alias, keystorePassword) as PrivateKey
        signingCertificate = keyStore.getCertificate(alias) as X509Certificate

        println("Signing Certificate:")
        println("  Subject DN: ${signingCertificate.subjectDN}")
        println("  Issuer DN: ${signingCertificate.issuerDN}")
        println("  Serial Number: ${signingCertificate.serialNumber}")
        println("  Valid From: ${signingCertificate.notBefore}")
        println("  Valid To: ${signingCertificate.notAfter}")
        println("  Public Key: ${signingCertificate.publicKey}")
    }

    fun signDocument(document: Document, attachments: List<Payload>): Document {
        val keyInfo = createKeyInfo()
        val domSignContext = DOMSignContext(signingKey, document.documentElement)
        val defaultResolver = factory.uriDereferencer
        domSignContext.uriDereferencer = EbmsAttachmentURIDereferencer(attachments, defaultResolver)
        val xmlSignature = factory.newXMLSignature(createSignedInfo(document, attachments), keyInfo)
        xmlSignature.sign(domSignContext)
        return document
    }

    private fun createSignedInfo(document: Document, attachments: List<Payload>): SignedInfo {
        val canonicalizationMethod =
            factory.newCanonicalizationMethod(canonicalizationMethod, null as C14NMethodParameterSpec?)
        val signatureMethod = factory.newSignatureMethod(signatureAlgorithm, null)
        val xPathParams: XMLStructure = generateXPathParams(document)

        val rootReference = factory.newReference(
            "",
            factory.newDigestMethod(digestAlgorithm, null),
            listOf(
                factory.newTransform(Transform.ENVELOPED, null as TransformParameterSpec?),
                factory.newTransform(Transform.XPATH, xPathParams),
                factory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", null as TransformParameterSpec?)
            ),
            null,
            null
        )

        val attachmentReferences = attachments.map { payload ->
            factory.newReference(
                payload.contentId,
                factory.newDigestMethod(digestAlgorithm, null),
                null,
                null,
                null
            )
        }
        return factory.newSignedInfo(
            canonicalizationMethod,
            signatureMethod,
            listOf(rootReference) + attachmentReferences
        )
    }

    private fun generateXPathParams(document: Document): XMLStructure {
        val xpathExpression =
            "not(ancestor-or-self::node()[@SOAP-ENV:actor=\"urn:oasis:names:tc:ebxml-msg:actor:nextMSH\"] | ancestor-or-self::node()[@SOAP-ENV:actor=\"http://schemas.xmlsoap.org/soap/actor/next\"])"

        val xPathElement = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:XPath")
        xPathElement.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:SOAP-ENV",
            "http://schemas.xmlsoap.org/soap/envelope/"
        )
        xPathElement.appendChild(document.createTextNode(xpathExpression))

        val container = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:XPathContainer")
        container.appendChild(xPathElement)
        val xPathParams: XMLStructure = DOMStructure(container)
        return xPathParams
    }

    private fun decodeBase64KeyStore(encodedData: String) =
        Base64.getMimeDecoder().decode(encodedData.trim()).inputStream()

    private fun createKeyInfo(): KeyInfo {
        val keyInfoFactory: KeyInfoFactory = factory.keyInfoFactory
        val x509Content: MutableList<Any> = ArrayList()
        x509Content.add(signingCertificate)
        val x509Data: X509Data = keyInfoFactory.newX509Data(x509Content)
        return keyInfoFactory.newKeyInfo(listOf(x509Data))
    }
}
