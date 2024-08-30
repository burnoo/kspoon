package dev.burnoo.ksoup.jspoon

import dev.burnoo.ksoup.Kspoon
import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.test.Test

class SimpleTypesListTest {

    @Serializable
    private class BooleanModel {
        @Selector(".boolean")
        var booleanList: List<Boolean>? = null

        @Selector(".boolean")
        var booleanArrayList: ArrayList<Boolean>? = null
    }

    @Serializable
    private class IntModel {
        @Selector(".int")
        var integerList: List<Int>? = null

        @Selector(".int")
        var integerArrayList: ArrayList<Int>? = null
    }

    @Serializable
    private  class StringModel {
        @Selector(".string")
        var stringList: List<String>? = null

        @Selector(".string")
        var stringArrayList: ArrayList<String>? = null
    }

    @Test
    fun booleanLists() {
        val booleanModel: BooleanModel = BooleanModel::class.createObjectFromHtml()
        booleanModel.booleanList shouldBe listOf(true, false, false)
        booleanModel.booleanArrayList shouldBe ArrayList(listOf(true, false, false))
    }

    @Test
    fun integerLists() {
        val intModel: IntModel = IntModel::class.createObjectFromHtml()
        intModel.integerList shouldBe listOf(-200, 4, 32000)
        intModel.integerArrayList shouldBe ArrayList(listOf(-200, 4, 32000))
    }

    @Test
    fun stringLists() {
        val stringModel: StringModel = StringModel::class.createObjectFromHtml()
        stringModel.stringList shouldBe listOf("Test1", "", "Test2 ".trim())
        stringModel.stringArrayList shouldBe ArrayList(listOf("Test1", "", "Test2 ".trim()))
    }

    private inline fun <reified T : Any> KClass<T>.createObjectFromHtml(): T {
        return Kspoon.decodeFromString<T>(HTML_CONTENT)
    }

    companion object {
        private const val HTML_CONTENT = ("<div>"
                + "<span class='string'>Test1</span>"
                + "<span class='int'>-200</span>"
                + "<span class='boolean'>true</span>"
                + "<div class='string'></div>"
                + "<div class='int'>4</div>"
                + "<div class='boolean'>false</div>"
                + "<p class='string'>Test2 </p>"
                + "<p class='int'>32000</p>"
                + "<p class='boolean'>test</p>"
                + "</div>")
    }
}
