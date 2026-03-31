package io.github.skypurl.wyziesubs;

import io.github.skypurl.wyziesubs.config.WyzieSubsConfig;
import io.github.skypurl.wyziesubs.enums.Language;
import io.github.skypurl.wyziesubs.exception.ApiException;
import io.github.skypurl.wyziesubs.exception.MappingException;
import io.github.skypurl.wyziesubs.model.SourcesResponse;
import io.github.skypurl.wyziesubs.model.Subtitle;
import io.github.skypurl.wyziesubs.request.SearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultWyzieSubsClient")
class DefaultWyzieSubsClientTest {

    private static final String BASE_URL = "https://api.example.test";
    private static final String API_KEY = "test-api-key";

    private static final String SOURCES_JSON = """
            {
              "sources": ["opensubtitles", "subdl", "subf2m"]
            }
            """;

    private static final String SUBTITLES_JSON = """
            [
              {
                "id": "1955024019",
                "url": "https://sub.wyzie.io/c/198e0c4d/the.martian.srt",
                "format": "srt",
                "encoding": "UTF-8",
                "display": "English",
                "language": "en",
                "media": "The Martian",
                "isHearingImpaired": false,
                "source": "opensubtitles",
                "releases": ["The.Martian.WEB-DL"],
                "fileName": "the.martian.srt",
                "matchedRelease": "The.Martian"
              }
            ]
            """;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockStringResponse;

    @Mock
    private HttpResponse<Path> mockFileResponse;

    private DefaultWyzieSubsClient client;

    @BeforeEach
    void setUp() {
        WyzieSubsConfig config = WyzieSubsConfig.builder(API_KEY)
                .baseUrl(BASE_URL)
                .httpClient(mockHttpClient)
                .build();

        client = new DefaultWyzieSubsClient(config);
    }

    @Test
    @DisplayName("constructor should reject null config")
    void constructor_nullConfig_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DefaultWyzieSubsClient(null));
    }

    @Test
    @DisplayName("getEnabledSources() should return parsed response on HTTP 200")
    void getEnabledSources_http200_returnsSourcesResponse() {
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn(SOURCES_JSON);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        SourcesResponse result = client.getEnabledSources().join();

        assertNotNull(result);
        assertEquals(List.of("opensubtitles", "subdl", "subf2m"), result.sources());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).sendAsync(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        HttpRequest request = requestCaptor.getValue();
        assertEquals(BASE_URL + "/sources", request.uri().toString());
        assertEquals("application/json", request.headers().firstValue("Accept").orElseThrow());
        assertEquals("GET", request.method());
    }

    @Test
    @DisplayName("getEnabledSources() should accept HTTP 299")
    void getEnabledSources_http299_returnsSourcesResponse() {
        when(mockStringResponse.statusCode()).thenReturn(299);
        when(mockStringResponse.body()).thenReturn(SOURCES_JSON);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        SourcesResponse result = client.getEnabledSources().join();

        assertNotNull(result);
        assertEquals(3, result.sources().size());
    }

    @Test
    @DisplayName("getEnabledSources() should reject HTTP 199")
    void getEnabledSources_http199_throwsApiException() {
        when(mockStringResponse.statusCode()).thenReturn(199);
        when(mockStringResponse.body()).thenReturn("{\"error\":\"unexpected\"}");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        CompletionException ex = assertThrows(CompletionException.class, () -> client.getEnabledSources().join());

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals(199, apiEx.getStatusCode());
        assertTrue(apiEx.getMessage().contains("199"));
    }

    @Test
    @DisplayName("getEnabledSources() should reject HTTP 300")
    void getEnabledSources_http300_throwsApiException() {
        when(mockStringResponse.statusCode()).thenReturn(300);
        when(mockStringResponse.body()).thenReturn("{\"error\":\"redirect\"}");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        CompletionException ex = assertThrows(CompletionException.class, () -> client.getEnabledSources().join());

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals(300, apiEx.getStatusCode());
        assertTrue(apiEx.getResponseBody().contains("redirect"));
    }

    @Test
    @DisplayName("getEnabledSources() should use an empty body when the error response body is null")
    void getEnabledSources_nullErrorBody_usesEmptyString() {
        when(mockStringResponse.statusCode()).thenReturn(404);
        when(mockStringResponse.body()).thenReturn(null);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        CompletionException ex = assertThrows(CompletionException.class, () -> client.getEnabledSources().join());

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals(404, apiEx.getStatusCode());
        assertEquals("", apiEx.getResponseBody());
    }

    @Test
    @DisplayName("getEnabledSources() should surface mapping failures")
    void getEnabledSources_invalidJson_throwsMappingException() {
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn("{invalid json");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        CompletionException ex = assertThrows(CompletionException.class, () -> client.getEnabledSources().join());

        MappingException mappingEx = assertInstanceOf(MappingException.class, ex.getCause());
        assertTrue(mappingEx.getMessage().contains("SourcesResponse"));
    }

    @Test
    @DisplayName("getEnabledSources() should propagate HTTP client failures")
    void getEnabledSources_httpClientFailure_isPropagated() {
        RuntimeException failure = new RuntimeException("network down");

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.failedFuture(failure));

        CompletionException ex = assertThrows(CompletionException.class, () -> client.getEnabledSources().join());

        assertSame(failure, ex.getCause());
    }

    @Test
    @DisplayName("search() should return parsed subtitles on HTTP 200")
    void search_http200_returnsSubtitleList() {
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn(SUBTITLES_JSON);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        SearchRequest request = SearchRequest.builder("tt1234567")
                .languages(Language.ENGLISH)
                .build();

        List<Subtitle> result = client.search(request).join();

        assertNotNull(result);
        assertEquals(1, result.size());

        Subtitle subtitle = result.getFirst();
        assertEquals("1955024019", subtitle.id());
        assertEquals("https://sub.wyzie.io/c/198e0c4d/the.martian.srt", subtitle.url());
        assertEquals("en", subtitle.language());
        assertEquals("opensubtitles", subtitle.source());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).sendAsync(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        HttpRequest httpRequest = requestCaptor.getValue();
        assertEquals(BASE_URL + "/search?id=tt1234567&language=en&key=" + API_KEY, httpRequest.uri().toString());
        assertEquals("application/json", httpRequest.headers().firstValue("Accept").orElseThrow());
        assertEquals("GET", httpRequest.method());
    }

    @Test
    @DisplayName("search() should return an empty list when the payload is empty")
    void search_emptyArray_returnsEmptyList() {
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn("[]");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        List<Subtitle> result = client.search(SearchRequest.builder("tt9999999").build()).join();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("search() should reject HTTP 401")
    void search_http401_throwsApiException() {
        when(mockStringResponse.statusCode()).thenReturn(401);
        when(mockStringResponse.body()).thenReturn("{\"error\":\"Unauthorized\"}");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> client.search(SearchRequest.builder("tt1234567").build()).join()
        );

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals(401, apiEx.getStatusCode());
    }

    @Test
    @DisplayName("search() should surface mapping failures")
    void search_invalidJson_throwsMappingException() {
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn("[{invalid json}]");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockStringResponse));

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> client.search(SearchRequest.builder("tt1234567").build()).join()
        );

        MappingException mappingEx = assertInstanceOf(MappingException.class, ex.getCause());
        assertTrue(mappingEx.getMessage().contains("Subtitle"));
    }

    @Test
    @DisplayName("search() should reject a null request")
    void search_nullRequest_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> client.search(null));
    }

    @Test
    @DisplayName("search() should propagate HTTP client failures")
    void search_httpClientFailure_isPropagated() {
        RuntimeException failure = new RuntimeException("timeout");

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.failedFuture(failure));

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> client.search(SearchRequest.builder("tt1234567").build()).join()
        );

        assertSame(failure, ex.getCause());
    }

    @Test
    @DisplayName("download() should return the destination path on HTTP 200")
    void download_http200_returnsDestinationPath() {
        Path destination = Path.of("subtitle.srt");
        Subtitle subtitle = subtitle("https://cdn.example.test/subtitle.srt");

        when(mockFileResponse.statusCode()).thenReturn(200);
        when(mockFileResponse.body()).thenReturn(destination);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockFileResponse));

        Path result = client.download(subtitle, destination).join();

        assertEquals(destination, result);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).sendAsync(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        HttpRequest request = requestCaptor.getValue();
        assertEquals("https://cdn.example.test/subtitle.srt", request.uri().toString());
        assertEquals("GET", request.method());
    }

    @Test
    @DisplayName("download() should reject HTTP errors")
    void download_http500_throwsApiException() {
        Path destination = Path.of("subtitle.srt");
        Subtitle subtitle = subtitle("https://cdn.example.test/subtitle.srt");

        when(mockFileResponse.statusCode()).thenReturn(500);
        when(mockFileResponse.body()).thenReturn(destination);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockFileResponse));

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> client.download(subtitle, destination).join()
        );

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals(500, apiEx.getStatusCode());
        assertTrue(apiEx.getResponseBody().contains("subtitle.srt"));
    }

    @Test
    @DisplayName("download() should reject a null subtitle")
    void download_nullSubtitle_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> client.download(null, Path.of("subtitle.srt")));
    }

    @Test
    @DisplayName("download() should reject a null destination")
    void download_nullDestination_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> client.download(subtitle("https://cdn.example.test/subtitle.srt"), null));
    }

    @Test
    @DisplayName("download() should reject a subtitle without URL")
    void download_nullSubtitleUrl_throwsNullPointerException() {
        Subtitle subtitle = subtitle(null);

        assertThrows(NullPointerException.class, () -> client.download(subtitle, Path.of("subtitle.srt")));
    }

    @Test
    @DisplayName("download() should propagate HTTP client failures")
    void download_httpClientFailure_isPropagated() {
        RuntimeException failure = new RuntimeException("disk error");
        Subtitle subtitle = subtitle("https://cdn.example.test/subtitle.srt");

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.failedFuture(failure));

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> client.download(subtitle, Path.of("subtitle.srt")).join()
        );

        assertSame(failure, ex.getCause());
    }

    private Subtitle subtitle(String url) {
        return new Subtitle(
                "1955024019",
                url,
                null,
                "srt",
                "UTF-8",
                "English",
                "en",
                "The Martian",
                false,
                "opensubtitles",
                "The.Martian",
                List.of("The.Martian.WEB-DL"),
                "the.martian.srt",
                "WEB",
                null,
                null
        );
    }
}