package dev.burnoo.ksoup

enum class HtmlTextMode {
    Text,
    InnerHtml,
    OuterHtml,
    Data, // script, style, comment
    ;
}

enum class SelectorHtmlTextMode {
    Default, Text, InnerHtml, OuterHtml, Data,
}
