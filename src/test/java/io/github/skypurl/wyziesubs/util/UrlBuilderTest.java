package io.github.skypurl.wyziesubs.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UrlBuilder")
class UrlBuilderTest {

    @Test
    @DisplayName("build() retourne l'URL de base inchangée si aucun paramètre n'est ajouté")
    void build_noParams_returnsBaseUrlUnchanged() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search").build();

        assertEquals("https://sub.wyzie.io/search", uri.toString());
    }

    @Test
    @DisplayName("build() ajoute le séparateur '?' avant le premier paramètre")
    void build_withOneParam_addsQuestionMark() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("id", "tt1234567")
                .build();

        assertTrue(uri.toString().contains("?"));
        assertTrue(uri.toString().contains("id=tt1234567"));
    }

    @Test
    @DisplayName("addQueryParam() encode les espaces en UTF-8 (%20 ou +)")
    void addQueryParam_valueWithSpaces_encodesCorrectly() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("fileName", "the martian.srt")
                .build();

        String raw = uri.toString();
        // URLEncoder encode les espaces en '+', URI.toString() les conserve encodés
        assertTrue(raw.contains("the+martian.srt") || raw.contains("the%20martian.srt"),
                "Les espaces doivent être encodés en UTF-8, URI obtenu : " + raw);
    }

    @Test
    @DisplayName("addQueryParam() encode les caractères spéciaux (accents, &, =)")
    void addQueryParam_specialCharacters_encodesCorrectly() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("fileName", "été & été.srt")
                .build();

        String raw = uri.toString();
        assertFalse(raw.contains(" "),  "L'URI ne doit pas contenir d'espaces bruts");
        assertFalse(raw.contains("&fileName"), "Le '&' de la valeur ne doit pas être confondu avec un séparateur");
    }

    @Test
    @DisplayName("addQueryParam() ignore silencieusement les valeurs null")
    void addQueryParam_nullValue_isIgnored() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("id",       "tt1234567")
                .addQueryParam("language", null)
                .addQueryParam("format",   null)
                .build();

        String raw = uri.toString();
        assertFalse(raw.contains("language"), "Le paramètre 'language' null ne doit pas apparaître");
        assertFalse(raw.contains("format"),   "Le paramètre 'format' null ne doit pas apparaître");
        assertTrue(raw.contains("id=tt1234567"), "Le paramètre 'id' valide doit être présent");
    }

    @Test
    @DisplayName("addQueryParam() joint plusieurs paramètres avec '&'")
    void addQueryParam_multipleParams_joinedWithAmpersand() {
        URI uri = UrlBuilder.of("https://sub.wyzie.io/search")
                .addQueryParam("id",       "tt1234567")
                .addQueryParam("language", "en")
                .addQueryParam("format",   "srt")
                .build();

        String raw = uri.toString();
        // Vérifie la présence des 3 paramètres
        assertTrue(raw.contains("id=tt1234567"));
        assertTrue(raw.contains("language=en"));
        assertTrue(raw.contains("format=srt"));
        // Vérifie la structure avec '&'
        assertEquals(2, raw.chars().filter(c -> c == '&').count(),
                "3 paramètres doivent être séparés par exactement 2 '&'");
    }
}