package io.github.skypurl.wyziesubs.exception;

/**
 * Base exception for the WyzieSubs SDK.
 *
 * <p>All SDK-specific exceptions extend this class, allowing users to
 * catch all SDK errors with a single {@code catch (WyzieSubsException e)} block.</p>
 */
public class WyzieSubsException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message Error description.
     */
    public WyzieSubsException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message Error description.
     * @param cause   The underlying cause of the error.
     */
    public WyzieSubsException(String message, Throwable cause) {
        super(message, cause);
    }
}