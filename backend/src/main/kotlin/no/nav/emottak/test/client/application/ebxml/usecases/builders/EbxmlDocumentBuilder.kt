package no.nav.emottak.test.client.application.ebxml.usecases.builders

import no.nav.emottak.test.client.application.ebxml.usecases.EbxmlRequest
import no.nav.emottak.test.client.domain.Payload
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import no.nav.emottak.test.client.infrastructure.xml.asByteArray
import no.nav.emottak.test.client.infrastructure.xml.xmlMarshaller
import org.apache.xml.security.signature.XMLSignature
import org.apache.xml.security.utils.Constants
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.From
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.Manifest
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.MessageData
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.MessageHeader
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.PartyId
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.Reference
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.Service
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.SyncReply
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.To
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xmlsoap.schemas.soap.envelope.Body
import org.xmlsoap.schemas.soap.envelope.Envelope
import org.xmlsoap.schemas.soap.envelope.Header
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory

class EbxmlDocumentBuilder(private val applicationConfig: ApplicationConfig, private val requestDto: EbxmlRequest) {

    init {
        org.apache.xml.security.Init.init()
    }

    private val documentSigner = DocumentSigner(
        applicationConfig.signing.key,
        applicationConfig.signing.password.toCharArray(),
        applicationConfig.alias
    )

    fun ByteArray.asDocument(): Document {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        val docBuilder = factory.newDocumentBuilder()
        return docBuilder.parse(this.inputStream())
    }

    val payload: Payload? = requestDto.let {
        if (it.ebxmlPayload == null) return@let null
        println("Signing PAYLOAD")
        var payloadAsBytes: ByteArray = Base64.getDecoder().decode(it.ebxmlPayload.base64Content.trim())
        if (it.signPayload == true) {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            val docBuilder = factory.newDocumentBuilder()
            val document = docBuilder.parse(payloadAsBytes.inputStream())
            payloadAsBytes = documentSigner.signerXML(
                document
            ).asByteArray()
        }
        with(payloadAsBytes.asDocument().retrieveSignatureElement()) {
            checkSignatureValue(keyInfo.x509Certificate)
        }
        val contentId = if (it.ebxmlPayload.contentId.startsWith("cid:")) {
            it.ebxmlPayload.contentId
        } else {
            "cid:${it.ebxmlPayload.contentId}"
        }

        println(contentId)
        println(payloadAsBytes.size)
        return@let Payload(
            bytes = payloadAsBytes,
            contentType = "application/xml",
            contentId = contentId
        )
    }

    fun buildAndSign(): Document {
        val envelope = buildEnvelope()
        val document = xmlMarshaller.marshal(envelope)

        val canonicalizedXml = xmlMarshaller.documentToString(document)
        println("Canonicalized XML:\n$canonicalizedXml")

        val attachments = listOfNotNull(payload)
        val signedDocument = documentSigner.signDocument(document, attachments)
        insertSignatureIntoHeader(signedDocument)

        return signedDocument
    }

    fun Document.retrieveSignatureElement(): XMLSignature {
        val nodeList: NodeList = this.getElementsByTagNameNS(Constants.SignatureSpecNS, Constants._TAG_SIGNATURE)
        // Regel ID 45, 52
        if (nodeList.length != 1) throw RuntimeException("${nodeList.length} signaturer i dokumentet! Skal være nøyaktig 1")
        // Regel ID 363, 42, 32
        return XMLSignature(nodeList.item(0) as Element, Constants.SignatureSpecNS)
    }

    fun buildEnvelope(): Envelope {
        val hasPayload = requestDto.ebxmlPayload != null
        val payloadContentId = requestDto.ebxmlPayload?.contentId
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val parsedDate: Date = dateFormat.parse(requestDto.timestamp)

        return Envelope().apply {
            otherAttributes!![QName("xsi:schemaLocation")] =
                "http://schemas.xmlsoap.org/soap/envelope/ http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd"
            otherAttributes!![QName("xmlns:SOAP")] = "http://schemas.xmlsoap.org/soap/envelope/"
            otherAttributes!![QName("xmlns:eb")] =
                "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd"
            otherAttributes!![QName("xmlns:xlink")] = "http://www.w3.org/1999/xlink"
            otherAttributes!![QName("xmlns:xsi")] = "http://www.w3.org/2001/XMLSchema-instance"

            header = Header().apply {
                any.add(
                    MessageHeader().apply {
                        this.isMustUnderstand = true
                        this.version = "2.0"

                        from = From().apply {
                            partyId.add(
                                PartyId().apply {
                                    type = "HER"
                                    value = requestDto.fromPartyId
                                }
                            )
                            role = requestDto.fromRole
                        }
                        to = To().apply {
                            partyId.add(
                                PartyId().apply {
                                    type = "HER"
                                    value = requestDto.toPartyId
                                }
                            )
                            role = requestDto.toRole
                        }
                        cpaId = requestDto.cpaId
                        conversationId = requestDto.conversationId
                        service = Service().apply {
                            type = "string"
                            value = requestDto.service
                        }
                        action = requestDto.action
                        messageData = MessageData().apply {
                            messageId = requestDto.messageId
                            timestamp = parsedDate
                        }
                    }
                )

                any.add(
                    SyncReply().apply {
                        this.isMustUnderstand = true
                        this.actor = "http://schemas.xmlsoap.org/soap/actor/next"
                        version = "2.0"
                    }
                )
            }
            body = Body().apply {
                xmlMarshaller
                if (hasPayload) {
                    any.add(
                        Manifest().apply {
                            version = "2.0"
                            reference.add(
                                Reference().apply {
                                    type = "simple"
                                    href = "cid:$payloadContentId"
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    private fun insertSignatureIntoHeader(document: Document) {
        val headerNode = document.documentElement
            .getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
            .item(0)

        val signatureElement = document.documentElement
            .getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature")
            .item(0)

        if (headerNode != null && signatureElement != null) {
            headerNode.appendChild(signatureElement)
        }
    }

    fun marshalDocumentToXml(document: Document): String {
        val xmlText = xmlMarshaller.documentToString(document)
        return xmlText
    }

    fun marshalEnvelopeToXml(envelope: Envelope): String {
        val document = xmlMarshaller.marshal(envelope)
        return xmlMarshaller.documentToString(document)
    }
}
