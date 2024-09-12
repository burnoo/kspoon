package dev.burnoo.kspoon

import dev.burnoo.kspoon.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonTextModeTest {

    @Test
    fun shouldParseTextByDefault() {
        @Serializable
        data class Model(
            @Selector("p", textMode = SelectorHtmlTextMode.Default)
            val text: String,
        )

        val body = "<div><p><span>text</span></p></div>"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("text")
    }

    @Test
    fun shouldParseText() {
        @Serializable
        data class Model(
            @Selector("p", textMode = SelectorHtmlTextMode.Text)
            val text: String,
        )

        val body = "<div><p><span>text</span></p></div>"
        val model = Kspoon {
            defaultTextMode = HtmlTextMode.OuterHtml
        }.parse<Model>(body)

        model shouldBe Model("text")
    }

    @Test
    fun shouldParseOuterHtml() {
        @Serializable
        data class Model(
            @Selector("p", textMode = SelectorHtmlTextMode.OuterHtml)
            val outerHtml: String,
        )

        val body = "<div><p><span>text</span></p></div>"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("<p><span>text</span></p>")
    }

    @Test
    fun shouldParseInnerHtml() {
        @Serializable
        data class Model(
            @Selector("p", textMode = SelectorHtmlTextMode.InnerHtml)
            val innerHtml: String,
        )

        val body = "<div><p><span>text</span></p></div>"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("<span>text</span>")
    }

    @Test
    fun shouldParseScript() {
        @Serializable
        data class Model(
            @Selector("script", textMode = SelectorHtmlTextMode.Data)
            val script: String,
        )

        val body = "<script>console.log('burnoo')</script>"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("console.log('burnoo')")
    }

    @Test
    fun shouldParseStyle() {
        @Serializable
        data class Model(
            @Selector("style", textMode = SelectorHtmlTextMode.Data)
            val script: String,
        )

        val body = "<style>p { width: 20px; }</style>"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("p { width: 20px; }")
    }

    @Test
    fun shouldParseComment() {
        @Serializable
        data class Model(
            @Selector("*", textMode = SelectorHtmlTextMode.Data)
            val script: String,
        )

        val body = "<!--comment-->"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("comment")
    }
}
