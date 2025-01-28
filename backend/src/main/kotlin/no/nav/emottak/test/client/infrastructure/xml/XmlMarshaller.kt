package no.nav.emottak.test.client.infrastructure.xml

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.stream.XMLInputFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Document
import org.xmlsoap.schemas.soap.envelope.Envelope

val xmlMarshaller = XmlMarshaller()

class XmlMarshaller {

    companion object {
        private val jaxbContext = JAXBContext.newInstance(
            org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.ObjectFactory::class.java,
            org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.ObjectFactory::class.java,
            org.xmlsoap.schemas.soap.envelope.ObjectFactory::class.java,
            org.w3._1999.xlink.ObjectFactory::class.java,
            org.w3._2009.xmldsig11_.ObjectFactory::class.java,
            no.kith.xmlstds.msghead._2006_05_24.ObjectFactory::class.java,
            no.nav.tjeneste.ekstern.frikort.v1.types.ObjectFactory::class.java
        )

        private val marshaller = jaxbContext.createMarshaller()
        private val unmarshaller = jaxbContext.createUnmarshaller()
        private val marshlingMonitor = Any()
        private val unmarshlingMonitor = Any()
    }

    fun marshal(objekt: Any): String {
        val writer = StringWriter()
        synchronized(marshlingMonitor) {
            marshaller.marshal(objekt, writer)
        }
        return writer.toString()
    }

    fun marshal(envelope: Envelope): Document {
        val out = ByteArrayOutputStream()
        synchronized(marshlingMonitor) {
            marshaller.marshal(envelope, out)
        }
        return getDocumentBuilder().parse(ByteArrayInputStream(out.toByteArray()))
    }

    fun documentToString(document: Document): String {
        val transformer = TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
            setOutputProperty(OutputKeys.INDENT, "no")
            setOutputProperty(OutputKeys.METHOD, "xml")
        }

        val output = StreamResult(StringWriter())
        val source = DOMSource(document)
        transformer.transform(source, output)
        return output.writer.toString().replace("&#13;", "").replace("\r\n", "").replace("\n", "")
    }

    fun <T> unmarshal(xml: String, clazz: Class<T>): T {
        val reader = XMLInputFactory.newInstance().createXMLStreamReader(xml.reader())
        return synchronized(unmarshlingMonitor) {
            unmarshaller.unmarshal(reader, clazz).value
        }
    }
}
