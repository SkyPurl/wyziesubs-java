package io.github.skypurl.wyziesubs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.skypurl.wyziesubs.exception.MappingException;
import io.github.skypurl.wyziesubs.model.SourcesResponse;
import io.github.skypurl.wyziesubs.model.Subtitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonMapper")
class JsonMapperTest {

    @Test
    @DisplayName("fromJson(Class) should deserialize a record and ignore unknown fields")
    void fromJson_class_deserializesAndIgnoresUnknownFields() {
        String json = """
                {
                  "sources": ["subdl", "opensubtitles"],
                  "ignored": true
                }
                """;

        SourcesResponse result = JsonMapper.fromJson(json, SourcesResponse.class);

        assertNotNull(result);
        assertEquals(List.of("subdl", "opensubtitles"), result.sources());
    }

    @Test
    @DisplayName("fromJson(Class) should wrap Jackson failures")
    void fromJson_class_invalidJson_throwsMappingException() {
        String json = "{\"sources\": [}";

        MappingException ex = assertThrows(
                MappingException.class,
                () -> JsonMapper.fromJson(json, SourcesResponse.class)
        );

        assertTrue(ex.getMessage().contains("SourcesResponse"));
        assertInstanceOf(JsonProcessingException.class, ex.getCause());
    }

    @Test
    @DisplayName("fromJson(TypeReference) should deserialize generic payloads")
    void fromJson_typeReference_deserializesGenericPayload() {
        String json = """
                [
                  {
                    "id": "1955024019",
                    "url": "https://sub.wyzie.io/c/198e0c4d/id/1955024019?format=srt&encoding=UTF-8",
                    "flagUrl": "https://flagsapi.com/US/flat/24.png",
                    "format": "srt",
                    "encoding": "UTF-8",
                    "display": "English",
                    "language": "en",
                    "media": "The Martian",
                    "isHearingImpaired": false,
                    "source": "opensubtitles",
                    "release": "The.Martian.2015.1080p.WEB-DL",
                    "releases": ["The.Martian.2015.1080p.WEB-DL"],
                    "fileName": "the.martian.2015.1080p.web-dl.srt",
                    "origin": "WEB-DL",
                    "matchedRelease": "The.Martian.2015.1080p.WEB-DL",
                    "matchedFilter": "martian",
                    "unknownField": "ignored"
                  }
                ]
                """;

        List<Subtitle> result = JsonMapper.fromJson(json, new TypeReference<List<Subtitle>>() {});

        assertNotNull(result);
        assertEquals(1, result.size());

        Subtitle subtitle = result.getFirst();
        assertEquals("1955024019", subtitle.id());
        assertEquals("https://sub.wyzie.io/c/198e0c4d/id/1955024019?format=srt&encoding=UTF-8", subtitle.url());
        assertEquals("https://flagsapi.com/US/flat/24.png", subtitle.flagUrl());
        assertEquals("srt", subtitle.format());
        assertEquals("UTF-8", subtitle.encoding());
        assertEquals("English", subtitle.display());
        assertEquals("en", subtitle.language());
        assertEquals("The Martian", subtitle.media());
        assertFalse(subtitle.isHearingImpaired());
        assertEquals("opensubtitles", subtitle.source());
        assertEquals("The.Martian.2015.1080p.WEB-DL", subtitle.release());
        assertEquals(List.of("The.Martian.2015.1080p.WEB-DL"), subtitle.releases());
        assertEquals("the.martian.2015.1080p.web-dl.srt", subtitle.fileName());
        assertEquals("WEB-DL", subtitle.origin());
        assertEquals("The.Martian.2015.1080p.WEB-DL", subtitle.matchedRelease());
        assertEquals("martian", subtitle.matchedFilter());
    }

    @Test
    @DisplayName("fromJson(TypeReference) should wrap Jackson failures")
    void fromJson_typeReference_invalidJson_throwsMappingException() {
        String json = "[{invalid json}]";

        MappingException ex = assertThrows(
                MappingException.class,
                () -> JsonMapper.fromJson(json, new TypeReference<List<Subtitle>>() {})
        );

        assertTrue(ex.getMessage().contains("Subtitle"));
        assertInstanceOf(JsonProcessingException.class, ex.getCause());
    }
}