package io.github.skypurl.wyziesubs.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiException")
class ApiExceptionTest {

    @Test
    @DisplayName("constructor should retain status code and response body")
    void constructor_retainsStatusCodeAndResponseBody() {
        ApiException exception = new ApiException(404, "{\"error\":\"Not Found\"}");

        assertEquals(404, exception.getStatusCode());
        assertEquals("{\"error\":\"Not Found\"}", exception.getResponseBody());
        assertTrue(exception.getMessage().contains("404"));
        assertTrue(exception.getMessage().contains("Not Found"));
    }
}