package dev.burnoo.ksoup.serializer

import com.fleeksoft.ksoup.nodes.Element
import dev.burnoo.ksoup.Kspoon
import dev.burnoo.ksoup.decoder.KspoonDecoder
import dev.burnoo.ksoup.exception.KspoonParseException
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [Element]. Will throw [KspoonParseException] if element is not found for not nullable Element,
 * and return null for nullable Element?.
 *
 * This Serializer is also registered by [Kspoon] as [ContextualSerializer], so it's possible to use it by annotating
 * `@Contextual` on a field.
 *
 * Example:
 * ```
 * @Serializable
 * data class Model(
 *     @Selector("#id1")
 *     @Serializable(ElementSerializer::class) // or @Contextual
 *     val element: Element,
 * )
 *
 * val element = Kspoon.parse<Model>("<p id='id1'>Hello</p><p>World</p>").element
 * ```
 */
public object ElementSerializer : KSerializer<Element> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.fleeksoft.ksoup.select.Element", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Element {
        return (decoder as KspoonDecoder).decodeElementOrThrow()
    }

    override fun serialize(encoder: Encoder, value: Element) {
        kspoonEncodeError()
    }
}
