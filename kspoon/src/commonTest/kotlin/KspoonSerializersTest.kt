package dev.burnoo.ksoup

import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.decoder.KspoonDecoder
import dev.burnoo.ksoup.exception.KspoonParseException
import dev.burnoo.ksoup.serializer.kspoonEncodeError
import io.kotest.assertions.throwables.shouldThrowWithMessage
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
        kspoonEncodeError()
    }
}

object TagSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.burnoo.ksoup.type.CustomString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val kspoonDecoder = decoder as KspoonDecoder
        val element = decoder.decodeElement()
        val tag = element?.tag()?.name
            ?: throw KspoonParseException("Could not get tag name for selector: ${kspoonDecoder.getSelectorFullPath()}")
        return tag
    }

    override fun serialize(encoder: Encoder, value: String) {
        kspoonEncodeError()
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

    @Test
    fun shouldUseCustomSerializer() {
        @Serializable
        data class Model(
            @Selector(".class1")
            @Serializable(TagSerializer::class)
            val tag: String,
        )

        val model = Kspoon.parse<Model>("""<p class="class1">text</p>""")

        model shouldBe Model("p")
    }

    @Test
    fun shouldThrowWithFullPathTagMessage() {
        @Serializable
        data class Model(
            @Selector(".class1")
            @Serializable(TagSerializer::class)
            val tag: String,
        )

        shouldThrowWithMessage<KspoonParseException>(message = "Could not get tag name for selector: ['.class1']") {
            Kspoon.parse<Model>("")
        }
    }
}
