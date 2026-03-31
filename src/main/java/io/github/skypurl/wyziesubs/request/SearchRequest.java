package io.github.skypurl.wyziesubs.request;

import io.github.skypurl.wyziesubs.enums.Language;
import io.github.skypurl.wyziesubs.enums.MediaOrigin;
import io.github.skypurl.wyziesubs.enums.SubtitleEncoding;
import io.github.skypurl.wyziesubs.enums.SubtitleFormat;
import io.github.skypurl.wyziesubs.enums.SubtitleSource;
import io.github.skypurl.wyziesubs.util.ApiParameter;
import io.github.skypurl.wyziesubs.util.UrlBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents an immutable subtitle search request.
 *
 * <p>Uses a strict Builder pattern. Only {@code id} is mandatory.
 * Usage example:</p>
 * <pre>{@code
 * SearchRequest request = SearchRequest.builder("tt1234567")
 *         .languages(Language.FRENCH, Language.ENGLISH)
 *         .formats(SubtitleFormat.SRT)
 *         .sources(SubtitleSource.OPENSUBTITLES, SubtitleSource.SUBDL)
 *         .hi(false)
 *         .build();
 * }</pre>
 */
public final class SearchRequest {

    private final String id;
    private final Integer season;
    private final Integer episode;
    private final List<Language> languages;
    private final List<SubtitleFormat> formats;
    private final Boolean hi;
    private final SubtitleEncoding encoding;
    private final List<SubtitleSource> sources;
    private final List<String> releases;
    private final String fileName;
    private final List<MediaOrigin> origins;
    private final Boolean refresh;

    private SearchRequest(Builder builder) {
        this.id        = Objects.requireNonNull(builder.id, "id must not be null");
        this.season    = builder.season;
        this.episode   = builder.episode;
        this.languages = List.copyOf(builder.languages);
        this.formats   = List.copyOf(builder.formats);
        this.hi        = builder.hi;
        this.encoding  = builder.encoding;
        this.sources   = List.copyOf(builder.sources);
        this.releases  = List.copyOf(builder.releases);
        this.fileName  = builder.fileName;
        this.origins   = List.copyOf(builder.origins);
        this.refresh   = builder.refresh;
    }

    // ----
    // Builder Entry Point
    // ----

    /**
     * Returns a new {@link Builder} with the required media identifier.
     *
     * @param id IMDb or TMDB media identifier (e.g., {@code "tt1234567"}).
     * @return A {@link Builder} instance ready for configuration.
     */
    public static Builder builder(String id) {
        return new Builder(id);
    }

    // ----
    // UrlBuilder Conversion
    // ----

    /**
     * Populates a {@link UrlBuilder} with all non-null parameters from this request.
     *
     * <p>Enum lists are joined by commas using their {@link ApiParameter#getValue()}
     * method (e.g., {@code "en,fr"}).</p>
     *
     * @param urlBuilder The {@link UrlBuilder} to populate.
     */
    public void populate(UrlBuilder urlBuilder) {
        urlBuilder.addQueryParam("id",       id);
        urlBuilder.addQueryParam("season",   season   != null ? season.toString()  : null);
        urlBuilder.addQueryParam("episode",  episode  != null ? episode.toString() : null);
        urlBuilder.addQueryParam("language", joinParameters(languages));
        urlBuilder.addQueryParam("format",   joinParameters(formats));
        urlBuilder.addQueryParam("hi",       hi       != null ? hi.toString()      : null);
        urlBuilder.addQueryParam("encoding", encoding != null ? encoding.getValue() : null);
        urlBuilder.addQueryParam("source",   joinParameters(sources));
        urlBuilder.addQueryParam("fileName", fileName);
        urlBuilder.addQueryParam("origin",   joinParameters(origins));
        urlBuilder.addQueryParam("refresh",  refresh  != null ? refresh.toString() : null);

        // Releases are raw Strings, not ApiParameters
        if (!releases.isEmpty()) {
            urlBuilder.addQueryParam("releases", String.join(",", releases));
        }
    }

    // ----
    // Private Utility Methods
    // ----

    /**
     * Joins values from a list of {@link ApiParameter} with commas.
     * Returns {@code null} if the list is empty (ignored by {@link UrlBuilder}).
     *
     * @param params List of parameters (enums implementing {@link ApiParameter}).
     * @param <T>    Type bounded to {@link ApiParameter}.
     * @return Joined string (e.g., {@code "en,fr"}) or {@code null}.
     */
    private <T extends ApiParameter> String joinParameters(List<T> params) {
        if (params.isEmpty()) {
            return null;
        }
        return params.stream()
                .map(ApiParameter::getValue)
                .collect(Collectors.joining(","));
    }

    // ----
    // Getters
    // ----

    public String getId()                    { return id;        }
    public Integer getSeason()               { return season;    }
    public Integer getEpisode()              { return episode;   }
    public List<Language> getLanguages()     { return languages; }
    public List<SubtitleFormat> getFormats() { return formats;   }
    public Boolean getHi()                   { return hi;        }
    public SubtitleEncoding getEncoding()    { return encoding;  }
    public List<SubtitleSource> getSources() { return sources;   }
    public List<String> getReleases()        { return releases;  }
    public String getFileName()              { return fileName;  }
    public List<MediaOrigin> getOrigins()    { return origins;   }
    public Boolean getRefresh()              { return refresh;   }

    // ----
    // Builder Implementation
    // ----

    public static final class Builder {

        private final String id;
        private Integer season;
        private Integer episode;
        private final List<Language> languages       = new ArrayList<>();
        private final List<SubtitleFormat> formats   = new ArrayList<>();
        private Boolean hi;
        private SubtitleEncoding encoding;
        private final List<SubtitleSource> sources   = new ArrayList<>();
        private final List<String> releases          = new ArrayList<>();
        private String fileName;
        private final List<MediaOrigin> origins      = new ArrayList<>();
        private Boolean refresh;

        private Builder(String id) {
            this.id = id;
        }

        public Builder season(int season)           { this.season = season;     return this; }
        public Builder episode(int episode)         { this.episode = episode;   return this; }
        public Builder hi(boolean hi)               { this.hi = hi;             return this; }
        public Builder encoding(SubtitleEncoding encoding) { this.encoding = encoding; return this; }
        public Builder fileName(String fileName)    { this.fileName = fileName; return this; }
        public Builder refresh(boolean refresh)     { this.refresh = refresh;   return this; }

        public Builder languages(Language... langs) {
            languages.addAll(List.of(langs));
            return this;
        }

        public Builder formats(SubtitleFormat... fmts) {
            formats.addAll(List.of(fmts));
            return this;
        }

        public Builder sources(SubtitleSource... srcs) {
            sources.addAll(List.of(srcs));
            return this;
        }

        public Builder releases(String... rels) {
            releases.addAll(List.of(rels));
            return this;
        }

        public Builder origins(MediaOrigin... origs) {
            origins.addAll(List.of(origs));
            return this;
        }

        /**
         * Builds and returns the immutable {@link SearchRequest} instance.
         *
         * @return A new {@link SearchRequest} instance.
         * @throws NullPointerException if {@code id} is null.
         */
        public SearchRequest build() {
            return new SearchRequest(this);
        }
    }
}