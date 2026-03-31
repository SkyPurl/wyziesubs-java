package io.github.skypurl.wyziesubs.exception;

/**
 * Thrown when the Wyzie Subs API returns an error HTTP response (4xx or 5xx).
 *
 * <p>Usage example in an {@code exceptionally} block:</p>
 * <pre>{@code
 * client.search(request)
 *       .exceptionally(ex -> {
 *           if (ex.getCause() instanceof ApiException apiEx) {
 *               System.err.println("HTTP Status: " + apiEx.getStatusCode());
 *               System.err.println("Response Body: " + apiEx.getResponseBody());
 *           }
 *           return List.of();
 *       });
 * }</pre>
 */
public final class ApiException extends WyzieSubsException {

    private final int statusCode;
    private final String responseBody;

    /**
     * Constructs an {@code ApiException} from an error HTTP response.
     *
     * @param statusCode   HTTP status code (e.g., 401, 404, 500).
     * @param responseBody Raw response body (may be empty, never {@code null}).
     */
    public ApiException(int statusCode, String responseBody) {
        super("API request failed with HTTP %d : %s".formatted(statusCode, responseBody));
        this.statusCode   = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Returns the HTTP status code of the error response.
     *
     * @return HTTP status code (e.g., {@code 401}, {@code 404}, {@code 500}).
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the raw HTTP response body.
     *
     * @return Response body, potentially empty but never {@code null}.
     */
    public String getResponseBody() {
        return responseBody;
    }
}