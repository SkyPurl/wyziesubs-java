package io.github.skypurl.wyziesubs.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WyzieSubsException")
class WyzieSubsExceptionTest {

    @Test
    @DisplayName("constructor should retain the message")
    void constructor_messageOnly_retainsMessage() {
        WyzieSubsException exception = new WyzieSubsException("failure");

        assertEquals("failure", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("constructor should retain the message and cause")
    void constructor_messageAndCause_retainsBoth() {
        RuntimeException cause = new RuntimeException("root cause");

        WyzieSubsException exception = new WyzieSubsException("failure", cause);

        assertEquals("failure", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}