package dev.burnoo.ksoup.serializer

import dev.burnoo.ksoup.HtmlTextMode
import dev.burnoo.ksoup.decoder.HtmlTreeDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object InnerHtmlSerializer : KSerializer<String> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.burnoo.ksoup.type.InnerHtmlString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        return (decoder as HtmlTreeDecoder).SerializerDecoder().decodeStringWithTextMode(HtmlTextMode.InnerHtml)
    }

    override fun serialize(encoder: Encoder, value: String) {
        error("Serialization is not supported")
    }
}
