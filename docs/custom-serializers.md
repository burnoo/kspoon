# Custom serializers
kspoon supports custom HTML serializers by exposing public [`KspoonDecoder`](/kspoon/src/commonMain/kotlin/decoder/KspoonDecoder.kt) that can be used to obtain
Ksoup classes (`Element`, `Elements` and `Document`). This allows to extract HTML elements and attributes that are not supported directly by kspoon (for example comments or tag names).

Here is an example of a custom serializer that extracts the tag name of the selected element:
```kotlin
object TagSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TagString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val kspoonDecoder = decoder as KspoonDecoder
        val element = decoder.decodeElement()
        val tag = element?.tag()?.name
            ?: throw KspoonParseException("Could not get tag name for selector: ${kspoonDecoder.getSelectorFullPath()}")
        return tag
    }

    override fun serialize(encoder: Encoder, value: String) {
        throw KspoonParseException("Serialization is not supported")
    }
}
```

or to get comments:
```kotlin
object CommentsSerializer : KSerializer<List<String>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CommentStringList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        val kspoonDecoder = decoder as KspoonDecoder
        val element = decoder.decodeElementOrThrow()
        return element.nodeStream().filterIsInstance<Comment>().map { it.data }.toList()
    }

    override fun serialize(encoder: Encoder, value: String) {
        throw KspoonParseException("Serialization is not supported")
    }
}
```
