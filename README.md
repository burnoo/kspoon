# kspoon 🥄

kspoon is a Kotlin Multiplatform library that provides parsing HTML into Kotlin objects basing on CSS selectors.
It uses [ksoup](https://github.com/fleeksoft/ksoup) as a HTML parser and
[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) to create objects.
It's the [jspoon](https://github.com/DroidsOnRoids/jspoon/]) successor.

Big shoutout to @itboy87 for porting Jsoup to KMP. This library couldn't exist without his amazing work.
Check out the [Ksoup repoitory](https://github.com/fleeksoft/ksoup)!

## Installation

Add the following dependency into your project's `build.gradle.kts`/`build.gradle` file:

```kotlin
dependencies {
  implementation("dev.burnoo.kspoon:kspoon:0.0.1-SNAPSHOT")
}
```

Above library uses [kotlinx-io](https://github.com/Kotlin/kotlinx-io) and [Ktor 3.x](https://github.com/ktorio/ktor),
but it has other variants that can be used.
Check [Ksoup README](https://github.com/fleeksoft/ksoup?tab=readme-ov-file#ksoup-is-published-on-maven-central)
for more details. Other variants:

- `dev.burnoo.kspoon:kspoon-korlibs`
- `dev.burnoo.kspoon:kspoon-ktor2` (should be used when ktor2 is used in project, wasm js is not supported)
- `dev.burnoo.kspoon:kspoon-okio` (wasm js is not supported)

## Usage

kspoon works on any serializable class. Adding `@Selector` annotations on its serializable fields, enables HTML parsing:

```kotlin
@Serializable
data class Page(
  @Selector("#header") val header: String,
  @Selector("li.class1") val intList: List<Int>,
  @Selector(value = "#image1", attr = "src") val imageSource: String,
)
```

Then `Kspoon` instance can be used to create objects:

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

It looks for the first occurrence with CSS selector in HTML and sets its value to a field.

### Configuration

kspoon can be configured using `Kspoon {}` factory function. It returns kspoon instance that can be used for parsing.
All available options with default values are listed below:

```kotlin
val kspoon = Kspoon {
  // Specifies Ksoup function that is used for parsing. Type: Ksoup.(String) -> Document
  parse = { html: String -> parse(html) }
  // Default text mode using for parsing.
  defaultTextMode = HtmlTextMode.Text
  // Enables coercing values when the selected HTML element is not found.
  coerceInputValues = false
  // Module with contextual and polymorphic serializers to be used.
  serializersModule = EmptySerializersModule()
}
kspoon.parse(HTML_CONTENT)
```

### Selecting content

By default, the HTML's `textContent` value is used to get data. This behavior can be changed either in configuration or
by `textMode` parameter in the `@Selector` annotation. It can be `InnerHtml`, `OuterHtml`
or `Data` (for scripts and styles):
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

It is also possible to get an attribute value by setting an `attr` parameter in the `@Selector` annotation
(see [Usage](#Usage) for the example).

### Regex

Regex can be set up by passing `regex` parameter to the `@Selector` annotation.
Then after parsing text (with html text mode or attribute), regex will be applied on the string.
The returned string will be the first matched group or the whole match, if no group was added.

```kotlin
data class Page(
  @Selector(value = "#numbers", regex = "([0-9]+) ")
  val starNumber: Int // <span id="numbers">31 stars</span> (31 will be parsed)
)
```

### Default values

There are 3 ways to set default values:

- `@Selector("#tag", defValue = "default")` - if the HTML element is not found, the default value will be used as a
  parsed string
- nullable field - in case HTML is not found value will be set to null
- `coerceInputValues = true` in `Kspoon {}` configuration - it enables coercing to default value
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

It's worth mentioning that `defValue` has the best performance due to kotlinx serialization internal logic.
Nullable fields does html selecting twice. Coercing input values does html selecting twice and also disables
[sequential decoding](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-core/kotlinx.serialization.encoding/-composite-decoder/decode-sequentially.html).

### Serializers

Any `KSerializer` can be applied to a field annotated with `@Selector` to customize serialization logic.
For example date serializers from [`kotlinx-datetime`](https://github.com/Kotlin/kotlinx-datetime):

```kotlin
@Serializable
data class Model(
  @Serializable(LocalDateIso8601Serializer::class)
  @Selector("span")
  val date: LocalDate,
)
```

Additionally, kspoon has built in serializers for Ksoup classes: `ElementSerializer`, `ElementsSerializer`, and
`DocumentSerializer`.
They can be used directly, or by using contextual serialization:

```kotlin
@Serializable
data class Model(
  @Serializable(ElementSerializer::class) // or @Contextual
  @Selector("div.class1")
  val element: Element,
)
```

It is also possible to write custom kspoon serializers that can access selected `Element`.
Read more here.

### External libraries

The `Kspoon` class has `toFormat(): StringFormat` function that can be used with 3rd party libraries.
For detailed integration instructions see following links:

- ktor
- Retrofit

### Supported targets

- All variants: `jvm`, `js`, `linuxX64`, `linuxArm64`, `tvosArm64`, `tvosX64`, `tvosSimulatorArm64`, `macosX64`,
  `macosArm64`, `iosArm64`, `iosSimulatorArm64`, `iosX64`, `mingwX64`
- Only default and `korlibs` variants: `wasmjs`

### Custom serializers

### jspoon compatibility

### Changelog

See [GitHub releases](https://github.com/burnoo/kspoon/releases)
