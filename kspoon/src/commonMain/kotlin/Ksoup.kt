@file:Suppress("FunctionName")

package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.configuration.KspoonBuilder
import dev.burnoo.ksoup.configuration.KspoonConfiguration
import dev.burnoo.ksoup.decoder.HtmlTreeDecoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

sealed class Kspoon(
    val configuration: KspoonConfiguration,
    override val serializersModule: SerializersModule,
) : StringFormat {

    companion object Default : Kspoon(KspoonConfiguration(), EmptySerializersModule())

    final override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val document = configuration.parse(Ksoup, string)
        val decoder = HtmlTreeDecoder(
            elements = Elements(document),
            configuration = configuration,
            extraSerializersModule = serializersModule,
        )
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decodeFromString(string: String): T = decodeFromString(serializersModule.serializer(), string)

    final override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return configuration.encodeStringFormatDelegate?.encodeToString(serializer, value) ?:
            error("Serialization is not supported")
    }
}

fun Kspoon(from: Kspoon = Kspoon.Default, builderAction: KspoonBuilder.() -> Unit): Kspoon {
    val builder = KspoonBuilder(from)
    builder.builderAction()
    val configuration = builder.build()
    return KspoonImpl(configuration, builder.serializersModule)
}

private class KspoonImpl(configuration: KspoonConfiguration, module: SerializersModule) : Kspoon(configuration, module)
