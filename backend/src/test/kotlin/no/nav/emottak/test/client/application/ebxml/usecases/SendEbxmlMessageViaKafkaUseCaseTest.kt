package no.nav.emottak.test.client.application.ebxml.usecases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.emottak.test.client.domain.Payload

class SendEbxmlMessageViaKafkaUseCaseTest : FunSpec({

    test("createSoapWithAttachments includes payload as one attachment") {
        val envelope = "envelope".toByteArray()
        val payload = Payload(
            bytes = "payload".toByteArray(),
            contentId = "cid:test-payload",
            contentType = "application/xml"
        )

        val result = createSoapWithAttachments(envelope, payload)

        result.envelope.contentEquals(envelope) shouldBe true
        result.attachments.shouldHaveSize(1)
        result.attachments.first().content.contentEquals(payload.bytes) shouldBe true
        result.attachments.first().contentId shouldBe payload.contentId
        result.attachments.first().contentType shouldBe payload.contentType
    }

    test("createSoapWithAttachments uses empty attachments when payload is missing") {
        val envelope = "envelope".toByteArray()

        val result = createSoapWithAttachments(envelope, null)

        result.envelope.contentEquals(envelope) shouldBe true
        result.attachments shouldBe emptyList()
    }
})
