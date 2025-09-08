package no.nav.emottak.test.client.application.ebxml.usecases.builders

import java.io.File
import java.io.FileInputStream
import no.nav.emottak.test.client.domain.Payload
import no.nav.emottak.test.client.infrastructure.xml.EbmsAttachmentURIDereferencer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
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
import javax.xml.crypto.dsig.XMLSignature
import javax.xml.crypto.dsig.XMLSignatureFactory
import javax.xml.crypto.dsig.dom.DOMSignContext
import javax.xml.crypto.dsig.keyinfo.KeyInfo
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory
import javax.xml.crypto.dsig.keyinfo.X509Data
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec
import javax.xml.crypto.dsig.spec.TransformParameterSpec

class DocumentSigner(keystoreBase64: String, keystorePath: String?, keystorePassword: CharArray, alias: String) {

    private val log = LoggerFactory.getLogger(DocumentSigner::class.java)
    private val signingKey: PrivateKey
    private val signingCertificate: X509Certificate
    private val factory: XMLSignatureFactory = XMLSignatureFactory.getInstance("DOM")
    private val digestAlgorithm = "http://www.w3.org/2001/04/xmlenc#sha256"
    private val canonicalizationMethod = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
    private val signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"

    init {
        Security.addProvider(BouncyCastleProvider())
        val keyStore = if (keystorePath?.isNotBlank()?:false && File(keystorePath).exists()) {
            KeyStore.getInstance("PKCS12").apply {
                load(
                    FileInputStream(keystorePath), keystorePassword
                )
            }
        } else {
            val keystoreData = decodeBase64KeyStore(keystoreBase64)
            KeyStore.getInstance("PKCS12").apply {
                load(keystoreData, keystorePassword)
            }
        }
        signingKey = keyStore.getKey(alias, keystorePassword) as PrivateKey
        signingCertificate = keyStore.getCertificate(alias) as X509Certificate

        log.info("Signing Certificate:")
        log.info("  Subject DN: ${signingCertificate.subjectDN}")
        log.info("  Issuer DN: ${signingCertificate.issuerDN}")
        log.info("  Serial Number: ${signingCertificate.serialNumber}")
        log.info("  Valid From: ${signingCertificate.notBefore}")
        log.info("  Valid To: ${signingCertificate.notAfter}")
        log.info("  Public Key: ${signingCertificate.publicKey}")
    }

    fun signerXML(document: Document): Document {
        val signature = buildXmlSignature(signingCertificate)
        signature.sign(DOMSignContext(signingKey, document.documentElement))
        log.info("Document is being signed")
        return document
    }

    private fun buildXmlSignature(signerCertificate: X509Certificate): XMLSignature {
        val keyInfoFactory = factory.keyInfoFactory
        val x509Content: MutableList<Any?> = ArrayList()
        x509Content.add(signerCertificate)
        val x509data = keyInfoFactory.newX509Data(x509Content)
        val keyInfo = keyInfoFactory.newKeyInfo(listOf(x509data))
        val signature = factory.newXMLSignature(createSignedInfo(), keyInfo)
        return signature
    }

    private fun createSignedInfo(): SignedInfo {
        return factory.newSignedInfo(
            factory.newCanonicalizationMethod(
                canonicalizationMethod,
                null as C14NMethodParameterSpec?
            ),
            factory.newSignatureMethod(signatureAlgorithm, null),
            listOf(
                factory.newReference(
                    "",
                    factory.newDigestMethod(digestAlgorithm, null),
                    listOf(factory.newTransform(Transform.ENVELOPED, null as TransformParameterSpec?)),
                    null,
                    null
                )
            )
        )
    }

    fun signDocument(document: Document, attachments: List<Payload>): Document {
        val keyInfo = createKeyInfo()
        log.info("Signing EBXML envelope")
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
