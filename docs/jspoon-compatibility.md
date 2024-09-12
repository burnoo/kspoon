# jspoon compatibility
I've created [jspoon](https://github.com/DroidsOnRoids/jspoon) in 2017. A lot of things changed since then, but I've tried to make kspoon more-less compatible with jspoon. However, there are a few breaking changes.

## Prerequisites
jspoon was working with Java and Kotlin JVM, kspoon is working with Kotlin Multiplatform only, so kspoon won't work with code written in Java - migration is possible in Kotlin JVM projects

## Breaking changes
- `@Selector.attr = "innerhtml"` (or `"html"`/`"outerHtml"`) was replaced with `@Selector.textMode = SelectorHtmlTextMode.InnerHtml` (or `SelectorHtmlTextMode.Text`/`SelectorHtmlTextMode.OuterHtml`) this fixes edge cases where selecting of `<tag html="value" />` wasn't parsed correctly (and also having enum is nicer API). See [Selecting content](/README.md#selecting-content)
- `@Format` annotation is not supported by kspoon. `KSerializer<T>` can be used instead to specify format for parsing. See [Serializers](/README.md#Serializers).
- Custom converters are not supported by kspoon. `KSerilizers<T>` with `KspoonDecoder` can be used instead. See [Custom serializers](custom-serializers.md).
- Different parsing API - instead of `Jspoon.create()`, `jspoon.adapter` and `htmlAdapter.fromHtml` use `Kspoon.parse`. See [Usage](/README.md#usage)
- Retrofit integration is different. See [Retrofit integration](retrofit.md)

## Migration guide
1. Replace jspoon dependency with kspoon and apply serialization plugin (see [Installation](/README.md#installation)). See [Selecting content](/README.md#selecting-content)
2. Add `@Serializable` to each class that should be parsed.
3. Replace `import pl.droidsonroids.jspoon.annotation.Selector` with `import dev.burnoo.kspoon.annotation.Selector`
4. Replace usages of `@Selector.attr = "innerhtml"` (or `"html"`/`"outerHtml"`) with `@Selector.textMode = SelectorHtmlTextMode.InnerHtml` (or `SelectorHtmlTextMode.OuterHtml`). See [Selecting content](/README.md#selecting-content)
5. Replace `@Format` with `KSerializer<T>` implementation. See [Serializers](/README.md##Serializers)
6. Replace usages of `Jspoon.create()`, `jspoon.adapter` and `htmlAdapter.fromHtml` with `Kspoon.parse`. See [Usage](/README.md#usage)
7. Update integration with Retrofit if used. See [Retrofit integration](retrofit.md)
8. Replace custom converters with `KSerialzier<T>` with `KspoonDecoder`. See [Custom serializers](custom-serializers.md)
