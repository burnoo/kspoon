@file:OptIn(ExperimentalSerializationApi::class)

package dev.burnoo.kspoon

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.kspoon.annotation.Selector
import dev.burnoo.kspoon.exception.KspoonParseException
import dev.burnoo.kspoon.model.KspoonDocument
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Contextual
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonDocumentTest {

    @Test
    fun shouldParseKspoonDocument() {
        val body = """<html><head></head><body></body></html>"""
        val kspoonDocument = Kspoon.parse<KspoonDocument>(body)

        kspoonDocument.document.html() shouldBe Ksoup.parse(body).html()
    }

    @Test
    fun shouldParseDocument() {
        val body = """<html><head></head><body></body></html>"""
        val document = Kspoon.parse(ContextualSerializer(Document::class), body)

        document.html() shouldBe Ksoup.parse(body).html()
    }

    @Test
    fun shouldParseDocumentField() {
        @Serializable
        data class Model(
            @Contextual
            @Selector(":root")
            val document: Document,
        )

        val body = """<html><head></head><body></body></html>"""
        val model = Kspoon.parse<Model>(body)

        model.document.html() shouldBe Ksoup.parse(body).html()
    }

    @Test
    fun shouldThrowOnNestedDocument() {
        @Serializable
        data class Nested(
            @Contextual
            @Selector(":root")
            val document: Document,
        )

        @Serializable
        data class Model(
            @Selector(":root")
            val document: Nested,
        )

        val body = """<html><head></head><body></body></html>"""
        shouldThrowWithMessage<KspoonParseException>(message = "Current Element is not a Document. Document type works only on root") {
            Kspoon.parse<Model>(body)
        }
    }
}
