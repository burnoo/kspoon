package dev.burnoo.ksoup.serializer

import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.ksoup.HtmlDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object DocumentSerializer : KSerializer<Document> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.fleeksoft.ksoup.nodes.Document", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Document {
        return (decoder as HtmlDecoder).SerializerDecoder().decodeDocument()
    }

    override fun serialize(encoder: Encoder, value: Document) {
        error("Serialization is not supported")
    }
}
