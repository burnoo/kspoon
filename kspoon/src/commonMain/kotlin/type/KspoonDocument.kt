package dev.burnoo.ksoup.type

import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.ksoup.serializer.DocumentSerializer
import kotlinx.serialization.Serializable

@Serializable
data class KspoonDocument(
    @Serializable(DocumentSerializer::class)
    val document: Document,
)
