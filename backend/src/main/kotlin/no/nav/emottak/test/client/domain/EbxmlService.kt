package no.nav.emottak.test.client.domain

interface EbxmlService {
    suspend fun sendEbxmlRequest(): EbxmlResult
}