package dev.burnoo.kspoon

import dev.burnoo.kspoon.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonNestedListTest {

    @Test
    fun `should parse nested items`() {
        @Serializable
        data class Item(
            @Selector("h2")
            val title: String,
        )

        @Serializable
        data class Page(
            @Selector("h1")
            val title: String,
            @Selector(".element")
            val elements: List<Item>,
        )

        val body =
            """
            <body>
              <h1>Header</h1>
              <div class="container">
                <div class="element">
                  <h2>Element 1</h2>
                </div>
                <div class="element">
                  <h2>Element 2</h2>
                </div>
                <div class="element">
                  <h2>Element 3</h2>
                </div>
              </div>
            </body>
            """.trimIndent()
        val model = Kspoon.parse<Page>(body)

        model shouldBe Page(
            title = "Header",
            elements = listOf(
                Item(title = "Element 1"),
                Item(title = "Element 2"),
                Item(title = "Element 3"),
            ),
        )
    }
}
