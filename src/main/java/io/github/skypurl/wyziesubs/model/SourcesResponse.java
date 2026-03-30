package io.github.skypurl.wyziesubs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Réponse de l'endpoint {@code GET /sources}.
 * Exemple : {@code {"sources": ["subdl", "subf2m", "opensubtitles"]}}
 *
 * @param sources Liste des noms de sources activées sur l'instance API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SourcesResponse(
        List<String> sources
) {}