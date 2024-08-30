package dev.burnoo.ksoup.serializer

import com.fleeksoft.ksoup.nodes.Comment
import dev.burnoo.ksoup.HtmlTreeDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object CommentListSerializer : KSerializer<List<Comment>> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.burnoo.ksoup.type.CommentList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<Comment> {
        return (decoder as HtmlTreeDecoder).SerializerDecoder().decodeCommentList()
    }

    override fun serialize(encoder: Encoder, value: List<Comment>) {
        error("Serialization is not supported")
    }
}
