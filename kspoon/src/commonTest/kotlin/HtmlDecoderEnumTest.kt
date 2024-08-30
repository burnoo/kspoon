package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test

@Serializable
enum class Enum { @SerialName("a") A, @SerialName("b") B, @SerialName("c") C }

class HtmlDecoderEnumTest {

    @Test
    fun shouldParseEnum() {
        @Serializable
        data class Model(
            @Selector("p")
            val enum: Enum,
        )

        val body = "<p>b</p>"
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model(Enum.B)
    }
}
