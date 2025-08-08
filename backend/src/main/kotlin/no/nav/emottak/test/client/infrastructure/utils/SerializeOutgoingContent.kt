package no.nav.emottak.test.client.infrastructure.utils

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.utils.io.ByteChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.charset.Charset

suspend fun MultiPartFormDataContent.asXmlString(charset: Charset = Charsets.UTF_8): String {
    val channel = ByteChannel(autoFlush = true)
    val writer = CoroutineScope(Dispatchers.Unconfined).launch { writeTo(channel) }
    val buffer = ByteArray(8192)
    val out = java.io.ByteArrayOutputStream()
    while (!channel.isClosedForRead) {
        val read = channel.readAvailable(buffer, 0, buffer.size)
        if (read > 0) out.write(buffer, 0, read)
        if (read == -1) break
    }
    writer.join()
    return out.toByteArray().toString(charset)
}