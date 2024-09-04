package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.exception.KspoonParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonSimpleTest {

    @Test
    fun shouldParseFirstText() {
        @Serializable
        data class Model(
            @Selector("p.class1")
            val text: String,
        )

        val body =
            """
            <p class="class1"><span>text</span>1</p>
            <p class="class1"><span>text</span>2</p>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("text1")
    }

    @Test
    fun shouldParseDate() {
        @Serializable
        data class Model(
            @Selector("span")
            val date: LocalDate,
        )

        val body =
            """
            <span>1996-04-11</span>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(LocalDate(1996, 4, 11))
    }

    @Test
    fun shouldParseNestedText() {
        @Serializable
        data class Span(
            @Selector("span")
            val text: String,
        )

        @Serializable
        data class Model(
            @Selector("p.class1")
            val text: Span,
        )

        val body =
            """
            <p class="class1"><span>text</span>1</p>
            <p class="class1"><span>text</span>2</p>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(Span("text"))
    }

    @Test
    fun shouldParseRoot() {
        @Serializable
        data class Root(
            @Selector(":root")
            val text: String,
        )

        @Serializable
        data class Model(
            @Selector("p.class1")
            val text: Root,
        )

        val body = """<p class="class1">text1</p>"""
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(Root("text1"))
    }

    @Test
    fun shouldParseWithRegex() {
        @Serializable
        data class Model(
            @Selector("p.class1", regex = "ext[0-9]")
            val text: String,
        )

        val body =
            """
            <p class="class1"><span>text</span>10</p>
            <p class="class1"><span>text</span>20</p>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("ext1")
    }

    @Test
    fun shouldParseValueAtIndex() {
        @Serializable
        data class Model(
            @Selector("p", index = 1)
            val number: Int,
        )

        val body =
            """
            <p>1</p>
            <p>2</p>
            """.trimIndent()
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(2)
    }

    @Test
    fun shouldThrowAtNotFoundText() {
        @Serializable
        data class Model(
            @Selector("p")
            val number: Int,
        )

        val body = ""
        shouldThrowWithMessage<KspoonParseException>("Element not found for selector: ['p']") {
            Kspoon.parse<Model>(body)
        }
    }

    @Test
    fun shouldThrowAtWrongIndex() {
        @Serializable
        data class Model(
            @Selector("p", index = 1)
            val number: Int,
        )

        val body = "<p>1</p>"
        shouldThrowWithMessage<KspoonParseException>("Element not found for selector: ['p' at index 1]") {
            Kspoon.parse<Model>(body)
        }
    }

    @Test
    fun shouldThrowWhenConvertingFailed() {
        @Serializable
        data class Model(
            @Selector("p")
            val number: Int,
        )

        val body = "<p>a</p>"
        shouldThrow<NumberFormatException> {
            Kspoon.parse<Model>(body)
        }
    }
}
