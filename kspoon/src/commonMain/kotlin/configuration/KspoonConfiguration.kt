package dev.burnoo.kspoon.configuration

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.kspoon.HtmlTextMode
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@PublishedApi
internal class KspoonConfiguration(
    val parse: Ksoup.(html: String) -> Document = { parse(it) },
    val defaultTextMode: HtmlTextMode = HtmlTextMode.Text,
    val coerceInputValues: Boolean = false,
    val serializersModule: SerializersModule = EmptySerializersModule(),
)
