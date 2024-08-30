package dev.burnoo.ksoup.serializer

import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.HtmlTreeDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object ElementsSerializer : KSerializer<Elements> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.fleeksoft.ksoup.select.Elements", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Elements {
        return (decoder as HtmlTreeDecoder).SerializerDecoder().decodeElements()
    }

    override fun serialize(encoder: Encoder, value: Elements) {
        error("Serialization is not supported")
    }
}
