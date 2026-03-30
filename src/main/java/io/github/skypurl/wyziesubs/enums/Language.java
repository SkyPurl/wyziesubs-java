package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;

/**
 * Langues supportées (codes ISO 639-1).
 * Utiliser {@link io.github.skypurl.wyziesubs.model.Subtitle#language()} pour la désérialisation
 * (qui reste une {@code String} pour la résilience).
 */
public enum Language implements ApiParameter {

    ENGLISH("en"),
    FRENCH("fr"),
    SPANISH("es"),
    GERMAN("de"),
    ITALIAN("it"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    JAPANESE("ja"),
    CHINESE_SIMPLIFIED("zh"),
    ARABIC("ar"),
    KOREAN("ko"),
    DUTCH("nl"),
    POLISH("pl"),
    TURKISH("tr");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}