package dev.burnoo.ksoup.jspoon

import dev.burnoo.ksoup.Kspoon
import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class RegexTest {

    @Serializable
    private class RegexModel {
        @Selector(value = "div", regex = "([a-z]+),")
        var number: String? = null
    }

    @Serializable
    private class RegexModelDefault {
        @Selector(value = "div", regex = "(\\d+)", defValue = "1")
        var number: Int = 0
    }

    @Test
    fun regexTest() {
        val regexModel: RegexModel = Kspoon.parse(HTML_CONTENT)

        regexModel.number shouldBe "three"
    }

    @Test
    fun regexDefaultTest() {
        val regexModel: RegexModelDefault = Kspoon.parse(HTML_CONTENT)

        regexModel.number shouldBe 1
    }

    companion object {
        private const val HTML_CONTENT = (
            "<div>" +
                "ONE, TwO, three," +
                "</div>"
        )
    }
}
