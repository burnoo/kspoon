@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package dev.burnoo.ksoup.decoder

import com.fleeksoft.ksoup.nodes.Comment
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.HtmlTextMode
import dev.burnoo.ksoup.Selector
import dev.burnoo.ksoup.serializer.DocumentSerializer
import dev.burnoo.ksoup.serializer.ElementSerializer
import dev.burnoo.ksoup.serializer.ElementsSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.TaggedDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal class HtmlTreeDecoder internal constructor(
    private val elements: Elements,
    private val textMode: HtmlTextMode,
    extraSerializersModule: SerializersModule = EmptySerializersModule()
) : TaggedDecoder<HtmlTag>() {

    override val serializersModule = SerializersModule {
        @Suppress("UNCHECKED_CAST")
        contextual(ElementSerializer as KSerializer<Element>)
        contextual(ElementsSerializer)
        contextual(DocumentSerializer)
        this.include(extraSerializersModule)
    }
    private var elementIndex = 0

    override fun SerialDescriptor.getTag(index: Int): HtmlTag {
        val selectorAnnotation = getSelectorAnnotations(index)
        val selector = selectorAnnotation?.value ?: getElementName(index)
        return when (val newIndex = selector.toIntOrNull()) {
            null -> createSelectorHtmlTag(selector, selectorAnnotation)
            else -> HtmlTag.Index(newIndex)
        }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (elementIndex == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        return elementIndex++
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        val tag = currentTagOrNull ?: return this
        val selectedElements = selectElements(tag)
        return HtmlTreeDecoder(selectedElements, textMode = textMode)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = elements.size

    override fun decodeTaggedNotNullMark(tag: HtmlTag): Boolean {
        if (tag is HtmlTag.Index) return true
        if (tag is HtmlTag.Selector && tag.defaultValue != null) return true
        return selectElement(tag) != null
    }

    override fun decodeSequentially() = true

    override fun decodeTaggedLong(tag: HtmlTag) = getText(tag).toLong()
    override fun decodeTaggedShort(tag: HtmlTag) = getText(tag).toShort()
    override fun decodeTaggedByte(tag: HtmlTag) = getText(tag).toByte()
    override fun decodeTaggedFloat(tag: HtmlTag) = getText(tag).toFloat()
    override fun decodeTaggedDouble(tag: HtmlTag) = getText(tag).toDouble()
    override fun decodeTaggedBoolean(tag: HtmlTag) = getText(tag).toBoolean()
    override fun decodeTaggedChar(tag: HtmlTag) = getText(tag).first()
    override fun decodeTaggedInt(tag: HtmlTag) = getText(tag).toInt()
    override fun decodeTaggedString(tag: HtmlTag) = getText(tag)
    override fun decodeTaggedEnum(tag: HtmlTag, enumDescriptor: SerialDescriptor): Int {
        val text = getText(tag)
        val index = enumDescriptor.elementNames.indexOfFirst { it == text }
        return if (index == -1) {
            throw IllegalStateException("Can't parse value \"$text\" for enum ${enumDescriptor.serialName}")
        } else index
    }

    private fun selectElements(tag: HtmlTag): Elements {
        return when (tag) {
            is HtmlTag.Selector -> elements.select(tag.selector)
            is HtmlTag.Index -> elements.getAtAsElements(tag.index)
        }
    }

    @OptIn(ExperimentalContracts::class)
    private fun selectElement(tag: HtmlTag): Element? {
        contract { returns(null) implies (tag is HtmlTag.Selector) }
        return when (tag) {
            is HtmlTag.Selector -> elements.select(tag.selector).getOrNull(tag.index)
            is HtmlTag.Index -> elements[tag.index]
        }
    }

    private fun getText(tag: HtmlTag, currentTextMode: HtmlTextMode = textMode): String {
        return try {
            val element = selectElementOrThrow(tag)
            val attribute = (tag as? HtmlTag.Selector)?.attribute
            val textModeFromAttribute = HtmlTextMode.fromAttribute(attribute)
            if (attribute != null && textModeFromAttribute == null) {
                element.attr(attribute)
            } else when (textModeFromAttribute ?: currentTextMode) {
                HtmlTextMode.Text -> element.text()
                HtmlTextMode.InnerHtml -> element.html()
                HtmlTextMode.OuterHtml -> element.outerHtml()
                HtmlTextMode.Data -> element.data()
            }.withRegexIfPresent(tag)
        } catch (e: Exception) {
            if (tag !is HtmlTag.Selector || tag.defaultValue == null) throw e
            tag.defaultValue
        }
    }

    private fun selectElementOrThrow(tag: HtmlTag): Element {
        return when (val element = selectElement(tag)) {
            null -> throw IllegalStateException("Element not found for tag ${tag.selector} at index ${tag.index}")
            else -> element
        }
    }

    private fun String.withRegexIfPresent(tag: HtmlTag): String {
        if (tag !is HtmlTag.Selector) return this
        if (tag.regex == null) return this
        val matchResult = tag.regex.find(this) ?: error("Regex ${tag.regex} not found for tag ${tag.selector}")
        return matchResult.value
    }

    private fun SerialDescriptor.getSelectorAnnotations(index: Int): Selector? {
        return getElementAnnotations(index).filterIsInstance<Selector>().firstOrNull()
    }

    private fun Elements.getAtAsElements(index: Int) = getOrNull(index)?.let(::Elements) ?: Elements()

    internal inner class SerializerDecoder {

        fun decodeElement(): Element? = selectElement(tag = currentTag)

        fun decodeElements(): Elements = selectElements(tag = currentTag)

        fun decodeStringWithTextMode(textMode: HtmlTextMode): String {
            return getText(popTag(), textMode)
        }

        fun decodeCommentList(): List<Comment> {
            val element = selectElement(tag = currentTag)
            return element?.nodeStream()
                ?.toList()
                ?.filterIsInstance<Comment>()
                .orEmpty()
        }

        fun decodeDocument() = elements.firstOrNull() as? Document
            ?: error("Current Element is not a Document. Document type works only on root")
    }
}
