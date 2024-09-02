package dev.burnoo.ksoup.decoder

import dev.burnoo.ksoup.HtmlTextMode
import dev.burnoo.ksoup.SelectorHtmlTextMode
import dev.burnoo.ksoup.annotation.Selector
import dev.burnoo.ksoup.annotation.handleNullability

internal sealed class HtmlTag {

    data class Selector(
        val selector: String,
        val textMode: HtmlTextMode?,
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
    textMode = when (selectorAnnotation?.textMode) {
        SelectorHtmlTextMode.Text -> HtmlTextMode.Text
        SelectorHtmlTextMode.InnerHtml -> HtmlTextMode.InnerHtml
        SelectorHtmlTextMode.OuterHtml -> HtmlTextMode.OuterHtml
        SelectorHtmlTextMode.Data -> HtmlTextMode.Data
        SelectorHtmlTextMode.Default, null -> null
    },
    attribute = selectorAnnotation?.attr?.handleNullability(),
    index = selectorAnnotation?.index ?: 0,
    defaultValue = selectorAnnotation?.defValue?.handleNullability(),
    regex = selectorAnnotation?.regex?.handleNullability()?.toRegex(),
)
