package dev.burnoo.kspoon

import dev.burnoo.kspoon.serializer.kspoonEncodeError
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule

internal class KspoonFormat(
    private val kspoon: Kspoon,
    override val serializersModule: SerializersModule,
    private val encodeStringFormatDelegate: StringFormat?,
) : StringFormat {

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return kspoon.parse(deserializer, string)
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return encodeStringFormatDelegate?.encodeToString(serializer, value)
            ?: kspoonEncodeError()
    }
}
