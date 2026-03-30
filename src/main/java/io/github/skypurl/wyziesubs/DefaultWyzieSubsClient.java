package io.github.skypurl.wyziesubs;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.skypurl.wyziesubs.config.WyzieSubsConfig;
import io.github.skypurl.wyziesubs.exception.ApiException;
import io.github.skypurl.wyziesubs.model.SourcesResponse;
import io.github.skypurl.wyziesubs.model.Subtitle;
import io.github.skypurl.wyziesubs.request.SearchRequest;
import io.github.skypurl.wyziesubs.util.JsonMapper;
import io.github.skypurl.wyziesubs.util.UrlBuilder;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Implémentation asynchrone de {@link WyzieSubsClient} basée sur
 * {@link java.net.http.HttpClient} natif (Java 11+).
 *
 * <p>Toutes les requêtes sont exécutées de manière non-bloquante via
 * {@code sendAsync()}. Les Virtual Threads de Java 25 maximisent le
 * débit sans overhead de threads OS.</p>
 *
 * <p>Instanciation :</p>
 * <pre>{@code
 * WyzieSubsClient client = new DefaultWyzieSubsClient(
 *         WyzieSubsConfig.defaultWithApiKey("my-api-key")
 * );
 * }</pre>
 */
public final class DefaultWyzieSubsClient implements WyzieSubsClient {

    private static final String HEADER_ACCEPT      = "Accept";
    private static final String MIME_JSON          = "application/json";

    private final WyzieSubsConfig config;

    /**
     * Crée un nouveau client avec la configuration fournie.
     *
     * @param config Configuration du client (non nulle).
     * @throws NullPointerException si {@code config} est nulle.
     */
    public DefaultWyzieSubsClient(WyzieSubsConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    // -------------------------------------------------------------------------
    // getEnabledSources
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Appel : {@code GET {baseUrl}/sources}</p>
     */
    @Override
    public CompletableFuture<SourcesResponse> getEnabledSources() {
        URI uri = URI.create(config.getBaseUrl() + "/sources");

        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .header(HEADER_ACCEPT, MIME_JSON)
                .build();

        return config.getHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    validateStatus(response);
                    return JsonMapper.fromJson(response.body(), SourcesResponse.class);
                });
    }

    // -------------------------------------------------------------------------
    // search
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Appel : {@code GET {baseUrl}/search?id=...&key=...}</p>
     */
    @Override
    public CompletableFuture<List<Subtitle>> search(SearchRequest searchRequest) {
        Objects.requireNonNull(searchRequest, "searchRequest must not be null");

        UrlBuilder urlBuilder = UrlBuilder.of(config.getBaseUrl() + "/search");
        searchRequest.populate(urlBuilder);
        urlBuilder.addQueryParam("key", config.getApiKey());

        HttpRequest request = HttpRequest.newBuilder(urlBuilder.build())
                .GET()
                .header(HEADER_ACCEPT, MIME_JSON)
                .build();

        return config.getHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    validateStatus(response);
                    return JsonMapper.fromJson(response.body(), new TypeReference<List<Subtitle>>() {});
                });
    }

    // -------------------------------------------------------------------------
    // download
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Le fichier est écrit directement sur le disque via
     * {@link HttpResponse.BodyHandlers#ofFile(Path)} sans passer en mémoire.</p>
     */
    @Override
    public CompletableFuture<Path> download(Subtitle subtitle, Path destination) {
        Objects.requireNonNull(subtitle,    "subtitle must not be null");
        Objects.requireNonNull(destination, "destination must not be null");
        Objects.requireNonNull(subtitle.url(),
                "subtitle.url() must not be null — cannot download a subtitle without a URL");

        HttpRequest request = HttpRequest.newBuilder(URI.create(subtitle.url()))
                .GET()
                .build();

        return config.getHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofFile(destination))
                .thenApply(response -> {
                    validateStatus(response);
                    return response.body();
                });
    }

    // -------------------------------------------------------------------------
    // Méthode utilitaire privée
    // -------------------------------------------------------------------------

    /**
     * Valide que le code de statut HTTP est dans la plage 2xx.
     * Lance une {@link ApiException} dans le cas contraire.
     *
     * @param response La réponse HTTP à valider.
     * @param <T>      Type du body de la réponse.
     * @throws ApiException si le statut HTTP est hors de la plage 2xx.
     */
    private <T> void validateStatus(HttpResponse<T> response) {
        int status = response.statusCode();
        if (status < 200 || status > 299) {
            String body = response.body() != null ? response.body().toString() : "";
            throw new ApiException(status, body);
        }
    }
}