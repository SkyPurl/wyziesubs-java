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
 * Asynchronous implementation of {@link WyzieSubsClient} based on
 * the native {@link java.net.http.HttpClient} (Java 11+).
 *
 * <p>All requests are executed in a non-blocking manner using
 * {@code sendAsync()}. Java 25 Virtual Threads maximize
 * throughput without OS thread overhead.</p>
 *
 * <p>Instantiation:</p>
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
     * Creates a new client with the provided configuration.
     *
     * @param config Client configuration (must not be null).
     * @throws NullPointerException if {@code config} is null.
     */
    public DefaultWyzieSubsClient(WyzieSubsConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    // ----
    // getEnabledSources
    // ----

    /**
     * {@inheritDoc}
     *
     * <p>Endpoint: {@code GET {baseUrl}/sources}</p>
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

    // ----
    // search
    // ----

    /**
     * {@inheritDoc}
     *
     * <p>Endpoint: {@code GET {baseUrl}/search?id=...&key=...}</p>
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

    // ----
    // download
    // ----

    /**
     * {@inheritDoc}
     *
     * <p>The file is written directly to disk via
     * {@link HttpResponse.BodyHandlers#ofFile(Path)} to minimize memory footprint.</p>
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

    // ----
    // Private Utility Methods
    // ----

    /**
     * Validates that the HTTP status code is within the 2xx range.
     * Throws an {@link ApiException} otherwise.
     *
     * @param response The HTTP response to validate.
     * @param <T>      The response body type.
     * @throws ApiException if the HTTP status is outside the 2xx range.
     */
    private <T> void validateStatus(HttpResponse<T> response) {
        int status = response.statusCode();
        if (status < 200 || status > 299) {
            String body = response.body() != null ? response.body().toString() : "";
            throw new ApiException(status, body);
        }
    }
}