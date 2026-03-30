package io.github.skypurl.wyziesubs.request;

import io.github.skypurl.wyziesubs.enums.Language;
import io.github.skypurl.wyziesubs.enums.MediaOrigin;
import io.github.skypurl.wyziesubs.enums.SubtitleFormat;
import io.github.skypurl.wyziesubs.enums.SubtitleSource;
import io.github.skypurl.wyziesubs.util.ApiParameter;
import io.github.skypurl.wyziesubs.util.UrlBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Représente une requête de recherche de sous-titres immuable.
 *
 * <p>Utilise un pattern Builder strict. Seul {@code id} est obligatoire.
 * Exemple :</p>
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
    private final String encoding;
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

    // -------------------------------------------------------------------------
    // Point d'entrée du Builder
    // -------------------------------------------------------------------------

    /**
     * Retourne un nouveau {@link Builder} avec l'identifiant du média obligatoire.
     *
     * @param id Identifiant IMDb ou TMDB du média (ex: {@code "tt1234567"}).
     * @return Un {@link Builder} prêt à être configuré.
     */
    public static Builder builder(String id) {
        return new Builder(id);
    }

    // -------------------------------------------------------------------------
    // Conversion vers UrlBuilder
    // -------------------------------------------------------------------------

    /**
     * Peuple un {@link UrlBuilder} avec tous les paramètres non-null de cette requête.
     *
     * <p>Les listes d'enums sont jointes par des virgules via leur méthode
     * {@link ApiParameter#getValue()} (ex: {@code "en,fr"}).</p>
     *
     * @param urlBuilder Le {@link UrlBuilder} à peupler.
     */
    public void populate(UrlBuilder urlBuilder) {
        urlBuilder.addQueryParam("id",       id);
        urlBuilder.addQueryParam("season",   season   != null ? season.toString()  : null);
        urlBuilder.addQueryParam("episode",  episode  != null ? episode.toString() : null);
        urlBuilder.addQueryParam("language", joinParameters(languages));
        urlBuilder.addQueryParam("format",   joinParameters(formats));
        urlBuilder.addQueryParam("hi",       hi       != null ? hi.toString()      : null);
        urlBuilder.addQueryParam("encoding", encoding);
        urlBuilder.addQueryParam("source",   joinParameters(sources));
        urlBuilder.addQueryParam("fileName", fileName);
        urlBuilder.addQueryParam("origin",   joinParameters(origins));
        urlBuilder.addQueryParam("refresh",  refresh  != null ? refresh.toString() : null);

        // Les releases sont des String brutes, pas des ApiParameter
        if (!releases.isEmpty()) {
            urlBuilder.addQueryParam("releases", String.join(",", releases));
        }
    }

    // -------------------------------------------------------------------------
    // Méthode utilitaire privée
    // -------------------------------------------------------------------------

    /**
     * Joint les valeurs d'une liste de {@link ApiParameter} avec des virgules.
     * Retourne {@code null} si la liste est vide (ignoré par {@link UrlBuilder}).
     *
     * @param params Liste de paramètres (enums implémentant {@link ApiParameter}).
     * @param <T>    Type borné à {@link ApiParameter}.
     * @return Chaîne jointe (ex: {@code "en,fr"}) ou {@code null}.
     */
    private <T extends ApiParameter> String joinParameters(List<T> params) {
        if (params.isEmpty()) {
            return null;
        }
        return params.stream()
                .map(ApiParameter::getValue)
                .collect(Collectors.joining(","));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getId()                    { return id;        }
    public Integer getSeason()               { return season;    }
    public Integer getEpisode()              { return episode;   }
    public List<Language> getLanguages()     { return languages; }
    public List<SubtitleFormat> getFormats() { return formats;   }
    public Boolean getHi()                   { return hi;        }
    public String getEncoding()              { return encoding;  }
    public List<SubtitleSource> getSources() { return sources;   }
    public List<String> getReleases()        { return releases;  }
    public String getFileName()              { return fileName;  }
    public List<MediaOrigin> getOrigins()    { return origins;   }
    public Boolean getRefresh()              { return refresh;   }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static final class Builder {

        private final String id;
        private Integer season;
        private Integer episode;
        private final List<Language> languages       = new ArrayList<>();
        private final List<SubtitleFormat> formats   = new ArrayList<>();
        private Boolean hi;
        private String encoding;
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
        public Builder encoding(String encoding)    { this.encoding = encoding; return this; }
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
         * Construit et retourne l'instance {@link SearchRequest} immuable.
         *
         * @return Une nouvelle instance {@link SearchRequest}.
         * @throws NullPointerException si {@code id} est null.
         */
        public SearchRequest build() {
            return new SearchRequest(this);
        }
    }
}