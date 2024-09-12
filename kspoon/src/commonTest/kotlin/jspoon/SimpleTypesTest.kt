package dev.burnoo.kspoon.jspoon

import com.fleeksoft.ksoup.nodes.Element
import dev.burnoo.kspoon.Kspoon
import dev.burnoo.kspoon.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.test.Test

class SimpleTypesTest {
    @Serializable
    private class BooleanModel {
        @Selector("#boolean1")
        var testBoolean1: Boolean = false

        @Selector("#boolean2")
        var testBoolean2: Boolean = false

        @Selector("#boolean3")
        var testBoolean3: Boolean? = null
    }

    @Serializable
    private class IntModel {
        @Selector("#int1")
        var testInt1: Int = 0

        @Selector("#int2")
        var testInt2: Int = 0

        @Selector("#int3")
        var testInteger3: Int? = null
    }

    @Serializable
    private class LongModel {
        @Selector("#int1")
        var testLong1: Long = 0

        @Selector("#int2")
        var testLong2: Long = 0

        @Selector("#int3")
        var testLong3: Long? = null
    }

    @Serializable
    private class FloatModel {
        @Selector("#float1")
        var testFloat1: Float = 0f

        @Selector("#float2")
        var testFloat2: Float = 0f

        @Selector("#float3")
        var testFloat3: Float? = null
    }

    @Serializable
    private class DoubleModel {
        @Selector("#float1")
        var testDouble1: Double = 0.0

        @Selector("#float2")
        var testDouble2: Double = 0.0

        @Selector("#float3")
        var testDouble3: Double? = null
    }

    @Serializable
    private class StringModel {
        @Selector("#string1")
        var testString1: String? = null

        @Selector("#string2")
        var testString2: String? = null

        @Selector("#string3")
        var testString3: String? = null
    }

    @Serializable
    private class ElementModel {
        @Selector("#element-test")
        @Contextual
        var testElement: Element? = null
    }

    @Serializable
    private class DateModel {
        @Selector("#date1")
        var testDate1: LocalDate? = null

        @Selector("#date2")
        var testDate2: LocalDate? = null

        @Selector("#date3")
        var testDate3: LocalDate? = null
    }

    @Test
    fun booleanTest() {
        val booleanModel: BooleanModel = createObjectFromHtml()
        booleanModel.testBoolean1 shouldBe true
        booleanModel.testBoolean2 shouldBe false
        booleanModel.testBoolean3 shouldBe false
    }

    @Test
    fun integerTest() {
        val intModel: IntModel = createObjectFromHtml()
        intModel.testInt1 shouldBe -200
        intModel.testInt2 shouldBe 4
        intModel.testInteger3 shouldBe 32000
    }

    @Test
    fun longTest() {
        val longModel: LongModel = createObjectFromHtml()
        longModel.testLong1 shouldBe -200
        longModel.testLong2 shouldBe 4
        longModel.testLong3 shouldBe 32000L
    }

    @Test
    fun floatTest() {
        val floatModel: FloatModel = createObjectFromHtml()
        floatModel.testFloat1 shouldBe 4.1f
        floatModel.testFloat2 shouldBe -10.00001f
        floatModel.testFloat3 shouldBe -32.123455f
    }

    @Test
    fun doubleTest() {
        val doubleModel: DoubleModel = createObjectFromHtml()
        doubleModel.testDouble1 shouldBe 4.1
        doubleModel.testDouble2 shouldBe -10.00001
        doubleModel.testDouble3 shouldBe -32.123456
    }

    @Test
    fun stringTest() {
        val stringModel: StringModel = createObjectFromHtml()
        stringModel.testString1 shouldBe "Test1"
        stringModel.testString2 shouldBe ""
        stringModel.testString3 shouldBe "Test2 ".trim()
    }

    @Test
    fun elementTest() {
        val elementModel: ElementModel = createObjectFromHtml()
        val testElement: Element? = elementModel.testElement
        testElement!!.id() shouldBe "element-test"
        testElement.attr("data-test") shouldBe "test"
    }

    @Test
    @Throws(Exception::class)
    fun dateTest() {
        val dateModel: DateModel = createObjectFromHtml()
        dateModel.testDate1 shouldBe LocalDate(2017, 7, 14)
        dateModel.testDate2 shouldBe LocalDate(2137, 4, 1)
        dateModel.testDate3 shouldBe LocalDate(1444, 7, 30)
    }

    private inline fun <reified T : Any> createObjectFromHtml(): T {
        return Kspoon.parse(HTML_CONTENT)
    }

    companion object {
        private const val HTML_CONTENT = (
            "<div>" +
                "<span id='string1'>Test1</span>" +
                "<span id='int1'>-200</span>" +
                "<span id='float1'>4.1</span>" +
                "<span id='boolean1'>true</span>" +
                "<span id='date1'>2017-07-14</span>" +
                "<div id='string2'></div>" +
                "<div id='int2'>4</div>" +
                "<div id='float2'>-10.00001</div>" +
                "<div id='boolean2'>false</div>" +
                "<div id='date2'>2137-04-01</div>" +
                "<p id='string3'>Test2 </p>" +
                "<p id='int3'>32000</p>" +
                "<p id='float3'>-32.123456</p>" +
                "<p id='boolean3'>test</p>" +
                "<p id='date3'>1444-07-30</p>" +
                "<div id='element-test' data-test='test'/>" +
                "</div>"
        )
    }
}
