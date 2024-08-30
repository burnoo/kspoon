package dev.burnoo.ksoup.serializer

import dev.burnoo.ksoup.HtmlDecoder
import dev.burnoo.ksoup.HtmlTextMode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object OuterHtmlSerializer : KSerializer<String> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OuterHtmlString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        return (decoder as HtmlDecoder).SerializerDecoder().decodeStringWithTextMode(HtmlTextMode.OuterHtml)
    }

    override fun serialize(encoder: Encoder, value: String) {
        error("Serialization is not supported")
    }
}

typealias OuterHtmlString = @Serializable(OuterHtmlSerializer::class) String

