package dev.burnoo.ksoup

import dev.burnoo.ksoup.type.CommentList
import dev.burnoo.ksoup.type.StringCommentList
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class KspoonCommentTest {

    @Test
    fun shouldParseCommentList() {
        @Serializable
        data class Model(
            @Selector("p", index = 0)
            val comments: CommentList,
        )

        val body = "<p><!--a--><!--b--></p><!--c-->"
        val model = Kspoon.decodeFromString<Model>(body)

        model.comments.map { it.getData() } shouldBe listOf("a", "b")
    }

    @Test
    fun shouldParseAllCommentList() {
        @Serializable
        data class Model(
            @Selector(":root")
            val comments: CommentList,
        )

        val body = "<p><!--a--><!--b--></p><!--c-->"
        val model = Kspoon.decodeFromString<Model>(body)

        model.comments.map { it.getData() } shouldBe listOf("a", "b", "c")
    }

    @Test
    fun shouldParseStringCommentList() {
        @Serializable
        data class Model(
            @Selector("p", index = 0)
            val comments: StringCommentList,
        )

        val body = "<p><!--a--><!--b--></p><!--c-->"
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model(listOf("a", "b"))
    }
}
