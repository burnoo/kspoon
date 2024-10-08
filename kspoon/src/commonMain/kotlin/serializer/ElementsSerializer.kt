package dev.burnoo.kspoon.serializer

import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.kspoon.Kspoon
import dev.burnoo.kspoon.decoder.KspoonDecoder
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [Elements].
 *
 * This Serializer is also registered by [Kspoon] as [ContextualSerializer], so it's possible to use it by annotating
 * `@Contextual` on a field.
 *
 * Example:
 * ```
 * @Serializable
 * data class Model(
 *     @Selector("ul > li")
 *     @Serializable(ElementsSerializer::class) // or @Contextual
 *     val element: Elements,
 * )
 *
 * // Elements contains 3x Element
 * val elements = Kspoon.parse<Model>("<ul><li>1</li><li>2</li><li>3</li></ul>").elements
 * ```
 */
public object ElementsSerializer : KSerializer<Elements> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.fleeksoft.ksoup.select.Elements", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Elements {
        return (decoder as KspoonDecoder).decodeElements()
    }

    override fun serialize(encoder: Encoder, value: Elements) {
        kspoonEncodeError()
    }
}
