package dev.burnoo.ksoup.exception

/**
 * Exception thrown by Kspoon on errors.
 */
public class KspoonParseException(
    /**
     * Message of the error. Contains full HTML selector path for parsing.
     */
    override val message: String?,
    /**
     * Cause of the exception.
     */
    override val cause: Throwable? = null,
) : IllegalStateException()

internal fun kspoonError(message: String?, cause: Throwable? = null): Nothing {
    throw KspoonParseException(message, cause)
}
