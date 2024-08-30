package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Contextual
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.test.Test

@OptIn(ExperimentalSerializationApi::class)
class HtmlDecoderElementTest {

    @Test
    fun shouldParseElements() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            @Contextual
            val elements: Elements,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model.elements.map { it.outerHtml() } shouldBe listOf(
            "<li>1</li>",
            "<li>2</li>",
            "<li>3</li>",
        )
    }

    @Test
    fun shouldParseFirstElement() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            @Contextual
            val element: Element,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model.element.outerHtml() shouldBe "<li>1</li>"
    }

    @Test
    fun shouldGetNullElement() {
        @Serializable
        data class Model(
            @Selector("p")
            @Contextual
            val element: Element?,
        )

        val body = ""
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model(null)
    }

    @Test
    fun shouldParseListOfElements() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            val elements: List<@Contextual Element>,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model.elements.map { it.outerHtml() } shouldBe listOf(
            "<li>1</li>",
            "<li>2</li>",
            "<li>3</li>",
        )
    }

    @Test
    fun shouldParseDocument() {
        val body = """<html><head></head><body></body></html>""".trimIndent()
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val document = decoder.decodeSerializableValue(ContextualSerializer(Document::class))

        document.html() shouldBe Ksoup.parse(body).html()
    }

    @Test
    fun shouldParseDocumentField() {
        @Serializable
        data class Model(
            @Contextual
            val document: Document,
        )

        val body = """<html><head></head><body></body></html>""".trimIndent()
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model.document.html() shouldBe Ksoup.parse(body).html()
    }

}
