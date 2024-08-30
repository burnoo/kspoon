package dev.burnoo.ksoup

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonListTest {

    @Test
    fun shouldParsePrimitiveList() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            val elements: List<Int>,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model(listOf(1, 2, 3))
    }

    @Test
    fun shouldParseClassList() {
        @Serializable
        data class P(
            @Selector("p")
            val text: String,
        )

        @Serializable
        data class Model(
            @Selector("ul > li")
            val elements: List<P>,
        )

        val body =
            """
            <ul>
                <li>1<p>a</p></li>
                <li>2<p>c</p></li>
                <li>3<p>d</p></li>
            </ul>
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model(
            listOf(
                P("a"),
                P("c"),
                P("d"),
            ),
        )
    }

    @Test
    fun shouldParseNestedListOfListsClassWithList() {
        @Serializable
        data class P(
            @Selector("p")
            val text: List<String>,
        )

        @Serializable
        data class Model(
            @Selector("ul > li")
            val elements: List<P>,
        )

        val body =
            """
            <ul>
                <li>1<p>a</p><p>b</p></li>
                <li>2<p>c</p></li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model(
            listOf(
                P(listOf("a", "b")),
                P(listOf("c")),
                P(emptyList()),
            ),
        )
    }
}
