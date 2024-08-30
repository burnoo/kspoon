package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonConfigurationTest {

    @Test
    fun shouldRespectDefaultTextMode() {
        @Serializable
        data class Model(
            @Selector("p")
            val text: String
        )
        val kspoon = Kspoon {
            defaultTextMode = HtmlTextMode.OuterHtml
        }

        val text = kspoon.decodeFromString<Model>("<p>text</p>")

        text shouldBe Model("<p>text</p>")
    }

    @Test
    fun shouldUseParseFunctions() {
        @Serializable
        data class Model(
            @Selector("a", attr = "abs:href")
            val url: String
        )
        val kspoon = Kspoon {
            parse = { parse(it, baseUri = "https://github.com") }
        }

        val text = kspoon.decodeFromString<Model>("""<a href="burnoo">Click</a>""")

        text shouldBe Model("https://github.com/burnoo")
    }
}
