package dev.burnoo.kspoon

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.kspoon.configuration.KspoonBuilder
import dev.burnoo.kspoon.configuration.KspoonConfiguration
import dev.burnoo.kspoon.decoder.internal.HtmlTreeDecoder
import dev.burnoo.kspoon.exception.KspoonParseException
import dev.burnoo.kspoon.exception.kspoonError
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * The main entry point to work with HTML parsing.
 * It is typically used by constructing an application-specific instance, with configured behaviour
 * and, if necessary, registered in [SerializersModule] custom serializers.
 * `Kspoon` instance can be configured in its `Kspoon {}` factory function using [KspoonBuilder].
 * For demonstration purposes or trivial usages, Kspoon [companion][Kspoon.Default] can be used instead.
 *
 * Then constructed instance can be used in context of [StringFormat], by using function [Kspoon.toFormat].
 *
 * Example:
 * ```
 * @Serializable
 * data class GitHubProfile(
 *     @Selector("h1", regex = "(.*) - GitHub")
 *     val username: String,
 *     @Selector("img[id=avatar]", attr = "abs:src")
 *     val avatarUrl: String,
 *     @Selector("li")
 *     val interests: List<String>,
 *     @Selector(".pinned-repositories")
 *     val pinnedRepositories: List<GithubProfile.PinnedRepository> = emptyList(),
 * )
 *
 * val htmlContent = """
 *     <h1>burnoo - GitHub</h1>
 *     <img id="avatar" src="burnoo.png" />
 *     <ul class="interests">
 *         <li>KMP</li>
 *         <li>Android</li>
 *     </ul>
 * """
 *
 * val kspoon = Kspoon {
 *     parse = { html -> parse(html, baseUri = "https://github.com/") }
 *     coerceInputValues = true
 * }
 *
 * val profile = kspoon.parse<GitHubProfile>(htmlContent)
 * ```
 *
 * Kspoon format configuration can be refined using the corresponding constructor:
 * ```
 * val defaultKspoon = Kspoon {
 *     coerceInputValues = true
 * }
 * // Will inherit the properties of defaultKspoon
 * val debugEndpointKspoon = Kspoon(defaultKspoon) {
 *     // coerceInputValues is set to true
 *     defaultTextMode = HtmlTextMode.OuterHtml
 * }
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
public sealed class Kspoon(
    @PublishedApi internal val configuration: KspoonConfiguration,
) {

    /**
     * The default instance of [Kspoon] with default configuration.
     *
     * Example:
     * ```
     * @Serializable
     * data class Page(@Selector("p") val text: String)
     *
     * val html = "<h1>Header</h1><p>Text</p>"
     * val page = Kspoon.parse<Page>(html)
     * // Prints "Text"
     * println(page.text)
     * ```
     */
    public companion object Default : Kspoon(KspoonConfiguration())

    /**
     * Parses the given HTML string [html] to the value of type [T] using deserializer
     * retrieved from the reified type parameter.
     *
     * Example:
     * ```
     * @Serializable
     * data class Page(@Selector("p") val text: String)
     *
     * val html = "<h1>Header</h1><p>Text</p>"
     * val page = Kspoon.parse<Page>(html)
     * // Prints "Text"
     * println(page.text)
     * ```
     *
     * @throws KspoonParseException in case of any parsing-specific error
     * @throws IllegalArgumentException if the parsed input is not a valid instance of [T]
     */
    public inline fun <reified T> parse(html: String): T = parse(configuration.serializersModule.serializer<T>(), html)

    /**
     * Parses the given HTML string [html] to the value of type [T] using the given [deserializer].
     *
     * Example:
     * ```
     * @Serializable
     * data class Page(@Selector("p") val text: String)
     *
     * val html = "<h1>Header</h1><p>Text</p>"
     * val page = Kspoon.parse(Page.serializer(), html)
     * // Prints "Text"
     * println(page.text)
     * ```
     *
     * @throws [SerializationException] in case of any parsing-specific error
     * @throws [IllegalArgumentException] if the parsed input cannot be represented as a valid instance of type [T]
     */
    public fun <T> parse(deserializer: DeserializationStrategy<T>, html: String): T {
        if (deserializer.descriptor.kind is PrimitiveKind) {
            kspoonError("Parsing is not supported for primitive types. Use class instead")
        }
        val document = configuration.parse(Ksoup, html)
        val decoder = HtmlTreeDecoder(
            elements = Elements(document),
            configuration = configuration,
            extraSerializersModule = configuration.serializersModule,
        )
        return decoder.decodeSerializableValue(deserializer)
    }

    /**
     * Converts this [Kspoon] instance to a [StringFormat] instance. This function can be used to make [Kspoon] work
     * with external libraries that uses serialization formats (e.g. ktor's content negotiation for kotlinx serialization
     * or Retrofit's kotlinx serialization converter). By default, the [StringFormat.encodeToString] crashes with
     * [KspoonParseException], but encoding can be delegated to the given [encodeStringFormatDelegate].
     *
     * Example (ktor):
     * ```
     * val client = HttpClient {
     *     install(ContentNegotiation) {
     *         register(ContentType.Text.Html, KotlinxSerializationConverter(Kspoon.toFormat()))
     *     }
     * }
     *
     * val profile = client.get("https://github.com/burnoo").body<GithubProfile>()
     */
    public fun toFormat(encodeStringFormatDelegate: StringFormat? = null): StringFormat = KspoonFormat(
        kspoon = this,
        serializersModule = configuration.serializersModule,
        encodeStringFormatDelegate = encodeStringFormatDelegate,
    )
}

/**
 * Creates an instance of [Kspoon] configured from the optionally given [Kspoon instance][from] and adjusted with [builderAction].
 *
 * Example:
 * ```
 * val defaultKspoon = Kspoon {
 *     coerceInputValues = true
 * }
 * // Will inherit the properties of defaultKspoon
 * val debugEndpointKspoon = Kspoon(defaultKspoon) {
 *     // coerceInputValues is set to true
 *     defaultTextMode = HtmlTextMode.OuterHtml
 * }
 * ```
 */
public fun Kspoon(from: Kspoon = Kspoon.Default, builderAction: KspoonBuilder.() -> Unit): Kspoon {
    val builder = KspoonBuilder(from)
    builder.builderAction()
    val configuration = builder.build()
    return KspoonImpl(configuration)
}

private class KspoonImpl(configuration: KspoonConfiguration) : Kspoon(configuration)
