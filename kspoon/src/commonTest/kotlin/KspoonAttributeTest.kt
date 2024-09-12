package dev.burnoo.kspoon

import dev.burnoo.kspoon.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonAttributeTest {

    @Test
    fun shouldParseAttribute() {
        @Serializable
        data class Model(
            @Selector("a", attr = "href")
            val url: String,
        )

        val body = """<a href="https://github.com/burnoo">Click here</a>"""
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("https://github.com/burnoo")
    }

    @Test
    fun shouldReturnEmptyStringForMissingAttribute() {
        @Serializable
        data class Model(
            @Selector("a", attr = "src")
            val src: String,
        )

        val body = """<a href="https://github.com/burnoo">Click here</a>"""

        val model = Kspoon.parse<Model>(body)

        model shouldBe Model("")
    }
}
