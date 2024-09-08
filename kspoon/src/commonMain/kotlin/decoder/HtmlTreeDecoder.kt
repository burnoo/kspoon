package dev.burnoo.ksoup.decoder

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.HtmlTextMode
import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.configuration.KspoonConfiguration
import dev.burnoo.ksoup.exception.KspoonParseException
import dev.burnoo.ksoup.exception.kspoonError
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

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
internal class HtmlTreeDecoder(
    private val elements: Elements,
    private val configuration: KspoonConfiguration,
    extraSerializersModule: SerializersModule = EmptySerializersModule(),
    private val tagHierarchy: List<HtmlTag> = emptyList(),
) : TaggedDecoder<HtmlTag>(), KspoonDecoder {

    private val textMode = configuration.defaultTextMode
    private val coerceInputValues: Boolean = configuration.coerceInputValues

    override val serializersModule = SerializersModule {
        contextual(ElementSerializer)
        contextual(ElementsSerializer)
        contextual(DocumentSerializer)
    } overwriteWith extraSerializersModule
    private var elementIndex = 0

    override fun SerialDescriptor.getTag(index: Int): HtmlTag {
        val selectorAnnotation = getElementSelectorAnnotation(index)
        val newIndex = if (selectorAnnotation == null) {
            getElementName(index).toIntOrNull()
        } else {
            null
        }
        return when {
            selectorAnnotation != null -> selectorAnnotation.toHtmlTag()
            newIndex != null -> HtmlTag.Index(newIndex)
            else -> kspoonError(
                "Selector annotation not found for ${getElementDescriptor(index).serialName}," +
                    " parent selector: ${getSelectorFullPath(tag = null)}",
            )
        }
    }

    private fun SerialDescriptor.getElementSelectorAnnotation(index: Int): Selector? {
        val annotations = getElementAnnotations(index) + getElementDescriptor(index).annotations
        return annotations.filterIsInstance<Selector>().firstOrNull()
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
        return HtmlTreeDecoder(selectedElements, configuration, tagHierarchy = tagHierarchy + tag)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = elements.size

    override fun decodeTaggedNotNullMark(tag: HtmlTag): Boolean {
        if (tag is HtmlTag.Index) return true
        if (tag is HtmlTag.Selector && tag.defaultValue != null) return true
        return selectElement(tag) != null
    }

    override fun decodeSequentially() = !configuration.coerceInputValues

    override fun decodeTaggedLong(tag: HtmlTag) = getTextAndMap(tag) { toLong() }
    override fun decodeTaggedShort(tag: HtmlTag) = getTextAndMap(tag) { toShort() }
    override fun decodeTaggedByte(tag: HtmlTag) = getTextAndMap(tag) { toByte() }
    override fun decodeTaggedFloat(tag: HtmlTag) = getTextAndMap(tag) { toFloat() }
    override fun decodeTaggedDouble(tag: HtmlTag) = getTextAndMap(tag) { toDouble() }
    override fun decodeTaggedBoolean(tag: HtmlTag) = getTextAndMap(tag) { toBoolean() }
    override fun decodeTaggedChar(tag: HtmlTag) = getText(tag).first()
    override fun decodeTaggedInt(tag: HtmlTag) = getTextAndMap(tag) { toInt() }
    override fun decodeTaggedString(tag: HtmlTag) = getText(tag)
    override fun decodeTaggedEnum(tag: HtmlTag, enumDescriptor: SerialDescriptor): Int {
        val text = getText(tag)
        val index = enumDescriptor.elementNames.indexOfFirst { it == text }
        return if (index == -1) {
            kspoonError(
                "Can't parse value '$text' for enum '${enumDescriptor.serialName}' at selector: ${getSelectorFullPath(tag)}",
            )
        } else index
    }

    private inline fun <reified T> getTextAndMap(tag: HtmlTag, map: String.() -> T): T {
        val text = getText(tag)
        return try {
            text.map()
        } catch (e: Throwable) {
            kspoonError("Error while converting 'text' to '${T::class}' for selector ${getSelectorFullPath(tag)}", e)
        }
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
            if (tag is HtmlTag.Selector && tag.defaultValue != null) return tag.defaultValue
            if (e is KspoonParseException) {
                throw e
            } else {
                kspoonError("Error getting text for selector: ${getSelectorFullPath(tag)}", e)
            }
        }
    }

    private fun selectElementOrThrow(tag: HtmlTag): Element {
        return when (val element = selectElement(tag)) {
            null -> kspoonError("Element not found for selector: ${getSelectorFullPath(tag)}")
            else -> element
        }
    }

    private fun String.withRegexIfPresent(tag: HtmlTag): String {
        if (tag !is HtmlTag.Selector) return this
        if (tag.regex == null) return this
        val matchResult = tag.regex.find(this)
            ?: kspoonError("Regex '${tag.regex}' not found for current selector: ${getSelectorFullPath(tag)}")
        return if (matchResult.groupValues.size > 1) matchResult.groupValues[1] else matchResult.value
    }

    private fun Elements.getAtAsElements(index: Int) = getOrNull(index)?.let(::Elements) ?: Elements()

    private fun getSelectorFullPath(tag: HtmlTag?) = (tagHierarchy + tag)
        .filterNotNull()
        .joinToString(" -> ", prefix = "[", postfix = "]")

    // KspoonDecoder implementation
    override fun decodeElement(): Element? = selectElement(tag = currentTag)

    override fun decodeElementOrThrow(): Element = selectElementOrThrow(tag = currentTag)

    override fun decodeElements(): Elements = selectElements(tag = currentTag)

    override fun decodeDocument() = elements.firstOrNull() as? Document
        ?: kspoonError("Current Element is not a Document. Document type works only on root")

    override fun getSelectorFullPath(): String = getSelectorFullPath(tag = currentTagOrNull)
}
