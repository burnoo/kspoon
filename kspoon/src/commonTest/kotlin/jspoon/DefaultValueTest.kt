package dev.burnoo.ksoup.jspoon

import dev.burnoo.ksoup.Kspoon
import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class DefaultValueTest {

    @Serializable
    private class Model {
        @Selector(value = "span", defValue = "DEFAULT_VALUE")
        var text: String? = null

        @Selector(value = "p", defValue = "-100")
        var number: Int = 0

        @Selector(value = "span", defValue = "hello")
        var text2: String = "world"

        @Selector(value = "span")
        var text3: String = "hello world"

        @Selector(value = "span")
        var text4: String? = null
    }

    @Test
    fun defaultValueTest() {
        val model: Model = Kspoon {
            coerceInputValues = true
        }.parse("<div></div>")
        model.text shouldBe "DEFAULT_VALUE"  // since defValue explicitly defined
        model.text2 shouldBe "hello"  // defValue takes precedent as its whatever would be parsed from Element
        model.text3 shouldBe  "hello world" // no defValue, let's leave whatever is set
        model.text4 shouldBe null // should not be set to anything silently if developer did not set a defValue
        model.number shouldBe -100
    }
}
