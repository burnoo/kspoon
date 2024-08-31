package dev.burnoo.ksoup.configuration

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import dev.burnoo.ksoup.HtmlTextMode

data class KspoonConfiguration(
    val parse: Ksoup.(html: String) -> Document = { parse(it) },
    val defaultTextMode: HtmlTextMode = HtmlTextMode.Text,
    val coerceInputValues: Boolean = false,
)
