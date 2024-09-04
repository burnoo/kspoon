package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.exception.KspoonParseException
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.text.trimIndent as trimIndent1

class KspoonRegexTest {

    @Test
    fun shouldParseWithRegexRoot() {
        @Serializable
        data class Model(
            @Selector("p.class1", regex = "ext[0-9]")
            val text: String,
        )

        val body =
            """
            <p class="class1"><span>text</span>10</p>
            <p class="class1"><span>text</span>20</p>
            """.trimIndent1()
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("ext1")
    }

    @Test
    fun shouldParseWithRegexFirstGroup() {
        @Serializable
        data class Model(
            @Selector("p.class1", regex = "(te)xt")
            val text: String,
        )

        val body =
            """
            <p class="class1"><span>text</span>10</p>
            <p class="class1"><span>text</span>20</p>
            """.trimIndent1()
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("te")
    }

    @Test
    fun shouldThrowOnNotMatchingRegex() {
        @Serializable
        data class Model(
            @Selector("p.class1", regex = "hello")
            val text: String,
        )

        val body = """<p class="class1"><span>text</span>10</p>"""

        shouldThrowWithMessage<KspoonParseException>(message = "Regex 'hello' not found for current selector: ['p.class1' regex='hello']") {
            Kspoon.parse<Model>(body)
        }
    }
}
