package io.github.skypurl.wyziesubs.request;

import io.github.skypurl.wyziesubs.enums.Language;
import io.github.skypurl.wyziesubs.enums.MediaOrigin;
import io.github.skypurl.wyziesubs.enums.SubtitleFormat;
import io.github.skypurl.wyziesubs.enums.SubtitleSource;
import io.github.skypurl.wyziesubs.util.UrlBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SearchRequest")
class SearchRequestTest {

    // -------------------------------------------------------------------------
    // Validation du Builder
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("build() lève NullPointerException si id est null")
    void build_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> SearchRequest.builder(null).build(),
                "Un id null doit lever une NullPointerException");
    }

    @Test
    @DisplayName("build() réussit avec uniquement l'id obligatoire")
    void build_onlyId_succeeds() {
        SearchRequest request = SearchRequest.builder("tt1234567").build();

        assertEquals("tt1234567", request.getId());
        assertTrue(request.getLanguages().isEmpty());
        assertTrue(request.getFormats().isEmpty());
        assertTrue(request.getSources().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Immuabilité des listes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Les listes retournées par les getters sont immuables")
    void getters_returnImmutableLists() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .languages(Language.FRENCH, Language.ENGLISH)
                .formats(SubtitleFormat.SRT)
                .build();

        assertThrows(UnsupportedOperationException.class,
                () -> request.getLanguages().add(Language.SPANISH),
                "La liste des langues doit être immuable");

        assertThrows(UnsupportedOperationException.class,
                () -> request.getFormats().add(SubtitleFormat.VTT),
                "La liste des formats doit être immuable");
    }

    @Test
    @DisplayName("La modification du Builder après build() n'affecte pas la requête construite")
    void build_builderMutationAfterBuild_doesNotAffectRequest() {
        SearchRequest.Builder builder = SearchRequest.builder("tt1234567")
                .languages(Language.FRENCH);

        SearchRequest request = builder.build();
        // On tente de muter le builder après le build (appel varargs addAll)
        builder.languages(Language.ENGLISH, Language.SPANISH);

        // La requête déjà construite ne doit pas être affectée
        assertEquals(1, request.getLanguages().size(),
                "La requête construite ne doit pas être affectée par les mutations ultérieures du Builder");
        assertEquals(Language.FRENCH, request.getLanguages().getFirst());
    }

    // -------------------------------------------------------------------------
    // populate() — jointure des enums par virgule
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("populate() joint les langues avec des virgules (fr,en)")
    void populate_multipleLanguages_joinedWithComma() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .languages(Language.FRENCH, Language.ENGLISH)
                .build();

        URI uri = buildUri(request);
        String raw = uri.toString();

        // URLEncoder encode ',' en '%2C'
        assertTrue(raw.contains("language=fr%2Cen") || raw.contains("language=fr,en"),
                "Les langues doivent être jointes par une virgule, URI obtenu : " + raw);
    }

    @Test
    @DisplayName("populate() joint les formats avec des virgules (srt,vtt)")
    void populate_multipleFormats_joinedWithComma() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .formats(SubtitleFormat.SRT, SubtitleFormat.VTT)
                .build();

        URI uri = buildUri(request);
        String raw = uri.toString();

        assertTrue(raw.contains("format=srt%2Cvtt") || raw.contains("format=srt,vtt"),
                "Les formats doivent être joints par une virgule, URI obtenu : " + raw);
    }

    @Test
    @DisplayName("populate() joint les sources avec des virgules")
    void populate_multipleSources_joinedWithComma() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .sources(SubtitleSource.OPENSUBTITLES, SubtitleSource.SUBDL)
                .build();

        URI uri = buildUri(request);
        String raw = uri.toString();

        assertTrue(raw.contains("source=opensubtitles%2Csubdl") || raw.contains("source=opensubtitles,subdl"),
                "Les sources doivent être jointes par une virgule, URI obtenu : " + raw);
    }

    @Test
    @DisplayName("populate() joint les origines avec des virgules")
    void populate_multipleOrigins_joinedWithComma() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .origins(MediaOrigin.WEB, MediaOrigin.BLURAY)
                .build();

        URI uri = buildUri(request);
        String raw = uri.toString();

        assertTrue(raw.contains("origin=WEB%2CBLURAY") || raw.contains("origin=WEB,BLURAY"),
                "Les origines doivent être jointes par une virgule, URI obtenu : " + raw);
    }

    @Test
    @DisplayName("populate() n'ajoute pas les paramètres de listes vides")
    void populate_emptyLists_notAddedToUri() {
        SearchRequest request = SearchRequest.builder("tt1234567").build();

        URI uri = buildUri(request);
        String raw = uri.toString();

        assertFalse(raw.contains("language="), "language ne doit pas apparaître si la liste est vide");
        assertFalse(raw.contains("format="),   "format ne doit pas apparaître si la liste est vide");
        assertFalse(raw.contains("source="),   "source ne doit pas apparaître si la liste est vide");
    }

    @Test
    @DisplayName("populate() inclut les paramètres scalaires non-null (season, episode, hi)")
    void populate_scalarParams_presentInUri() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .season(2)
                .episode(5)
                .hi(true)
                .build();

        URI uri = buildUri(request);
        String raw = uri.toString();

        assertTrue(raw.contains("season=2"),   "season doit être présent");
        assertTrue(raw.contains("episode=5"),  "episode doit être présent");
        assertTrue(raw.contains("hi=true"),    "hi doit être présent");
    }

    // -------------------------------------------------------------------------
    // Méthode utilitaire de test
    // -------------------------------------------------------------------------

    /** Construit une URI à partir d'une SearchRequest via un UrlBuilder de test. */
    private URI buildUri(SearchRequest request) {
        UrlBuilder urlBuilder = UrlBuilder.of("https://sub.wyzie.io/search");
        request.populate(urlBuilder);
        return urlBuilder.build();
    }
}