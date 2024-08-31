package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.test.Test

data class StringWrapper(val text: String)

object StringWrapperSerializer : KSerializer<StringWrapper> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.burnoo.ksoup.type.CustomString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): StringWrapper {
        val value = decoder.decodeString()
        return StringWrapper(value)
    }

    override fun serialize(encoder: Encoder, value: StringWrapper) {
        error("serialization is not supported")
    }
}

class SerializersModuleTest {

    @Test
    fun shouldUseSerializersModule() {
        @Serializable
        data class Model(
            @Selector("p")
            @Contextual
            val stringWrapper: StringWrapper,
        )
        val kspoon = Kspoon {
            serializersModule = SerializersModule {
                contextual(StringWrapperSerializer)
            }
        }

        val model = kspoon.parse<Model>("<p>text</p>")

        model shouldBe Model(StringWrapper("text"))
    }
}
