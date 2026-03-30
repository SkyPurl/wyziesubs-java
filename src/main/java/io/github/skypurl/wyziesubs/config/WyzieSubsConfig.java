package io.github.skypurl.wyziesubs.config;

import java.net.http.HttpClient;
import java.util.Objects;

/**
 * Configuration immuable du client {@link io.github.skypurl.wyziesubs.WyzieSubsClient}.
 *
 * <p>Utilise un pattern Builder strict. Exemple d'utilisation :</p>
 * <pre>{@code
 * // Méthode rapide (valeurs par défaut)
 * WyzieSubsConfig config = WyzieSubsConfig.defaultWithApiKey("my-api-key");
 *
 * // Méthode Builder (configuration avancée)
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

    // -------------------------------------------------------------------------
    // Méthode de commodité
    // -------------------------------------------------------------------------

    /**
     * Crée une configuration avec les valeurs par défaut et l'API key fournie.
     *
     * @param apiKey Clé d'API Wyzie Subs (non nulle).
     * @return Une instance {@code WyzieSubsConfig} prête à l'emploi.
     */
    public static WyzieSubsConfig defaultWithApiKey(String apiKey) {
        return builder(apiKey).build();
    }

    // -------------------------------------------------------------------------
    // Point d'entrée du Builder
    // -------------------------------------------------------------------------

    /**
     * Retourne un nouveau {@link Builder} initialisé avec l'API key obligatoire.
     *
     * @param apiKey Clé d'API Wyzie Subs (non nulle).
     * @return Un {@link Builder} pré-configuré avec les valeurs par défaut.
     */
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /**
     * Builder immuable pour {@link WyzieSubsConfig}.
     * Les valeurs par défaut sont appliquées à l'initialisation du Builder.
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
         * Surcharge l'URL de base (utile pour les instances auto-hébergées).
         *
         * @param baseUrl URL de base de l'API (ex: {@code "https://my-instance.io"}).
         * @return {@code this} pour le chaînage.
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Fournit un {@link HttpClient} personnalisé (proxy, SSL, timeouts...).
         *
         * @param httpClient Instance {@link HttpClient} à utiliser.
         * @return {@code this} pour le chaînage.
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Construit et retourne l'instance {@link WyzieSubsConfig} immuable.
         *
         * @return Une nouvelle instance {@link WyzieSubsConfig}.
         * @throws NullPointerException si {@code apiKey} est nulle.
         */
        public WyzieSubsConfig build() {
            return new WyzieSubsConfig(this);
        }
    }
}