package dev.burnoo.ksoup.configuration

import dev.burnoo.ksoup.Kspoon
import kotlinx.serialization.modules.SerializersModule

class KspoonBuilder internal constructor(kspoon: Kspoon) {
    var defaultTextMode = kspoon.configuration.defaultTextMode
    var parse = kspoon.configuration.parse
    var serializersModule: SerializersModule = kspoon.serializersModule

    internal fun build() = KspoonConfiguration(defaultTextMode, parse)
}
