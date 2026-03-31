package io.github.skypurl.wyziesubs.request;

import io.github.skypurl.wyziesubs.enums.Language;
import io.github.skypurl.wyziesubs.enums.MediaOrigin;
import io.github.skypurl.wyziesubs.enums.SubtitleFormat;
import io.github.skypurl.wyziesubs.enums.SubtitleSource;
import io.github.skypurl.wyziesubs.util.UrlBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SearchRequest")
class SearchRequestTest {

    @Test
    @DisplayName("build() should reject a null id")
    void build_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SearchRequest.builder(null).build());
    }

    @Test
    @DisplayName("build() should succeed with the mandatory id only")
    void build_onlyMandatoryId_succeeds() {
        SearchRequest request = SearchRequest.builder("tt1234567").build();

        assertEquals("tt1234567", request.getId());
        assertNull(request.getSeason());
        assertNull(request.getEpisode());
        assertTrue(request.getLanguages().isEmpty());
        assertTrue(request.getFormats().isEmpty());
        assertNull(request.getHi());
        assertNull(request.getEncoding());
        assertTrue(request.getSources().isEmpty());
        assertTrue(request.getReleases().isEmpty());
        assertNull(request.getFileName());
        assertTrue(request.getOrigins().isEmpty());
        assertNull(request.getRefresh());
    }

    @Test
    @DisplayName("build() should retain all configured values")
    void build_allFields_retainsValues() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .season(2)
                .episode(5)
                .languages(Language.FRENCH, Language.ENGLISH)
                .formats(SubtitleFormat.SRT, SubtitleFormat.VTT)
                .hi(false)
                .encoding("UTF-8")
                .sources(SubtitleSource.OPENSUBTITLES, SubtitleSource.SUBDL)
                .releases("WEB-DL", "BLURAY")
                .fileName("the.martian.srt")
                .origins(MediaOrigin.WEB, MediaOrigin.BLURAY)
                .refresh(false)
                .build();

        assertEquals("tt1234567", request.getId());
        assertEquals(2, request.getSeason());
        assertEquals(5, request.getEpisode());
        assertEquals(List.of(Language.FRENCH, Language.ENGLISH), request.getLanguages());
        assertEquals(List.of(SubtitleFormat.SRT, SubtitleFormat.VTT), request.getFormats());
        assertFalse(request.getHi());
        assertEquals("UTF-8", request.getEncoding());
        assertEquals(List.of(SubtitleSource.OPENSUBTITLES, SubtitleSource.SUBDL), request.getSources());
        assertEquals(List.of("WEB-DL", "BLURAY"), request.getReleases());
        assertEquals("the.martian.srt", request.getFileName());
        assertEquals(List.of(MediaOrigin.WEB, MediaOrigin.BLURAY), request.getOrigins());
        assertFalse(request.getRefresh());
    }

    @Test
    @DisplayName("getters should return immutable lists")
    void getters_returnImmutableLists() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .languages(Language.FRENCH)
                .formats(SubtitleFormat.SRT)
                .sources(SubtitleSource.OPENSUBTITLES)
                .releases("WEB-DL")
                .origins(MediaOrigin.WEB)
                .build();

        assertThrows(UnsupportedOperationException.class, () -> request.getLanguages().add(Language.ENGLISH));
        assertThrows(UnsupportedOperationException.class, () -> request.getFormats().add(SubtitleFormat.VTT));
        assertThrows(UnsupportedOperationException.class, () -> request.getSources().add(SubtitleSource.SUBDL));
        assertThrows(UnsupportedOperationException.class, () -> request.getReleases().add("BLURAY"));
        assertThrows(UnsupportedOperationException.class, () -> request.getOrigins().add(MediaOrigin.BLURAY));
    }

    @Test
    @DisplayName("builder mutations after build() should not affect the built request")
    void build_builderMutationAfterBuild_doesNotAffectBuiltRequest() {
        SearchRequest.Builder builder = SearchRequest.builder("tt1234567")
                .languages(Language.FRENCH)
                .releases("WEB-DL");

        SearchRequest request = builder.build();

        builder.languages(Language.ENGLISH, Language.SPANISH);
        builder.releases("BLURAY");

        assertEquals(List.of(Language.FRENCH), request.getLanguages());
        assertEquals(List.of("WEB-DL"), request.getReleases());
    }

    @Test
    @DisplayName("populate() should include all supported parameters")
    void populate_allParameters_presentInUri() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .season(2)
                .episode(5)
                .languages(Language.FRENCH, Language.ENGLISH)
                .formats(SubtitleFormat.SRT, SubtitleFormat.VTT)
                .hi(false)
                .encoding("UTF-8")
                .sources(SubtitleSource.OPENSUBTITLES, SubtitleSource.SUBDL)
                .releases("WEB-DL", "BLURAY")
                .fileName("the martian.srt")
                .origins(MediaOrigin.WEB, MediaOrigin.BLURAY)
                .refresh(false)
                .build();

        String raw = buildUri(request).toString();

        assertTrue(raw.contains("id=tt1234567"));
        assertTrue(raw.contains("season=2"));
        assertTrue(raw.contains("episode=5"));
        assertTrue(raw.contains("language=fr%2Cen") || raw.contains("language=fr,en"));
        assertTrue(raw.contains("format=srt%2Cvtt") || raw.contains("format=srt,vtt"));
        assertTrue(raw.contains("hi=false"));
        assertTrue(raw.contains("encoding=UTF-8"));
        assertTrue(raw.contains("source=opensubtitles%2Csubdl") || raw.contains("source=opensubtitles,subdl"));
        assertTrue(raw.contains("releases=WEB-DL%2CBLURAY") || raw.contains("releases=WEB-DL,BLURAY"));
        assertTrue(raw.contains("fileName=the+martian.srt") || raw.contains("fileName=the%20martian.srt"));
        assertTrue(raw.contains("origin=WEB%2CBLURAY") || raw.contains("origin=WEB,BLURAY"));
        assertTrue(raw.contains("refresh=false"));
    }

    @Test
    @DisplayName("populate() should omit empty collections and null scalars")
    void populate_emptyAndNullValues_areOmitted() {
        SearchRequest request = SearchRequest.builder("tt1234567").build();

        String raw = buildUri(request).toString();

        assertTrue(raw.contains("id=tt1234567"));
        assertFalse(raw.contains("season="));
        assertFalse(raw.contains("episode="));
        assertFalse(raw.contains("language="));
        assertFalse(raw.contains("format="));
        assertFalse(raw.contains("hi="));
        assertFalse(raw.contains("encoding="));
        assertFalse(raw.contains("source="));
        assertFalse(raw.contains("releases="));
        assertFalse(raw.contains("fileName="));
        assertFalse(raw.contains("origin="));
        assertFalse(raw.contains("refresh="));
    }

    @Test
    @DisplayName("populate() should keep zero as a valid boundary value")
    void populate_zeroBoundaryValues_areIncluded() {
        SearchRequest request = SearchRequest.builder("tt1234567")
                .season(0)
                .episode(0)
                .build();

        String raw = buildUri(request).toString();

        assertTrue(raw.contains("season=0"));
        assertTrue(raw.contains("episode=0"));
    }

    @Test
    @DisplayName("populate() should reject a null UrlBuilder")
    void populate_nullUrlBuilder_throwsNullPointerException() {
        SearchRequest request = SearchRequest.builder("tt1234567").build();

        assertThrows(NullPointerException.class, () -> request.populate(null));
    }

    @Test
    @DisplayName("languages() should reject null elements")
    void languages_nullElement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SearchRequest.builder("tt1234567").languages((Language) null));
    }

    @Test
    @DisplayName("formats() should reject null elements")
    void formats_nullElement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SearchRequest.builder("tt1234567").formats((SubtitleFormat) null));
    }

    @Test
    @DisplayName("sources() should reject null elements")
    void sources_nullElement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SearchRequest.builder("tt1234567").sources((SubtitleSource) null));
    }

    @Test
    @DisplayName("releases() should reject null elements")
    void releases_nullElement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SearchRequest.builder("tt1234567").releases((String) null));
    }

    @Test
    @DisplayName("origins() should reject null elements")
    void origins_nullElement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SearchRequest.builder("tt1234567").origins((MediaOrigin) null));
    }

    private URI buildUri(SearchRequest request) {
        UrlBuilder urlBuilder = UrlBuilder.of("https://sub.wyzie.io/search");
        request.populate(urlBuilder);
        return urlBuilder.build();
    }
}