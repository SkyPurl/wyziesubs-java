package io.github.skypurl.wyziesubs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Response from the {@code GET /sources} endpoint.
 * Example: {@code {"sources": ["subdl", "subf2m", "opensubtitles"]}}
 *
 * @param sources List of enabled source names on the API instance.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SourcesResponse(
        List<String> sources
) {}