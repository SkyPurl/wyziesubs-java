package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;

/**
 * Media origin (video source quality).
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