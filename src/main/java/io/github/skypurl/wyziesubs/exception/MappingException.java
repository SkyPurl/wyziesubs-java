package io.github.skypurl.wyziesubs.exception;

/**
 * Thrown when JSON deserialization fails.
 *
 * <p>Wraps Jackson's {@code JsonProcessingException} so SDK users don't
 * need to handle internal Jackson exceptions in their catch blocks.</p>
 *
 * <p>The original cause remains accessible via {@link #getCause()} for
 * advanced debugging.</p>
 */
public final class MappingException extends WyzieSubsException {

    /**
     * Constructs a {@code MappingException} wrapping a Jackson exception.
     *
     * @param message Error context description (e.g., which endpoint failed).
     * @param cause   The original Jackson exception ({@code JsonProcessingException}).
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}