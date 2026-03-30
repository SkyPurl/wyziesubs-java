package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;

/**
 * Origine du média (qualité de la source vidéo).
 */
public enum MediaOrigin implements ApiParameter {

    WEB("WEB"),
    BLURAY("BLURAY"),
    DVD("DVD");

    private final String value;

    MediaOrigin(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}