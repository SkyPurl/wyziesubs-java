package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;

/**
 * Formats de sous-titres supportés par l'API Wyzie Subs.
 */
public enum SubtitleFormat implements ApiParameter {

    SRT("srt"),
    ASS("ass"),
    SSA("ssa"),
    VTT("vtt"),
    SUB("sub");

    private final String value;

    SubtitleFormat(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}