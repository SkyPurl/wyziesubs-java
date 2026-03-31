package io.github.skypurl.wyziesubs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a subtitle returned by the {@code GET /search} endpoint.
 *
 * <p>Fields such as {@code format}, {@code source}, and {@code origin} are
 * intentionally typed as {@code String} (rather than enums) to ensure
 * deserialization resilience if the API introduces new values.</p>
 *
 * <p>All fields are nullable except for {@code id} and {@code url}.</p>
 *
 * @param id                Unique identifier for the subtitle.
 * @param url               Direct download URL.
 * @param flagUrl           URL for the language flag icon (may be {@code null}).
 * @param format            File format (e.g., {@code "srt"}, {@code "ass"}).
 * @param encoding          File encoding (e.g., {@code "UTF-8"}).
 * @param display           Display name of the language (e.g., {@code "English"}).
 * @param language          ISO 639-1 language code (e.g., {@code "en"}).
 * @param media             Title of the associated media.
 * @param isHearingImpaired Indicates if the subtitle is for the hearing impaired.
 * @param source            Subtitle provider (e.g., {@code "opensubtitles"}).
 * @param release           Primary associated release.
 * @param releases          List of all compatible releases.
 * @param fileName          Subtitle filename.
 * @param origin            Media origin (e.g., {@code "WEB-DL"}).
 * @param matchedRelease    The specific release that triggered the match.
 * @param matchedFilter     The specific filter that triggered the match.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Subtitle(
        String id,
        String url,
        String flagUrl,
        String format,
        String encoding,
        String display,
        String language,
        String media,
        @JsonProperty("isHearingImpaired") Boolean isHearingImpaired,
        String source,
        String release,
        List<String> releases,
        String fileName,
        String origin,
        String matchedRelease,
        String matchedFilter
) {}