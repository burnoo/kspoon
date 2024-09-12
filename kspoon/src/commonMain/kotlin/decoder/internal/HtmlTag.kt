package dev.burnoo.kspoon.decoder.internal

import dev.burnoo.kspoon.HtmlTextMode
import dev.burnoo.kspoon.SelectorHtmlTextMode
import dev.burnoo.kspoon.annotation.Selector
import dev.burnoo.kspoon.annotation.handleNullability

internal sealed class HtmlTag {

    data class Selector(
        val selector: String,
        val textMode: HtmlTextMode?,
        val attribute: String?,
        val defaultValue: String?,
        val index: Int,
        val regex: Regex?,
    ) : HtmlTag() {

        override fun toString() = buildString {
            append("'$selector'")
            if (index > 0) append(" at index $index")
            if (attribute != null) append(" attr='$attribute'")
            if (regex != null) append(" regex='$regex'")
        }
    }

    data class Index(
        val index: Int,
    ) : HtmlTag() {

        override fun toString() = "List at index $index"
    }
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
    index = maxOf(index, 0),
    defaultValue = defValue.handleNullability(),
    regex = regex.handleNullability()?.toRegex(),
)
