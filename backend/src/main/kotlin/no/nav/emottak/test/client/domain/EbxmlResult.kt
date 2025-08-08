package no.nav.emottak.test.client.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class EbxmlResult {
    @Serializable
    @SerialName("Success")
    data class Success(val message: String, val outboundXml: String) : EbxmlResult()

    @Serializable
    @SerialName("Failure")
    data class Failure(val error: String, val statusCode: Int? = null, val outboundXml: String? = null) : EbxmlResult()
}
