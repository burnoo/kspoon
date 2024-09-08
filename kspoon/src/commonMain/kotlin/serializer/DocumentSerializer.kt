package dev.burnoo.ksoup.serializer

import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.decoder.KspoonDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [Document]. Does not use [Selector] annotation,
 * works only if Serializer is applied to root class field.
 *
 * Example:
 * ```
 * @Serializable
 * data class Page(
 *     @Serializable(DocumentSerializer::class) // or @Contextual
 *     val document: Document,
 * )
 *
 * Kspoon.parse<Page>("<html><head></head><body></body></html>")
 * ```
 */
public object DocumentSerializer : KSerializer<Document> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.fleeksoft.ksoup.nodes.Document", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Document {
        return (decoder as KspoonDecoder).decodeDocument()
    }

    override fun serialize(encoder: Encoder, value: Document) {
        kspoonEncodeError()
    }
}
