package dev.burnoo.ksoup.exception

class KspoonParseException(
    override val message: String?,
    override val cause: Throwable?,
) : IllegalStateException()

internal fun kspoonError(message: String?, cause: Throwable? = null): Nothing {
    throw KspoonParseException(message, cause)
}
