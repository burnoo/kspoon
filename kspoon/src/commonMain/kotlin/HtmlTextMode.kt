package dev.burnoo.ksoup

enum class HtmlTextMode {
    Text,
    InnerHtml,
    OuterHtml,
    Data // script, style, comment
    ;

    internal companion object {

        fun fromAttribute(attribute: String?) = when (attribute) {
            "textHtml" -> Text
            "innerHtml", "html" -> InnerHtml
            "outerHtml" -> OuterHtml
            "dataHtml" -> Data
            else -> null
        }
    }
}
