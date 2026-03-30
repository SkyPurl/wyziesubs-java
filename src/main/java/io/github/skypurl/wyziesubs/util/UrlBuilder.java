package io.github.skypurl.wyziesubs.util;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/**
 * Constructeur d'URL fluide et sécurisé pour les requêtes API.
 *
 * <p>Encode automatiquement les clés et valeurs en UTF-8 via {@link URLEncoder}.
 * Les paramètres à valeur {@code null} sont silencieusement ignorés.</p>
 *
 * <p>Exemple :</p>
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
     * Crée un nouveau {@code UrlBuilder} à partir d'une URL de base.
     *
     * @param baseUrl URL de base (ex: {@code "https://sub.wyzie.io/search"}).
     * @return Une nouvelle instance de {@code UrlBuilder}.
     */
    public static UrlBuilder of(String baseUrl) {
        return new UrlBuilder(baseUrl);
    }

    /**
     * Ajoute un paramètre de requête encodé en UTF-8.
     * Si {@code value} est {@code null}, l'appel est ignoré silencieusement.
     *
     * @param key   Nom du paramètre (sera encodé).
     * @param value Valeur du paramètre (sera encodée). {@code null} = ignoré.
     * @return {@code this} pour le chaînage.
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
     * Construit et retourne l'URI final.
     * Le séparateur {@code ?} n'est ajouté que si au moins un paramètre est présent.
     *
     * @return {@link URI} prêt à être passé à {@link java.net.http.HttpClient}.
     */
    public URI build() {
        String query = queryJoiner.toString();
        String fullUrl = query.isEmpty() ? baseUrl : baseUrl + "?" + query;
        return URI.create(fullUrl);
    }
}