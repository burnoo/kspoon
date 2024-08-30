package dev.burnoo.ksoup.type

import com.fleeksoft.ksoup.nodes.Comment
import dev.burnoo.ksoup.serializer.CommentListSerializer
import dev.burnoo.ksoup.serializer.DataHtmlSerializer
import dev.burnoo.ksoup.serializer.InnerHtmlSerializer
import dev.burnoo.ksoup.serializer.OuterHtmlSerializer
import dev.burnoo.ksoup.serializer.StringCommentListSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

typealias DataHtmlString = @Serializable(DataHtmlSerializer::class) String
typealias InnerHtmlString = @Serializable(InnerHtmlSerializer::class) String
typealias OuterHtmlString = @Serializable(OuterHtmlSerializer::class) String

typealias StringCommentList = @Serializable(StringCommentListSerializer::class) List<String>
typealias CommentList = @Serializable(CommentListSerializer::class) List<@Contextual Comment>
