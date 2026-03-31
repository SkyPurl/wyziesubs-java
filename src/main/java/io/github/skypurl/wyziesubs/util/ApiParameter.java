package io.github.skypurl.wyziesubs.util;

/**
 * Common contract for all enums used as API parameters.
 * Allows {@link io.github.skypurl.wyziesubs.request.SearchRequest} to depend
 * solely on this {@code util} package, preventing cyclic dependencies with {@code enums}.
 */
public interface ApiParameter {
    /**
     * Returns the raw string value to be sent to the API.
     *
     * @return API-compatible parameter value.
     */
    String getValue();
}