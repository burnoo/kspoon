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

    @Test
    fun simpleAttributes() {
        val imgAttributesModel = createObjectFromHtml<ImgAttributesModel>()
        imgAttributesModel.id shouldBe "id"
        imgAttributesModel.src shouldBe "/img.jpg"
        imgAttributesModel.alt shouldBe "alt-text"
        imgAttributesModel.classes shouldBe "A B C"
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
