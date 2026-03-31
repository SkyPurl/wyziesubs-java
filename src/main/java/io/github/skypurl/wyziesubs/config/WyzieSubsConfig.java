package io.github.skypurl.wyziesubs.config;

import java.net.http.HttpClient;
import java.util.Objects;

/**
 * Immutable configuration for the {@link io.github.skypurl.wyziesubs.WyzieSubsClient}.
 *
 * <p>This class uses a strict Builder pattern. Usage examples:</p>
 * <pre>{@code
 * // Quick setup with default values
 * WyzieSubsConfig config = WyzieSubsConfig.defaultWithApiKey("my-api-key");
 *
 * // Advanced configuration via Builder
 * WyzieSubsConfig config = WyzieSubsConfig.builder("my-api-key")
 *         .baseUrl("https://custom.instance.io/api")
 *         .httpClient(myCustomHttpClient)
 *         .build();
 * }</pre>
 */
public final class WyzieSubsConfig {

    private static final String DEFAULT_BASE_URL = "https://sub.wyzie.io";

    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;

    private WyzieSubsConfig(Builder builder) {
        this.apiKey     = Objects.requireNonNull(builder.apiKey,     "apiKey must not be null");
        this.baseUrl    = Objects.requireNonNull(builder.baseUrl,    "baseUrl must not be null");
        this.httpClient = Objects.requireNonNull(builder.httpClient, "httpClient must not be null");
    }

    // ----
    // Factory Methods
    // ----

    /**
     * Creates a configuration instance using default settings and the provided API key.
     *
     * @param apiKey Wyzie Subs API key (must not be null).
     * @return A ready-to-use {@code WyzieSubsConfig} instance.
     */
    public static WyzieSubsConfig defaultWithApiKey(String apiKey) {
        return builder(apiKey).build();
    }

    // ----
    // Builder Entry Point
    // ----

    /**
     * Returns a new {@link Builder} initialized with the required API key.
     *
     * @param apiKey Wyzie Subs API key (must not be null).
     * @return A {@link Builder} pre-configured with default values.
     */
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    // ----
    // Getters
    // ----

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    // ----
    // Builder Implementation
    // ----

    /**
     * Immutable builder for {@link WyzieSubsConfig}.
     * Default values are applied upon builder initialization.
     */
    public static final class Builder {

        private final String apiKey;
        private String baseUrl    = DEFAULT_BASE_URL;
        private HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        private Builder(String apiKey) {
            this.apiKey = apiKey;
        }

        /**
         * Overrides the default base URL. Useful for self-hosted instances.
         *
         * @param baseUrl API base URL (e.g., {@code "https://my-instance.io"}).
         * @return {@code this} for method chaining.
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Provides a custom {@link HttpClient} for specific needs (proxy, SSL, timeouts, etc.).
         *
         * @param httpClient The {@link HttpClient} instance to use.
         * @return {@code this} for method chaining.
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Builds and returns the immutable {@link WyzieSubsConfig} instance.
         *
         * @return A new {@link WyzieSubsConfig} instance.
         * @throws NullPointerException if {@code apiKey} is null.
         */
        public WyzieSubsConfig build() {
            return new WyzieSubsConfig(this);
        }
    }
}