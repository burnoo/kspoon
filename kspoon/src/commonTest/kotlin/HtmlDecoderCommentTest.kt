package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import dev.burnoo.ksoup.serializer.CommentList
import dev.burnoo.ksoup.serializer.StringCommentList
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class HtmlDecoderCommentTest {

    @Test
    fun shouldParseCommentList() {
        @Serializable
        data class Model(
            @Selector("p", index = 0)
            val comments: CommentList,
        )

        val body = "<p><!--a--><!--b--></p><!--c-->"
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model.comments.map { it.getData() } shouldBe listOf("a", "b")
    }

    @Test
    fun shouldParseStringCommentList() {
        @Serializable
        data class Model(
            @Selector("p", index = 0)
            val comments: StringCommentList,
        )

        val body = "<p><!--a--><!--b--></p><!--c-->"
        val decoder = HtmlDecoder(Ksoup.parse(body))
        val model = decoder.decodeSerializableValue(Model.serializer())

        model shouldBe Model(listOf("a", "b"))
    }
}
