package dev.burnoo.ksoup.decoder

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements

interface KspoonDecoder {

    fun decodeElement(): Element?

    fun decodeElementOrThrow(): Element

    fun decodeElements(): Elements

    fun decodeDocument(): Document

    fun getSelectorFullPath(): String
}
