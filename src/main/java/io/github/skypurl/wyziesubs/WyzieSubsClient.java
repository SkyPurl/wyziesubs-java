package io.github.skypurl.wyziesubs;

import io.github.skypurl.wyziesubs.model.SourcesResponse;
import io.github.skypurl.wyziesubs.model.Subtitle;
import io.github.skypurl.wyziesubs.request.SearchRequest;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface principale du SDK WyzieSubs.
 *
 * <p>Toutes les opérations sont asynchrones et retournent des
 * {@link CompletableFuture}. Les erreurs HTTP sont propagées via
 * {@link io.github.skypurl.wyziesubs.exception.ApiException} et les erreurs
 * de désérialisation via {@link io.github.skypurl.wyziesubs.exception.MappingException}.</p>
 *
 * <p>Exemple d'utilisation :</p>
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
     * Récupère la liste des sources de sous-titres activées sur l'instance API.
     *
     * @return Un {@link CompletableFuture} contenant la {@link SourcesResponse}.
     */
    CompletableFuture<SourcesResponse> getEnabledSources();

    /**
     * Recherche des sous-titres correspondant aux critères de la requête.
     *
     * @param request Les critères de recherche (voir {@link SearchRequest}).
     * @return Un {@link CompletableFuture} contenant la liste des {@link Subtitle} trouvés.
     */
    CompletableFuture<List<Subtitle>> search(SearchRequest request);

    /**
     * Télécharge un fichier de sous-titres directement sur le disque.
     *
     * @param subtitle    Le sous-titre à télécharger (doit avoir une {@code url} non nulle).
     * @param destination Le {@link Path} de destination sur le système de fichiers.
     * @return Un {@link CompletableFuture} contenant le {@link Path} du fichier téléchargé.
     */
    CompletableFuture<Path> download(Subtitle subtitle, Path destination);
}