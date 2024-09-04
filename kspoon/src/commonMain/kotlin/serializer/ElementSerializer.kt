package dev.burnoo.ksoup.serializer

import com.fleeksoft.ksoup.nodes.Element
import dev.burnoo.ksoup.decoder.KspoonDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ElementSerializer : KSerializer<Element> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.fleeksoft.ksoup.select.Element", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Element {
        return (decoder as KspoonDecoder).decodeElementOrThrow()
    }

    override fun serialize(encoder: Encoder, value: Element) {
        kspoonEncodeError()
    }
}
