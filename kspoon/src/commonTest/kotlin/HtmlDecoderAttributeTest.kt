package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class HtmlDecoderAttributeTest {

    @Test
    fun shouldParseAttribute() {
        @Serializable
        data class Model(
            @Selector("a", attr = "href")
            val url: String,
        )

        val body = """<a href="https://github.com/burnoo">Click here</a>"""
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model("https://github.com/burnoo")
    }

    @Test
    fun shouldParseOuterHtml() {
        @Serializable
        data class Model(
            @Selector("a", attr = "outerHtml")
            val link: String,
        )

        val body = """<a href="https://github.com/burnoo">Click <span>here</span></a>"""
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model("""<a href="https://github.com/burnoo">Click <span>here</span></a>""")
    }

    @Test
    fun shouldSupportInnerHtml() {
        @Serializable
        data class Model(
            @Selector("a", attr = "innerHtml")
            val link: String,
        )

        val body = """<a href="https://github.com/burnoo">Click <span>here</span></a>"""
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model("""Click <span>here</span>""")
    }

    @Test
    fun shouldParseTextWithRegex() {
        @Serializable
        data class Model(
            @Selector("a", attr = "text", regex = "Cli[a-z][a-z]")
            val text: String,
        )

        val body = """<a href="https://github.com/burnoo">Click <span>here</span></a>"""
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model("Click")
    }
}
