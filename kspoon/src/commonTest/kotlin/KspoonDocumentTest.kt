@file:OptIn(ExperimentalSerializationApi::class)

package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.ksoup.serializer.KspoonDocument
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
        val kspoonDocument = Kspoon.decodeFromString<KspoonDocument>(body)

        kspoonDocument.document.html() shouldBe Ksoup.parse(body).html()
    }

    @Test
    fun shouldParseDocument() {
        val body = """<html><head></head><body></body></html>"""
        val document = Kspoon.decodeFromString(ContextualSerializer(Document::class), body)

        document.html() shouldBe Ksoup.parse(body).html()
    }

    @Test
    fun shouldParseDocumentField() {
        @Serializable
        data class Model(
            @Contextual
            val document: Document,
        )

        val body = """<html><head></head><body></body></html>"""
        val model = Kspoon.decodeFromString<Model>(body)

        model.document.html() shouldBe Ksoup.parse(body).html()
    }
}
