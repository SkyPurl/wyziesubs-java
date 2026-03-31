package io.github.skypurl.wyziesubs.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WyzieSubsConfig")
class WyzieSubsConfigTest {

    @Test
    @DisplayName("defaultWithApiKey() should use default values")
    void defaultWithApiKey_usesDefaultValues() {
        WyzieSubsConfig config = WyzieSubsConfig.defaultWithApiKey("test-api-key");

        assertEquals("test-api-key", config.getApiKey());
        assertEquals("https://sub.wyzie.io", config.getBaseUrl());
        assertNotNull(config.getHttpClient());
    }

    @Test
    @DisplayName("builder() should apply custom values")
    void builder_customValues_areApplied() {
        HttpClient customHttpClient = HttpClient.newHttpClient();

        WyzieSubsConfig config = WyzieSubsConfig.builder("test-api-key")
                .baseUrl("https://custom.example.test")
                .httpClient(customHttpClient)
                .build();

        assertEquals("test-api-key", config.getApiKey());
        assertEquals("https://custom.example.test", config.getBaseUrl());
        assertSame(customHttpClient, config.getHttpClient());
    }

    @Test
    @DisplayName("build() should reject a null apiKey")
    void build_nullApiKey_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> WyzieSubsConfig.builder(null).build());
    }

    @Test
    @DisplayName("build() should reject a null baseUrl")
    void build_nullBaseUrl_throwsNullPointerException() {
        assertThrows(
                NullPointerException.class,
                () -> WyzieSubsConfig.builder("test-api-key").baseUrl(null).build()
        );
    }

    @Test
    @DisplayName("build() should reject a null httpClient")
    void build_nullHttpClient_throwsNullPointerException() {
        assertThrows(
                NullPointerException.class,
                () -> WyzieSubsConfig.builder("test-api-key").httpClient(null).build()
        );
    }
}