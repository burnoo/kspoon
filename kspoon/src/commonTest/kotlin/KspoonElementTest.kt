package dev.burnoo.ksoup

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.exception.KspoonParseException
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonElementTest {

    @Test
    fun shouldParseElements() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            @Contextual
            val elements: Elements,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model.elements.map { it.outerHtml() } shouldBe listOf(
            "<li>1</li>",
            "<li>2</li>",
            "<li>3</li>",
        )
    }

    @Test
    fun shouldParseFirstElement() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            @Contextual
            val element: Element,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model.element.outerHtml() shouldBe "<li>1</li>"
    }

    @Test
    fun shouldGetNullElement() {
        @Serializable
        data class Model(
            @Selector("p")
            @Contextual
            val element: Element?,
        )

        val body = ""
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(null)
    }

    @Test
    fun shouldParseListOfElements() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            val elements: List<@Contextual Element>,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model.elements.map { it.outerHtml() } shouldBe listOf(
            "<li>1</li>",
            "<li>2</li>",
            "<li>3</li>",
        )
    }

    @Test
    fun shouldThrowOnNotFoundElement() {
        @Serializable
        data class Model(
            @Selector("p")
            @Contextual
            val element: Element,
        )

        val body = """<div></div>""".trimIndent()
        shouldThrowWithMessage<KspoonParseException>(message = "Element not found for selector: ['p']") {
            Kspoon.parse<Model>(body)
        }
    }
}
