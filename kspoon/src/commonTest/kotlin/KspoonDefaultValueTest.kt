package dev.burnoo.ksoup

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonDefaultValueTest {

    @Test
    fun shouldGetFallbackDate() {
        @Serializable
        data class Model(
            @Selector("span", defValue = "1996-04-11")
            val date: LocalDate,
        )

        val body = ""
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model(LocalDate(1996, 4, 11))
    }

    @Test
    fun shouldGetFallbackOnMissingIndex() {
        @Serializable
        data class Model(
            @Selector("p", index = 1, defValue = "not-found")
            val text: String,
        )

        val body = "<p>text</p>"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("not-found")
    }

    @Test
    fun shouldGetFallbackOnNullable() {
        @Serializable
        data class Model(
            @Selector("p", defValue = "not-found")
            val text: String?,
        )

        val body = ""
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model("not-found")
    }
}
