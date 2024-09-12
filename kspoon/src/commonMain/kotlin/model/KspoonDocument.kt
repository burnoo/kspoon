package dev.burnoo.kspoon.model

import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.kspoon.annotation.Selector
import dev.burnoo.kspoon.serializer.DocumentSerializer
import kotlinx.serialization.Serializable

/**
 * Wrapper that allows parsing [Document] returned from a function.
 * In some scenarios (e.g. using the library with ktor or Retrofit) it is necessary to indicate
 * what serializer should be used for parsing the [Document].
 *
 * Example:
 * ```
 * val body = """<html><head></head><body></body></html>"""
 * val document = Kspoon.parse<KspoonDocument>(body).document
 * ```
 */
@Serializable
public data class KspoonDocument(
    /**
     * Parsed [Document].
     */
    @Serializable(DocumentSerializer::class)
    @Selector(":root")
    val document: Document,
)
