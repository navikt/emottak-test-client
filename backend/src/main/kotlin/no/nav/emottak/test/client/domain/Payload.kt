package no.nav.emottak.test.client.domain

data class Payload(
    val bytes: ByteArray,
    val contentType: String,
    val contentId: String,
)