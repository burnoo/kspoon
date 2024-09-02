@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package dev.burnoo.ksoup.decoder

import com.fleeksoft.ksoup.nodes.Comment
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.HtmlTextMode
import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.configuration.KspoonConfiguration
import dev.burnoo.ksoup.serializer.DocumentSerializer
import dev.burnoo.ksoup.serializer.ElementSerializer
import dev.burnoo.ksoup.serializer.ElementsSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.TaggedDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.overwriteWith
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal class HtmlTreeDecoder internal constructor(
    private val elements: Elements,
    private val configuration: KspoonConfiguration,
    extraSerializersModule: SerializersModule = EmptySerializersModule(),
) : TaggedDecoder<HtmlTag>() {

    private val textMode = configuration.defaultTextMode
    private val coerceInputValues: Boolean = configuration.coerceInputValues

    override val serializersModule = SerializersModule {
        contextual(ElementSerializer)
        contextual(ElementsSerializer)
        contextual(DocumentSerializer)
    } overwriteWith extraSerializersModule
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
        val isList = descriptor.kind == StructureKind.LIST
        val maxCount = if (isList) elements.size else descriptor.elementsCount
        while (shouldCoerceInputValue(maxCount, descriptor)) {
            elementIndex++
        }
        if (elementIndex == maxCount) return CompositeDecoder.DECODE_DONE
        return elementIndex++
    }

    private fun shouldCoerceInputValue(maxCount: Int, descriptor: SerialDescriptor): Boolean {
        // ensure coerceInputValues from config is enabled
        if (!coerceInputValues) return false
        // ensure current structure hasn't ended
        if (elementIndex == maxCount) return false
        // ensure current structure is a class (and not a list)
        if (descriptor.kind != StructureKind.CLASS) return false
        // check if the element is optional - if the instance can be created with Kotlin default value
        if (!descriptor.isElementOptional(0)) return false
        val tag = descriptor.getTag(elementIndex)
        if (tag !is HtmlTag.Selector) return false
        // check if default value was set in selector - then we skip selecting element here, as default value
        // will be returned instead
        if (tag.defaultValue != null) return false
        // finally checking if the selected element is empty
        return selectElement(tag) == null
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        val tag = currentTagOrNull ?: return this
        val selectedElements = selectElements(tag)
        return HtmlTreeDecoder(selectedElements, configuration)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = elements.size

    override fun decodeTaggedNotNullMark(tag: HtmlTag): Boolean {
        if (tag is HtmlTag.Index) return true
        if (tag is HtmlTag.Selector && tag.defaultValue != null) return true
        return selectElement(tag) != null
    }

    override fun decodeSequentially() = !configuration.coerceInputValues

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
            val textModeFromSelector = (tag as? HtmlTag.Selector)?.textMode
            if (attribute != null) {
                element.attr(attribute)
            } else when (textModeFromSelector ?: currentTextMode) {
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
            null -> throw IllegalStateException("Element not found for tag \"${tag.selector}\" at index ${tag.index}")
            else -> element
        }
    }

    private fun String.withRegexIfPresent(tag: HtmlTag): String {
        if (tag !is HtmlTag.Selector) return this
        if (tag.regex == null) return this
        val matchResult = tag.regex.find(this) ?: error("Regex ${tag.regex} not found for tag ${tag.selector}")
        return if (matchResult.groupValues.size > 1) matchResult.groupValues[1] else matchResult.value
    }

    private fun SerialDescriptor.getSelectorAnnotations(index: Int): Selector? {
        return getElementAnnotations(index).filterIsInstance<Selector>().firstOrNull()
    }

    private fun Elements.getAtAsElements(index: Int) = getOrNull(index)?.let(::Elements) ?: Elements()

    inner class SerializerDecoder {

        fun decodeElement(): Element? = selectElement(tag = currentTag)

        fun decodeElementOrThrow(): Element = selectElementOrThrow(tag = currentTag)

        fun decodeElements(): Elements = selectElements(tag = currentTag)

        fun decodeDocument() = elements.firstOrNull() as? Document
            ?: error("Current Element is not a Document. Document type works only on root")

        fun decodeCommentList(includeNested: Boolean = true): List<Comment> {
            val element = selectElement(tag = currentTag)
            return (if (includeNested) element?.nodeStream() else element?.childNodes()?.asSequence())
                ?.filterIsInstance<Comment>()
                .orEmpty()
                .toList()
        }
    }
}
