# jspoon compatibility

I've created [jspoon](https://github.com/DroidsOnRoids/jspoon) in 2017. A lot has changed since then, but I've tried to make kspoon mostly compatible with jspoon. However, there are a few breaking changes.

## Prerequisites

jspoon was compatible with Java and Kotlin JVM. kspoon, on the other hand, is compatible with Kotlin Multiplatform only, so it won't work with code written in Java. Migration is possible for Kotlin JVM projects.

## Breaking changes

- `@Selector.attr = "innerhtml"` (or `"html"`/`"outerHtml"`) has been replaced with `@Selector.textMode = SelectorHtmlTextMode.InnerHtml` (or `SelectorHtmlTextMode.Text`/`SelectorHtmlTextMode.OuterHtml`). This change fixes edge cases where selecting `<tag html="value" />` wasn't parsed correctly (and using an enum provides a nicer API). See [Selecting content](/README.md#selecting-content).
- The `@Format` annotation is not supported by kspoon. Instead, `KSerializer<T>` can be used to specify the format for parsing. See [Serializers](/README.md#serializers).
- Custom converters are not supported by kspoon. `KSerializers<T>` with `KspoonDecoder` can be used instead. See [Custom serializers](custom-serializers.md).
- Different parsing API: instead of `Jspoon.create()`, `jspoon.adapter`, and `htmlAdapter.fromHtml`, use `Kspoon.parse`. See [Usage](/README.md#usage).
- Retrofit integration is different. See [Retrofit integration](retrofit.md).

## Migration guide

1. Replace jspoon dependency with kspoon and apply the serialization plugin (see [Installation](/README.md#installation)). See [Selecting content](/README.md#selecting-content).
2. Add `@Serializable` to each class that should be parsed.
3. Replace `import pl.droidsonroids.jspoon.annotation.Selector` with `import dev.burnoo.kspoon.annotation.Selector`.
4. Replace usages of `@Selector.attr = "innerhtml"` (or `"html"`/`"outerHtml"`) with `@Selector.textMode = SelectorHtmlTextMode.InnerHtml` (or `SelectorHtmlTextMode.OuterHtml`). See [Selecting content](/README.md#selecting-content).
5. Replace `@Format` with a `KSerializer<T>` implementation. See [Serializers](/README.md#serializers).
6. Replace usages of `Jspoon.create()`, `jspoon.adapter`, and `htmlAdapter.fromHtml` with `Kspoon.parse`. See [Usage](/README.md#usage).
7. Update integration with Retrofit if used. See [Retrofit integration](retrofit.md).
8. Replace custom converters with `KSerializer<T>` and `KspoonDecoder`. See [Custom serializers](custom-serializers.md).
