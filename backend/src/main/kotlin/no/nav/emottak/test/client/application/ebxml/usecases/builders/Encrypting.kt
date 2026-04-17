package no.nav.emottak.test.client.application.ebxml.usecases.builders

import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.cms.CMSAlgorithm
import org.bouncycastle.cms.CMSEnvelopedDataGenerator
import org.bouncycastle.cms.CMSException
import org.bouncycastle.cms.CMSProcessableByteArray
import org.bouncycastle.cms.CMSTypedData
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.ByteArrayInputStream
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64
import kotlin.jvm.javaClass

// Kopiert fra ebms-payload

class Encrypting {

    private val provider = BouncyCastleProvider()
    private val encryptionAlgorithm: ASN1ObjectIdentifier = CMSAlgorithm.DES_EDE3_CBC
    private val keysize: Int = 168

    fun encrypt(doc: ByteArray): ByteArray {
        return try {
            encrypt(doc, listOf(getNavEncryptionCertificate()))
        } catch (e: Exception) {
            throw RuntimeException("Feil ved kryptering av dokument.", e)
        }
    }

    fun getNavEncryptionCertificate(): X509Certificate {
        val encodedCertificate = javaClass.getResource("/keystore/NAV_default_crypt_cert.txt").readText()
        val decodedBytes = Base64.getDecoder().decode(encodedCertificate)
        return createX509Certificate(decodedBytes)
    }

    private fun encrypt(input: ByteArray, certificates: List<X509Certificate>): ByteArray {
        return try {
            val dataGenerator = CMSEnvelopedDataGenerator()
            certificates.forEach { certificate ->
                dataGenerator.addRecipientInfoGenerator(JceKeyTransRecipientInfoGenerator(certificate))
            }
            val content: CMSTypedData = CMSProcessableByteArray(input)
            val envelopedData = dataGenerator.generate(
                content,
                JceCMSContentEncryptorBuilder(encryptionAlgorithm, keysize).setProvider(BouncyCastleProvider()).build()
            )
            envelopedData.encoded
        } catch (e: CertificateEncodingException) {
            throw RuntimeException("Feil ved kryptering av dokument", e)
        } catch (e: CMSException) {
            throw RuntimeException("Feil ved kryptering av dokument", e)
        }
    }

    fun createX509Certificate(byteArray: ByteArray): X509Certificate {
        val cf = CertificateFactory.getInstance("X.509", provider)
        return try {
            cf.generateCertificate(ByteArrayInputStream(byteArray)) as X509Certificate
        } catch (e: CertificateException) {
            throw RuntimeException("Kunne ikke opprette X509Certificate fra ByteArray", e)
        }
    }
}
