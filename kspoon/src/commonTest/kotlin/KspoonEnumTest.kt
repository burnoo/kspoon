package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test

@Serializable
private enum class Enum { @SerialName("a") A, @SerialName("b") B, @SerialName("c") C }

class KspoonEnumTest {

    @Test
    fun shouldParseEnum() {
        @Serializable
        data class Model(
            @Selector("p")
            val enum: Enum,
        )

        val body = "<p>b</p>"
        val model = Kspoon.parse<Model>(body)

        model shouldBe Model(Enum.B)
    }
}
