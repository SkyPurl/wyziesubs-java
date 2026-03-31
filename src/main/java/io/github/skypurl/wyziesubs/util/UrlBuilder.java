package io.github.skypurl.wyziesubs.util;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/**
 * Fluent and safe URL builder for API requests.
 *
 * <p>Automatically encodes keys and values in UTF-8 using {@link URLEncoder}.
 * Parameters with {@code null} values are silently ignored.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
 *         .addQueryParam("id", "tt1234567")
 *         .addQueryParam("language", "en,fr")
 *         .build();
 * // → https://sub.wyzie.io/search?id=tt1234567&language=en%2Cfr
 * }</pre>
 */
public final class UrlBuilder {

    private final String baseUrl;
    private final StringJoiner queryJoiner = new StringJoiner("&");

    private UrlBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Creates a new {@code UrlBuilder} from a base URL.
     *
     * @param baseUrl Base URL (e.g., {@code "https://sub.wyzie.io/search"}).
     * @return A new {@code UrlBuilder} instance.
     */
    public static UrlBuilder of(String baseUrl) {
        return new UrlBuilder(baseUrl);
    }

    /**
     * Adds a UTF-8 encoded query parameter.
     * If {@code value} is {@code null}, the call is silently ignored.
     *
     * @param key   Parameter name (will be encoded).
     * @param value Parameter value (will be encoded). {@code null} = ignored.
     * @return {@code this} for method chaining.
     */
    public UrlBuilder addQueryParam(String key, String value) {
        if (value == null) {
            return this;
        }
        String encodedKey   = URLEncoder.encode(key,   StandardCharsets.UTF_8);
        String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
        queryJoiner.add(encodedKey + "=" + encodedValue);
        return this;
    }

    /**
     * Builds and returns the final URI.
     * The {@code ?} separator is only added if at least one parameter is present.
     *
     * @return {@link URI} ready for {@link java.net.http.HttpClient}.
     */
    public URI build() {
        String query = queryJoiner.toString();
        String fullUrl = query.isEmpty() ? baseUrl : baseUrl + "?" + query;
        return URI.create(fullUrl);
    }
}