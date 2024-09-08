package dev.burnoo.ksoup.decoder

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.exception.KspoonParseException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder

/**
 * Public [Decoder] that can be used for writing custom [KSerializer] for Kspoon.
 * Allows to get [Ksoup] classes inside decode function.
 *
 * Example:
 * ```
 * object TagSerializer : KSerializer<String> {
 *     override val descriptor: SerialDescriptor =
 *         PrimitiveSerialDescriptor("TagString", PrimitiveKind.STRING)
 *
 *     override fun deserialize(decoder: Decoder): String {
 *         val kspoonDecoder = decoder as KspoonDecoder
 *         val element = decoder.decodeElement()
 *         val tag = element?.tag()?.name
 *             ?: throw KspoonParseException("Could not get tag name for selector: ${kspoonDecoder.getSelectorFullPath()}")
 *         return tag
 *     }
 *
 *     override fun serialize(encoder: Encoder, value: String) {
 *         throw KspoonParseException("Serialization is not supported")
 *     }
 * }
 * ```
 */
public interface KspoonDecoder : Decoder {

    /**
     * @return current [Element] or null for current [Selector]
     */
    public fun decodeElement(): Element?

    /**
     * @return current [Element] or throws [KspoonParseException] for current [Selector]
     */
    public fun decodeElementOrThrow(): Element

    /**
     * @return current [Elements] for current [Selector]
     */
    public fun decodeElements(): Elements

    /**
     * @return [Document] - does not use [Selector] annotation, works only if Serializer is applied to root class field.
     */
    public fun decodeDocument(): Document

    /**
     * Example: "[['div' -> 'p.class1` -> 'span#id1']]"
     *
     * @return Full path to the current [Selector]. Can be used to print error messages.
     *
     */
    public fun getSelectorFullPath(): String
}
