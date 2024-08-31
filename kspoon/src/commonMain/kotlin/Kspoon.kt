@file:Suppress("FunctionName")

package dev.burnoo.ksoup

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.configuration.KspoonBuilder
import dev.burnoo.ksoup.configuration.KspoonConfiguration
import dev.burnoo.ksoup.decoder.HtmlTreeDecoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer

sealed class Kspoon(val configuration: KspoonConfiguration) {

    companion object Default : Kspoon(KspoonConfiguration())

    fun <T> parse(deserializer: DeserializationStrategy<T>, string: String): T {
        val document = configuration.parse(Ksoup, string)
        val decoder = HtmlTreeDecoder(
            elements = Elements(document),
            configuration = configuration,
            extraSerializersModule = configuration.serializersModule,
        )
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> parse(string: String): T = parse(configuration.serializersModule.serializer(), string)

    fun toFormat(encodeStringFormatDelegate: StringFormat? = null) = KspoonFormat(
        kspoon = this,
        serializersModule = configuration.serializersModule,
        encodeStringFormatDelegate = encodeStringFormatDelegate,
    )
}

fun Kspoon(from: Kspoon = Kspoon.Default, builderAction: KspoonBuilder.() -> Unit): Kspoon {
    val builder = KspoonBuilder(from)
    builder.builderAction()
    val configuration = builder.build()
    return KspoonImpl(configuration)
}

private class KspoonImpl(configuration: KspoonConfiguration) : Kspoon(configuration)
