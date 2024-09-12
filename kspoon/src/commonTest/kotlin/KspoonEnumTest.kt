package dev.burnoo.kspoon

import dev.burnoo.kspoon.annotation.Selector
import dev.burnoo.kspoon.exception.KspoonParseException
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test

@Serializable
private enum class Enum {
    @SerialName("a") A,

    @SerialName("b") B,

    @SerialName("c") C,
}

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

    @Test
    fun shouldThrowOnNotMatchingValue() {
        @Serializable
        data class Model(
            @Selector("p")
            val enum: Enum,
        )

        val body = "<p>D</p>"
        shouldThrowWithMessage<KspoonParseException>(
            message = "Can't parse value 'D' for enum 'dev.burnoo.kspoon.Enum' at selector: ['p']",
        ) {
            Kspoon.parse<Model>(body)
        }
    }
}
