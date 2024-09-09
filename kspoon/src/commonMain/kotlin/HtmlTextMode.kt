package dev.burnoo.ksoup

import com.fleeksoft.ksoup.nodes.Element
import dev.burnoo.ksoup.annotation.Selector

/**
 * Mode used for extracting content from [Element].
 */
public enum class HtmlTextMode {
    /**
     * Uses [Element.text] for extracting.
     * Gets the normalized, combined text of this element and all its children. Whitespace is normalized and trimmed.
     *
     * For example, given HTML `<p>Hello  <b>there</b> now! </p>`, `p.text()` returns `"Hello there now!"`
     *
     * Default mode in [Kspoon] instance.
     */
    Text,

    /**
     * Uses [Element.html] for extracting.
     * Retrieves the element's inner HTML. E.g. on a `<div>` with one empty `<p>`, would return
     * `<p></p>`
     */
    InnerHtml,

    /**
     * Uses [Element.outerHtml] for extracting.
     * Get the outer HTML of this node. For example, on a `p` element, may return `<p>Para</p>`.
     * `<p></p>`
     */
    OuterHtml,

    /**
     * Uses [Element.data] for extracting.
     * Get the combined data of this element. Data is e.g. the inside of a <script> tag. Note that data is NOT the text of the element.
     * Use [Text] to get the text that would be visible to a user, and [Data] for the contents of scripts, CSS styles, etc.
     */
    Data,
}

/**
 * Class used in [Selector] for overriding default [HtmlTextMode] in [Kspoon] instance.
 */
public enum class SelectorHtmlTextMode {
    /**
     * Uses default [HtmlTextMode] specified in [Kspoon] configuration. Means no override. Default value in [Selector]
     */
    Default,

    /**
     * See [HtmlTextMode.Text]
     */
    Text,

    /**
     * See [HtmlTextMode.InnerHtml]
     */
    InnerHtml,

    /**
     * See [HtmlTextMode.OuterHtml]
     */
    OuterHtml,

    /**
     * See [HtmlTextMode.Data]
     */
    Data,
}
