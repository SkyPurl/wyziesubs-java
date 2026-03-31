package io.github.skypurl.wyziesubs.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UrlBuilder")
class UrlBuilderTest {

    @Test
    @DisplayName("build() should return the base URL when no parameters are added")
    void build_noParams_returnsBaseUrl() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search").build();

        assertEquals("https://sub.wyzie.io/search", uri.toString());
    }

    @Test
    @DisplayName("addQueryParam() should return the same instance for chaining")
    void addQueryParam_returnsSameInstance() {
        UrlBuilder builder = UrlBuilder.of("https://sub.wyzie.io/search");

        UrlBuilder result = builder.addQueryParam("id", "tt1234567");

        assertSame(builder, result);
    }

    @Test
    @DisplayName("build() should add the first query separator")
    void build_firstParam_addsQuestionMark() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("id", "tt1234567")
                .build();

        assertEquals("https://sub.wyzie.io/search?id=tt1234567", uri.toString());
    }

    @Test
    @DisplayName("addQueryParam() should ignore null values")
    void addQueryParam_nullValue_isIgnored() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("id", "tt1234567")
                .addQueryParam("language", null)
                .build();

        String raw = uri.toString();
        assertEquals("https://sub.wyzie.io/search?id=tt1234567", raw);
        assertFalse(raw.contains("language"));
    }

    @Test
    @DisplayName("addQueryParam() should keep empty values")
    void addQueryParam_emptyValue_isRetained() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("language", "")
                .build();

        assertEquals("https://sub.wyzie.io/search?language=", uri.toString());
    }

    @Test
    @DisplayName("addQueryParam() should reject a null key")
    void addQueryParam_nullKey_throwsNullPointerException() {
        UrlBuilder builder = UrlBuilder.of("https://sub.wyzie.io/search");

        assertThrows(NullPointerException.class, () -> builder.addQueryParam(null, "value"));
    }

    @Test
    @DisplayName("addQueryParam() should encode special characters with UTF-8")
    void addQueryParam_specialCharacters_encodesWithUrlEncoder() {
        String value = "été & été.srt";

        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("fileName", value)
                .build();

        String expected = "fileName=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
        assertTrue(uri.toString().contains(expected));
        assertFalse(uri.toString().contains(" été"));
    }

    @Test
    @DisplayName("build() should preserve insertion order")
    void build_multipleParams_preservesInsertionOrder() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("id", "tt1234567")
                .addQueryParam("language", "en")
                .addQueryParam("format", "srt")
                .build();

        assertEquals(
                "https://sub.wyzie.io/search?id=tt1234567&language=en&format=srt",
                uri.toString()
        );
    }

    @Test
    @DisplayName("build() should be stable across repeated calls")
    void build_multipleCalls_returnsSameUri() {
        UrlBuilder builder = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("id", "tt1234567")
                .addQueryParam("language", "en");

        URI first = builder.build();
        URI second = builder.build();

        assertEquals(first, second);
        assertEquals(first.toString(), second.toString());
    }
}