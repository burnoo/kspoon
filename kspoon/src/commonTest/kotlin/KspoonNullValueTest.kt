package dev.burnoo.kspoon

import dev.burnoo.kspoon.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonNullValueTest {

    @Test
    fun shouldGetNullNotFoundSelector() {
        @Serializable
        data class Model(
            @Selector("p")
            val text: String?,
        )

        val body = ""
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(null)
    }

    @Test
    fun shouldGetNullOnMissingIndex() {
        @Serializable
        data class Model(
            @Selector("p", index = 1)
            val text: String?,
        )

        val body = "<p>text</p>"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(null)
    }
}
