package dev.burnoo.ksoup.jspoon

import dev.burnoo.ksoup.Kspoon
import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class AttributeTest {

    @Serializable
    private class ImgAttributesModel {
        @Selector(value = "img", attr = "id")
        var id: String? = null

        @Selector(value = "img", attr = "src")
        var src: String? = null

        @Selector(value = "img", attr = "alt")
        var alt: String? = null

        @Selector(value = "img", attr = "class")
        var classes: String? = null
    }

    @Serializable
    private class HtmlAttributesModel {
        @Selector("div")
        var text: String? = null

        // "default" by javadocs attr value should have the same result as no attr value above
        @Selector(value = "div", attr = "textHtml")
        var text2: String? = null

        @Selector(value = "div", attr = "html")
        var html: String? = null

        @Selector(value = "div", attr = "innerHtml")
        var innerHtml: String? = null

        @Selector(value = "div", attr = "outerHtml")
        var outerHtml: String? = null
    }

    @Test
    fun simpleAttributes() {
        val imgAttributesModel = createObjectFromHtml<ImgAttributesModel>()
        imgAttributesModel.id shouldBe "id"
        imgAttributesModel.src shouldBe "/img.jpg"
        imgAttributesModel.alt shouldBe "alt-text"
        imgAttributesModel.classes shouldBe "A B C"
    }

    @Test
    fun htmlAttributes() {
        val htmlAttributesModel = createObjectFromHtml<HtmlAttributesModel>()
        htmlAttributesModel.text shouldBe "test"
        htmlAttributesModel.text2 shouldBe "test"
        htmlAttributesModel.html shouldBe "<p>test</p>"
        htmlAttributesModel.innerHtml shouldBe "<p>test</p>"
        htmlAttributesModel.outerHtml!!.replace("[\n ]".toRegex(), "") shouldBe "<div><p>test</p></div>"
    }

    private inline fun <reified T : Any> createObjectFromHtml(): T {
        return Kspoon.parse<T>(HTML_CONTENT)
    }

    companion object {
        private const val HTML_CONTENT = (
            "<img " +
                "id='id' " +
                "src='/img.jpg' " +
                "alt='alt-text' " +
                "class='A B C' />" +
                "<div><p>test</p></div>"
        )
    }
}
