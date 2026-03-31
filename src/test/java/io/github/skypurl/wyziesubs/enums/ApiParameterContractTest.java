package io.github.skypurl.wyziesubs.enums;

import io.github.skypurl.wyziesubs.util.ApiParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ApiParameter contract")
class ApiParameterContractTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("parameters")
    @DisplayName("getValue() should return the expected API value")
    void getValue_returnsExpectedApiValue(String label, ApiParameter parameter, String expectedValue) {
        assertEquals(expectedValue, parameter.getValue());
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> parameters() {
        return Stream.of(
                Arguments.of("Language.ENGLISH", Language.ENGLISH, "en"),
                Arguments.of("Language.FRENCH", Language.FRENCH, "fr"),
                Arguments.of("Language.SPANISH", Language.SPANISH, "es"),
                Arguments.of("Language.GERMAN", Language.GERMAN, "de"),
                Arguments.of("Language.ITALIAN", Language.ITALIAN, "it"),
                Arguments.of("Language.PORTUGUESE", Language.PORTUGUESE, "pt"),
                Arguments.of("Language.RUSSIAN", Language.RUSSIAN, "ru"),
                Arguments.of("Language.JAPANESE", Language.JAPANESE, "ja"),
                Arguments.of("Language.CHINESE_SIMPLIFIED", Language.CHINESE_SIMPLIFIED, "zh"),
                Arguments.of("Language.ARABIC", Language.ARABIC, "ar"),
                Arguments.of("Language.KOREAN", Language.KOREAN, "ko"),
                Arguments.of("Language.DUTCH", Language.DUTCH, "nl"),
                Arguments.of("Language.POLISH", Language.POLISH, "pl"),
                Arguments.of("Language.TURKISH", Language.TURKISH, "tr"),

                Arguments.of("SubtitleFormat.SRT", SubtitleFormat.SRT, "srt"),
                Arguments.of("SubtitleFormat.ASS", SubtitleFormat.ASS, "ass"),
                Arguments.of("SubtitleFormat.SSA", SubtitleFormat.SSA, "ssa"),
                Arguments.of("SubtitleFormat.VTT", SubtitleFormat.VTT, "vtt"),
                Arguments.of("SubtitleFormat.SUB", SubtitleFormat.SUB, "sub"),

                Arguments.of("SubtitleSource.OPENSUBTITLES", SubtitleSource.OPENSUBTITLES, "opensubtitles"),
                Arguments.of("SubtitleSource.SUBDL", SubtitleSource.SUBDL, "subdl"),
                Arguments.of("SubtitleSource.SUBF2M", SubtitleSource.SUBF2M, "subf2m"),
                Arguments.of("SubtitleSource.PODNAPISI", SubtitleSource.PODNAPISI, "podnapisi"),
                Arguments.of("SubtitleSource.ANIMETOSHO", SubtitleSource.ANIMETOSHO, "animetosho"),
                Arguments.of("SubtitleSource.GESTDOWN", SubtitleSource.GESTDOWN, "gestdown"),
                Arguments.of("SubtitleSource.JIMAKU", SubtitleSource.JIMAKU, "jimaku"),
                Arguments.of("SubtitleSource.KITSUNEKKO", SubtitleSource.KITSUNEKKO, "kitsunekko"),
                Arguments.of("SubtitleSource.YIFY", SubtitleSource.YIFY, "yify"),
                Arguments.of("SubtitleSource.AJATTTOOLS", SubtitleSource.AJATTTOOLS, "ajatttools"),
                Arguments.of("SubtitleSource.ALL", SubtitleSource.ALL, "all"),

                Arguments.of("MediaOrigin.WEB", MediaOrigin.WEB, "WEB"),
                Arguments.of("MediaOrigin.BLURAY", MediaOrigin.BLURAY, "BLURAY"),
                Arguments.of("MediaOrigin.DVD", MediaOrigin.DVD, "DVD"),

                Arguments.of("SubtitleEncoding.UTF_8", SubtitleEncoding.UTF_8, "utf-8"),
                Arguments.of("SubtitleEncoding.LATIN_1", SubtitleEncoding.LATIN_1, "latin-1"),
                Arguments.of("SubtitleEncoding.UTF_16", SubtitleEncoding.UTF_16, "utf-16"),
                Arguments.of("SubtitleEncoding.ASCII", SubtitleEncoding.ASCII, "ascii")
        );
    }
}