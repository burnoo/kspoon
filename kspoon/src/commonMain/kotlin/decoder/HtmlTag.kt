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

internal fun Selector.toHtmlTag() = HtmlTag.Selector(
    selector = value,
    textMode = when (textMode) {
        SelectorHtmlTextMode.Text -> HtmlTextMode.Text
        SelectorHtmlTextMode.InnerHtml -> HtmlTextMode.InnerHtml
        SelectorHtmlTextMode.OuterHtml -> HtmlTextMode.OuterHtml
        SelectorHtmlTextMode.Data -> HtmlTextMode.Data
        SelectorHtmlTextMode.Default -> null
    },
    attribute = attr.handleNullability(),
    index = index,
    defaultValue = defValue.handleNullability(),
    regex = regex.handleNullability()?.toRegex(),
)
