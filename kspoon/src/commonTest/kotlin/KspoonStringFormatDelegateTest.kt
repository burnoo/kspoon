package dev.burnoo.ksoup

import io.kotest.matchers.shouldBe
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlin.test.Test

object CustomStringFormat : StringFormat {
    override val serializersModule = EmptySerializersModule()

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        error("Decoding not supported")
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return value.toString()
    }
}

class KspoonStringFormatDelegateTest {

    @Test
    fun shouldUseStringFormatDelegateForEncoding() {
        @Serializable
        data class Model(val text: String)
        val kspoon = Kspoon { encodeStringFormatDelegate = CustomStringFormat }
        val model = Model("text")

        val encodedString = kspoon.encodeToString(Model.serializer(), model)
        encodedString shouldBe model.toString()
    }
}
