package io.github.skypurl.wyziesubs;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.skypurl.wyziesubs.config.WyzieSubsConfig;
import io.github.skypurl.wyziesubs.exception.ApiException;
import io.github.skypurl.wyziesubs.model.SourcesResponse;
import io.github.skypurl.wyziesubs.model.Subtitle;
import io.github.skypurl.wyziesubs.request.SearchRequest;
import io.github.skypurl.wyziesubs.enums.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultWyzieSubsClient")
class DefaultWyzieSubsClientTest {

    // -------------------------------------------------------------------------
    // Fixtures JSON
    // -------------------------------------------------------------------------

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

    private static final String ERROR_BODY_404 = """
            {"error": "Not Found"}
            """;

    private static final String ERROR_BODY_500 = """
            {"error": "Internal Server Error"}
            """;

    // -------------------------------------------------------------------------
    // Mocks & SUT
    // -------------------------------------------------------------------------

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private DefaultWyzieSubsClient client;

    @BeforeEach
    void setUp() {
        WyzieSubsConfig config = WyzieSubsConfig.builder("test-api-key")
                .httpClient(mockHttpClient)
                .build();
        client = new DefaultWyzieSubsClient(config);
    }

    // -------------------------------------------------------------------------
    // getEnabledSources() — succès
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getEnabledSources() retourne une SourcesResponse parsée sur HTTP 200")
    void getEnabledSources_http200_returnsSourcesResponse() {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(SOURCES_JSON);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        SourcesResponse result = client.getEnabledSources().join();

        assertNotNull(result);
        assertEquals(3, result.sources().size());
        assertTrue(result.sources().contains("opensubtitles"));
        assertTrue(result.sources().contains("subdl"));
        assertTrue(result.sources().contains("subf2m"));
    }

    // -------------------------------------------------------------------------
    // getEnabledSources() — erreurs HTTP
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getEnabledSources() lève ApiException encapsulée dans CompletionException sur HTTP 404")
    void getEnabledSources_http404_throwsApiException() {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn(ERROR_BODY_404);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletionException ex = assertThrows(CompletionException.class,
                () -> client.getEnabledSources().join());

        assertInstanceOf(ApiException.class, ex.getCause(),
                "La cause doit être une ApiException");
        assertEquals(404, ((ApiException) ex.getCause()).getStatusCode());
    }

    @Test
    @DisplayName("getEnabledSources() lève ApiException encapsulée dans CompletionException sur HTTP 500")
    void getEnabledSources_http500_throwsApiException() {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn(ERROR_BODY_500);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletionException ex = assertThrows(CompletionException.class,
                () -> client.getEnabledSources().join());

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals(500, apiEx.getStatusCode());
        assertTrue(apiEx.getResponseBody().contains("Internal Server Error"));
    }

    // -------------------------------------------------------------------------
    // search() — succès
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("search() retourne une liste de Subtitle parsée sur HTTP 200")
    void search_http200_returnsSubtitleList() {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(SUBTITLES_JSON);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        SearchRequest request = SearchRequest.builder("tt1234567")
                .languages(Language.ENGLISH)
                .build();

        List<Subtitle> result = client.search(request).join();

        assertNotNull(result);
        assertEquals(1, result.size());

        Subtitle subtitle = result.getFirst();
        assertEquals("1955024019",                                  subtitle.id());
        assertEquals("https://sub.wyzie.io/c/198e0c4d/the.martian.srt", subtitle.url());
        assertEquals("srt",                                         subtitle.format());
        assertEquals("en",                                          subtitle.language());
        assertEquals("opensubtitles",                               subtitle.source());
        assertFalse(subtitle.isHearingImpaired());
        assertEquals(1, subtitle.releases().size());
        assertEquals("The.Martian.WEB-DL", subtitle.releases().getFirst());
    }

    @Test
    @DisplayName("search() retourne une liste vide si le JSON est un tableau vide")
    void search_http200_emptyArray_returnsEmptyList() {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("[]");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<Subtitle> result = client.search(
                SearchRequest.builder("tt9999999").build()
        ).join();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // search() — erreurs HTTP
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("search() lève ApiException encapsulée dans CompletionException sur HTTP 401")
    void search_http401_throwsApiException() {
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn("{\"error\": \"Unauthorized\"}");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletionException ex = assertThrows(CompletionException.class,
                () -> client.search(SearchRequest.builder("tt1234567").build()).join());

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals(401, apiEx.getStatusCode());
    }

    @Test
    @DisplayName("search() lève ApiException encapsulée dans CompletionException sur HTTP 500")
    void search_http500_throwsApiException() {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn(ERROR_BODY_500);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletionException ex = assertThrows(CompletionException.class,
                () -> client.search(SearchRequest.builder("tt1234567").build()).join());

        assertEquals(500, ((ApiException) ex.getCause()).getStatusCode());
    }

    // -------------------------------------------------------------------------
    // search() — validation des paramètres
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("search() lève NullPointerException si la requête est null")
    void search_nullRequest_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> client.search(null));
    }

    // -------------------------------------------------------------------------
    // ApiException — contenu du message
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ApiException contient le statusCode et le responseBody dans son message")
    void apiException_messageContainsStatusCodeAndBody() {
        when(mockResponse.statusCode()).thenReturn(403);
        when(mockResponse.body()).thenReturn("{\"error\": \"Forbidden\"}");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletionException ex = assertThrows(CompletionException.class,
                () -> client.getEnabledSources().join());

        ApiException apiEx = assertInstanceOf(ApiException.class, ex.getCause());
        assertTrue(apiEx.getMessage().contains("403"),
                "Le message doit contenir le code HTTP");
        assertTrue(apiEx.getMessage().contains("Forbidden"),
                "Le message doit contenir le corps de la réponse");
    }
}