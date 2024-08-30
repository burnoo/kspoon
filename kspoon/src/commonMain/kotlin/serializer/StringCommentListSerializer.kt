package dev.burnoo.ksoup.serializer

import dev.burnoo.ksoup.decoder.HtmlTreeDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object StringCommentListSerializer : KSerializer<List<String>> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.burnoo.ksoup.type.StringCommentList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        return (decoder as HtmlTreeDecoder).SerializerDecoder()
            .decodeCommentList()
            .map { it.getData() }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        error("Serialization is not supported")
    }
}
