package io.github.skypurl.wyziesubs.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MappingException")
class MappingExceptionTest {

    @Test
    @DisplayName("constructor should retain the message and cause")
    void constructor_retainsMessageAndCause() {
        RuntimeException cause = new RuntimeException("invalid json");

        MappingException exception = new MappingException("Failed to deserialize payload", cause);

        assertEquals("Failed to deserialize payload", exception.getMessage());
        assertSame(cause, exception.getCause());
        assertInstanceOf(WyzieSubsException.class, exception);
    }
}