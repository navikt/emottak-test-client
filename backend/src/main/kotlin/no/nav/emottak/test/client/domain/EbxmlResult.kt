package no.nav.emottak.test.client.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class EbxmlResult {
    @Serializable
    data class Success(val message: String) : EbxmlResult()

    @Serializable
    data class Failure(val error: String, val statusCode: Int? = null) : EbxmlResult()
}
