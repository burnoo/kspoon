package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonCoerceTest {

    private val kspoon = Kspoon {
        coerceInputValues = true
    }

    @Test
    fun shouldCoerceNotFoundValue() {
        @Serializable
        data class Model(
            @Selector("span")
            val text: String = "not found",
        )

        val body = "<p></p>"
        val model = kspoon.parse<Model>(body)

        model shouldBe Model("not found")
    }

    @Test
    fun shouldCoerceNullValue() {
        @Serializable
        data class Model(
            @Selector("span")
            val text: String? = null,
        )

        val body = "<p></p>"
        val model = kspoon.parse<Model>(body)

        model shouldBe Model(null)
    }

    @Test
    fun shouldCoerceInvalidIndex() {
        @Serializable
        data class Model(
            @Selector("p", index = 1)
            val text: String = "not found",
        )

        val body = "<p>text</p>"
        val model = kspoon.parse<Model>(body)

        model shouldBe Model("not found")
    }

    @Test
    fun shouldCoerceManyValues() {
        @Serializable
        data class Model(
            @Selector("p")
            val text: String = "not found",
            @Selector("p")
            val text2: String = "not found",
            @Selector("p")
            val text3: String = "not found",
        )

        val body = "<div></div>"
        val model = kspoon.parse<Model>(body)

        model shouldBe Model()
    }

    @Test
    fun shouldCoerceFirstAndLastValues() {
        @Serializable
        data class Model(
            @Selector("p")
            val text: String = "not found",
            @Selector("div")
            val text2: String = "not found",
            @Selector("p")
            val text3: String = "not found",
        )

        val body = "<div>text</div>"
        val model = kspoon.parse<Model>(body)

        model shouldBe Model(text2 = "text")
    }
}
