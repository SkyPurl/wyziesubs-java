package io.github.skypurl.wyziesubs;

import io.github.skypurl.wyziesubs.model.SourcesResponse;
import io.github.skypurl.wyziesubs.model.Subtitle;
import io.github.skypurl.wyziesubs.request.SearchRequest;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main interface for the WyzieSubs SDK.
 *
 * <p>All operations are asynchronous and return {@link CompletableFuture}.
 * HTTP errors are propagated via {@link io.github.skypurl.wyziesubs.exception.ApiException}
 * and deserialization errors via {@link io.github.skypurl.wyziesubs.exception.MappingException}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * WyzieSubsConfig config = WyzieSubsConfig.defaultWithApiKey("my-api-key");
 * WyzieSubsClient client = new DefaultWyzieSubsClient(config);
 *
 * client.search(SearchRequest.builder("tt0369610")
 *               .languages(Language.FRENCH)
 *               .formats(SubtitleFormat.SRT)
 *               .build())
 *       .thenAccept(subtitles -> subtitles.forEach(System.out::println))
 *       .join();
 * }</pre>
 */
public interface WyzieSubsClient {

    /**
     * Retrieves the list of subtitle sources enabled on the API instance.
     *
     * @return A {@link CompletableFuture} containing the {@link SourcesResponse}.
     */
    CompletableFuture<SourcesResponse> getEnabledSources();

    /**
     * Searches for subtitles matching the request criteria.
     *
     * @param request Search criteria (see {@link SearchRequest}).
     * @return A {@link CompletableFuture} containing the list of found {@link Subtitle}.
     */
    CompletableFuture<List<Subtitle>> search(SearchRequest request);

    /**
     * Downloads a subtitle file directly to disk.
     *
     * @param subtitle    The subtitle to download (must have a non-null {@code url}).
     * @param destination The destination {@link Path} on the file system.
     * @return A {@link CompletableFuture} containing the {@link Path} of the downloaded file.
     */
    CompletableFuture<Path> download(Subtitle subtitle, Path destination);
}