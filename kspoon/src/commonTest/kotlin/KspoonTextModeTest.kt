package dev.burnoo.ksoup

import dev.burnoo.ksoup.type.DataHtmlString
import dev.burnoo.ksoup.type.InnerHtmlString
import dev.burnoo.ksoup.type.OuterHtmlString
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonTextModeTest {

    @Test
    fun shouldParseTextByDefault() {
        @Serializable
        data class Model(
            @Selector("p")
            val text: String,
        )

        val body = "<div><p><span>text</span></p></div>"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("text")
    }

    @Test
    fun shouldParseOuterHtml() {
        @Serializable
        data class Model(
            @Selector("p")
            val outerHtml: OuterHtmlString,
        )

        val body = "<div><p><span>text</span></p></div>"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("<p><span>text</span></p>")
    }

    @Test
    fun shouldParseInnerHtml() {
        @Serializable
        data class Model(
            @Selector("p")
            val innerHtml: InnerHtmlString,
        )

        val body = "<div><p><span>text</span></p></div>"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("<span>text</span>")
    }

    @Test
    fun shouldParseScript() {
        @Serializable
        data class Model(
            @Selector("script")
            val script: DataHtmlString,
        )

        val body = "<script>console.log('burnoo')</script>"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("console.log('burnoo')")
    }

    @Test
    fun shouldParseStyle() {
        @Serializable
        data class Model(
            @Selector("style")
            val script: DataHtmlString,
        )

        val body = "<style>p { width: 20px; }</style>"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("p { width: 20px; }")
    }

    @Test
    fun shouldParseComment() {
        @Serializable
        data class Model(
            @Selector("*")
            val script: DataHtmlString,
        )

        val body = "<!--comment-->"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("comment")
    }
}
