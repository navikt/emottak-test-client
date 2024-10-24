package no.nav.emottak.test.client.domain

sealed class EbxmlResult {
    data class Success(val message: String) : EbxmlResult()
    data class Failure(val error: String, val statusCode: Int? = null) : EbxmlResult()
}
