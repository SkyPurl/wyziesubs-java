package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;

/**
 * Supported subtitle languages (ISO 639-1 codes).
 */
public enum Language implements ApiParameter {

    ENGLISH("en"),
    FRENCH("fr"),
    SPANISH("es"),
    GERMAN("de"),
    ITALIAN("it"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    ARABIC("ar"),
    CHINESE("zh"),
    JAPANESE("ja"),
    KOREAN("ko"),
    DUTCH("nl"),
    POLISH("pl"),
    SWEDISH("sv"),
    NORWEGIAN("no"),
    DANISH("da"),
    FINNISH("fi"),
    CZECH("cs"),
    SLOVAK("sk"),
    HUNGARIAN("hu"),
    ROMANIAN("ro"),
    BULGARIAN("bg"),
    CROATIAN("hr"),
    SERBIAN("sr"),
    UKRAINIAN("uk"),
    TURKISH("tr"),
    GREEK("el"),
    HEBREW("he"),
    PERSIAN("fa"),
    INDONESIAN("id"),
    MALAY("ms"),
    THAI("th"),
    VIETNAMESE("vi");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}