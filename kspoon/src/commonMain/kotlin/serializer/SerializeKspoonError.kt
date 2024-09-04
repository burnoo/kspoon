package dev.burnoo.ksoup.serializer

import dev.burnoo.ksoup.exception.kspoonError

internal fun kspoonEncodeError(): Nothing {
    kspoonError("Encoding is not supported by kspoon")
}
