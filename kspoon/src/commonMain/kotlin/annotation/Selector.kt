package dev.burnoo.kspoon.annotation

import dev.burnoo.kspoon.Kspoon
import dev.burnoo.kspoon.SelectorHtmlTextMode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.Serializable

private const val NULL_VALUE = "[null]"

/**
 * Annotates a field to be parsed from HTML.
 *
 * A field annotated with this will receive the value corresponding to it's CSS
 * selector when the [Kspoon.parse] is called.
 *
 * Can be applied to any [Serializable] field
 *
 * Example:
 * ```
 * @Serializable
 * data class GithubProfile(
 *     @Selector("title", regex = "(.*) · GitHub")
 *     val displayName: String,
 *     @Selector("meta[property=og:image]", attr = "content")
 *     val avatarUrl: String,
 * )
 * ```
 *
 * @property value CSS selector
 * @property textMode The text mode of the selector. See [SelectorHtmlTextMode]
 * @property attr Attribute or property of selected field
 * @property defValue Default string value if selected HTML element is empty
 * @property index Index of the found HTML element
 * @property regex Regular expression to be applied on the parsed string
 */
@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
public annotation class Selector(
    val value: String,
    val textMode: SelectorHtmlTextMode = SelectorHtmlTextMode.Default,
    val attr: String = NULL_VALUE,
    val defValue: String = NULL_VALUE,
    val index: Int = 0,
    val regex: String = NULL_VALUE,
)

internal fun String.handleNullability() = if (this == NULL_VALUE) null else this
