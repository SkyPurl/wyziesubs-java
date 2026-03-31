package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;

/**
 * Character encodings supported by the Wyzie Subs API.
 */
public enum SubtitleEncoding implements ApiParameter {

    UTF_8("utf-8"),
    LATIN_1("latin-1"),
    UTF_16("utf-16"),
    ASCII("ascii");

    private final String value;

    SubtitleEncoding(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}