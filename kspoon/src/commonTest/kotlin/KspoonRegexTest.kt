package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

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
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

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
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("te")
    }
}
