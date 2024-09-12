package dev.burnoo.kspoon.serializer

import dev.burnoo.kspoon.exception.kspoonError

internal fun kspoonEncodeError(): Nothing {
    kspoonError("Encoding is not supported by kspoon")
}
