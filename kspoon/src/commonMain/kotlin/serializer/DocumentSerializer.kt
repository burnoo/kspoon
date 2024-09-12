package dev.burnoo.kspoon.serializer

import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.kspoon.Kspoon
import dev.burnoo.kspoon.decoder.KspoonDecoder
import dev.burnoo.kspoon.model.KspoonDocument
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [Document]. [Document] should be annotated with `@Selector(":root")`.
 * Works only if Serializer is applied to the root class field. For using Document parsing in places where Serializer cannot be easily
 * specified (e.g. returning from ktor/Retrofit) [KspoonDocument] can be used instead.
 *
 * This Serializer is also registered by [Kspoon] as [ContextualSerializer], so it's possible to use it by annotating
 * `@Contextual` on a field.
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
