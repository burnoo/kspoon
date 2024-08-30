package dev.burnoo.ksoup.decoder

import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.annotation.handleNullability

internal sealed class HtmlTag {

    data class Selector(
        val selector: String,
        val attribute: String?,
        val defaultValue: String?,
        val index: Int,
        val regex: Regex?,
    ) : HtmlTag()
    data class Index(val index: Int) : HtmlTag()
}

internal fun createSelectorHtmlTag(
    selector: String,
    selectorAnnotation: Selector?,
) = HtmlTag.Selector(
    selector = selector,
    attribute = selectorAnnotation?.attr?.handleNullability(),
    index = selectorAnnotation?.index ?: 0,
    defaultValue = selectorAnnotation?.defValue?.handleNullability(),
    regex = selectorAnnotation?.regex?.handleNullability()?.toRegex(),
)
