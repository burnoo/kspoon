package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class HtmlDecoderNullValueTest {

    @Test
    fun shouldGetNullNotFoundSelector() {
        @Serializable
        data class Model(
            @Selector("p")
            val text: String?,
        )

        val body = ""
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model(null)
    }

    @Test
    fun shouldGetNullOnMissingIndex() {
        @Serializable
        data class Model(
            @Selector("p", index = 1)
            val text: String?,
        )

        val body = "<p>text</p>"
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model(null)
    }
}
