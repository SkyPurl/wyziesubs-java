package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;

/**
 * Subtitle providers available on the Wyzie Subs API.
 * Use {@code ALL} to query all sources simultaneously.
 */
public enum SubtitleSource implements ApiParameter {

    OPENSUBTITLES("opensubtitles"),
    SUBDL("subdl"),
    SUBF2M("subf2m"),
    PODNAPISI("podnapisi"),
    ANIMETOSHO("animetosho"),
    GESTDOWN("gestdown"),
    JIMAKU("jimaku"),
    KITSUNEKKO("kitsunekko"),
    YIFY("yify"),
    AJATTTOOLS("ajatttools"),
    ALL("all");

    private final String value;

    SubtitleSource(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}