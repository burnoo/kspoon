package dev.burnoo.ksoup.type

import com.fleeksoft.ksoup.nodes.Comment
import dev.burnoo.ksoup.serializer.CommentListSerializer
import dev.burnoo.ksoup.serializer.StringCommentListSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

typealias StringCommentList = @Serializable(StringCommentListSerializer::class) List<String>
typealias CommentList = @Serializable(CommentListSerializer::class) List<@Contextual Comment>
