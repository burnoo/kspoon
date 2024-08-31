package dev.burnoo.ksoup.configuration

import dev.burnoo.ksoup.Kspoon
import kotlinx.serialization.modules.SerializersModule

class KspoonBuilder internal constructor(kspoon: Kspoon) {
    var parse = kspoon.configuration.parse
    var defaultTextMode = kspoon.configuration.defaultTextMode
    var coerceInputValues = kspoon.configuration.coerceInputValues
    var encodeStringFormatDelegate = kspoon.configuration.encodeStringFormatDelegate
    var serializersModule: SerializersModule = kspoon.serializersModule

    internal fun build() = KspoonConfiguration(parse, defaultTextMode, coerceInputValues, encodeStringFormatDelegate)
}
