# kspoon 🥄

[![Maven Central](https://img.shields.io/maven-central/v/dev.burnoo.kspoon/kspoon)](https://search.maven.org/search?q=dev.burnoo.kspoon) [![javadoc](https://javadoc.io/badge2/dev.burnoo.kspoon/kspoon/javadoc.svg?label=dokka&logo=)](https://javadoc.io/doc/dev.burnoo.kspoon/kspoon/latest/kspoon/dev.burnoo.kspoon/-kspoon/index.html)

kspoon is a Kotlin Multiplatform library for parsing HTML into Kotlin objects. It
uses [ksoup](https://github.com/fleeksoft/ksoup) as an HTML parser
and [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) to create objects. This library is a
successor to [jspoon](https://github.com/DroidsOnRoids/jspoon/).

A big shoutout to [@itboy87](https://github.com/itboy87) for porting Jsoup to KMP - this library wouldn't exist without
his amazing work. Check out the [Ksoup repository](https://github.com/fleeksoft/ksoup)!

## Installation

Apply serialization plugin to your module `build.gradle.kts`/`build.gradle`:

```kotlin
plugins {
    kotlin("plugin.serialization") version "<kotlin version>"
}
```

Add the following dependency to your module `build.gradle.kts`/`build.gradle` file:

```kotlin
dependencies {
    implementation("dev.burnoo.kspoon:kspoon:0.0.1")
}
```

This library uses
the [ksoup-lite](https://github.com/fleeksoft/ksoup/?tab=readme-ov-file#ksoup-is-published-on-maven-central)
variant of Ksoup. If you plan to use other variants of the Ksoup library within the same project, you may need to
replace `ksoup-lite` with
your preferred variant by using
Gradle's [dependency substitution](https://docs.gradle.org/current/userguide/resolution_rules.html#sec:variant_aware_substitutions).

## Usage

kspoon works with any serializable class. Adding `@Selector` annotations on its serializable fields, enables HTML
parsing:

```kotlin
@Serializable
data class Page(
    @Selector("#header") val header: String,
    @Selector("li.class1") val intList: List<Int>,
    @Selector(value = "#image1", attr = "src") val imageSource: String,
)
```

You can then use a `Kspoon` instance to create objects:

```kotlin
val htmlContent = """<div>
    <p id='header'>Title</p>
    <ul>
    <li class='class1'>1</li>
    <li>2</li>
    <li class='class1'>3</li>
    </ul>
    <img id='image1' src='image.bmp' />
    </div>""".trimIndent()

val page = Kspoon.parse<Page>(htmlContent)
println(page) // Page(header=Title, intList=[1, 3], imageSource=image.bmp)
```

The library looks for the first occurrence with CSS selector in the HTML and sets its value to the corresponding field.

### Configuration

kspoon can be configured using the `Kspoon {}` factory function, which returns an instance that can be used for parsing.
All available options with default values are listed below:

```kotlin
val kspoon = Kspoon {
    // Specifies the parsing function. Type: (String) -> Document
    parse = { html: String -> Ksoup.parse(html, baseUri = "") }
    // Default text mode used for parsing.
    defaultTextMode = HtmlTextMode.Text
    // Enables coercing values when the selected HTML element is not found.
    coerceInputValues = false
    // Module with contextual and polymorphic serializers to be used.
    serializersModule = EmptySerializersModule()
}
kspoon.parse(HTML_CONTENT)
```

### Selecting content

By default, the HTML's `textContent` value is used to extract data. This behavior can be changed either in the
configuration or by using the `textMode` parameter in the `@Selector` annotation. Options include `InnerHtml`,
`OuterHtml`, or `Data` (for scripts and styles):

```kotlin
@Serializable
data class Page(
    @Selector("p", textMode = SelectorHtmlTextMode.OuterHtml)
    val content: String
)

val htmlContent = "<p><span>Text</span></p>"
val page = Kspoon.parse<Page>(htmlContent)
println(page) // Page(content=<p><span>Text</span></p>)
```

It is also possible to get an attribute value by setting the `attr` parameter in the `@Selector` annotation (
see [Usage](#usage) for an example).

### Regex

Regex can be set up by passing the `regex` parameter to the `@Selector` annotation. After parsing the text (with HTML
text mode or attribute), the regex is applied to the string. The returned string will be the first matched group or the
entire match if no group is specified.

```kotlin
data class Page(
    @Selector(value = "#numbers", regex = "([0-9]+) ")
    val starNumber: Int // <span id="numbers">31 stars</span> (31 will be parsed)
)
```

### Default values

There are three ways to set default values:

- `@Selector("#tag", defValue = "default")` - if the HTML element is not found, the `defValue` will be used as a
  parsed string
- Nullable field - if the HTML element is not found, the value will be set to `null`
- `coerceInputValues = true` in the `Kspoon {}` configuration - enables coercing to a default value
  ```kotlin
  @Serializable
  data class Model(
      @Selector("span")
      val text: String = "not found"
  ) 
  val body = "<p></p>"
  val text = Kspoon { coerceInputValues = true }.parse<Model>(body).text
  println(text) // prints "not found"
  ```

`defValue` offers the best performance due to the internal logic of kotlinx.serialization. Nullable fields does HTML
selection twice. Coercing input values does HTML selection twice and also disables
[sequential decoding](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-core/kotlinx.serialization.encoding/-composite-decoder/decode-sequentially.html).

### Serializers

Any `KSerializer` can be applied to a field annotated with `@Selector` to customize serialization logic. For example,
date serializers from [`kotlinx-datetime`](https://github.com/Kotlin/kotlinx-datetime):

```kotlin
@Serializable
data class Model(
    @Serializable(LocalDateIso8601Serializer::class)
    @Selector("span")
    val date: LocalDate,
)
```

Additionally, kspoon has built-in serializers for Ksoup classes: `ElementSerializer`, `ElementsSerializer`, and
`DocumentSerializer`. They can be used directly or via contextual serialization:

```kotlin
@Serializable
data class Model(
    @Serializable(ElementSerializer::class) // or @Contextual
    @Selector("div.class1")
    val element: Element,
)
```

It is also possible to write custom kspoon serializers that can access the selected `Element`. Read
more [here](/docs/custom-serializers.md).

### External librarues

The `Kspoon` class has a `toFormat(): StringFormat` function that can be used with third-party libraries. For detailed
integration instructions, see the following links:

- [Ktor](/docs/ktor.md)
- [Retrofit](docs/retrofit.md)

### Supported targets

`jvm`, `js`, `wasmjs` `linuxX64`, `linuxArm64`, `tvosArm64`, `tvosX64`, `tvosSimulatorArm64`, `macosX64`,
  `macosArm64`, `iosArm64`, `iosSimulatorArm64`, `iosX64`, `mingwX64`

### [Custom serializers](docs/custom-serializers.md)

### [jspoon compatibility](docs/jspoon-compatibility.md)

### Changelog

See [GitHub releases](https://github.com/burnoo/kspoon/releases).
