package dev.burnoo.kspoon.configuration

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.kspoon.HtmlTextMode
import dev.burnoo.kspoon.Kspoon
import dev.burnoo.kspoon.annotation.Selector
import dev.burnoo.kspoon.decoder.internal.HtmlTreeDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

/**
 * Builder of the [Kspoon] instance provided by `Kspoon { ... }` factory function:
 *
 * ```
 * val kspoon = Kspoon { // this: KspoonBuilder
 *     defaultTextMode = HtmlTextMode.OuterHtml
 *     coerceInputValues = true
 * }
 * ```
 */
public class KspoonBuilder internal constructor(kspoon: Kspoon) {
    /**
     * Specifies [Ksoup] function that is used for parsing. Allows to set up [Ksoup].
     *
     * Example:
     * ```
     * @Serializable
     * data class Model(
     *     @Selector("a", attr = "abs:href")
     *     val url: String,
     * )
     *
     * val kspoon = Kspoon {
     *     parse = { html -> parse(html, baseUri = "https://github.com") }
     * }
     *
     * val url = kspoon.parse<Model>("""<a href="burnoo">Click</a>""").url
     * println(url) // prints "https://github.com/burnoo"
     * ```
     */
    public var parse: Ksoup.(String) -> Document = kspoon.configuration.parse

    /**
     * Specifies default [HtmlTextMode] that is used for parsing. Can be overridden by [Selector.textMode]
     *
     * Example:
     * ```
     * @Serializable
     * data class Model(
     *     @Selector("p")
     *     val text: String,
     * )
     * val kspoon = Kspoon {
     *     defaultTextMode = HtmlTextMode.OuterHtml
     * }
     *
     * val text = kspoon.parse<Model>("<p>text</p>").text
     * println(text) // prints "<p>text</p>"
     * ```
     */
    public var defaultTextMode: HtmlTextMode = kspoon.configuration.defaultTextMode

    /**
     * Enables coercing values when the selected HTML element is not found.
     *
     * Coerced values are treated as missing; they are replaced either with a default property value if it exists.
     *
     * Warning: If very high performance is needed, it is advised to keep [coerceInputValues] as `false`. It disables sequential decoding
     * (see [CompositeDecoder.decodeSequentially]) and adds additional element selection for all elements with default value.
     * See: [HtmlTreeDecoder.shouldCoerceInputValue]
     *
     * Example:
     * ```
     * @Serializable
     * data class Model(
     *     @Selector("span")
     *     val text: String = "not found",
     * )
     *
     * val body = "<p></p>"
     * val text = Kspoon { coerceInputValues = true }.parse<Model>(body).text
     * println(text) // prints "not found"
     * ```
     */
    public var coerceInputValues: Boolean = kspoon.configuration.coerceInputValues

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Kspoon] instance.
     */
    public var serializersModule: SerializersModule = kspoon.configuration.serializersModule

    internal fun build() = KspoonConfiguration(parse, defaultTextMode, coerceInputValues, serializersModule)
}
