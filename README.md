# WyzieSubs Java SDK

[![CI](https://github.com/SkyPurl/wyziesubs-java/actions/workflows/ci.yml/badge.svg)](https://github.com/SkyPurl/wyziesubs-java/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.skypurl/wyziesubs-java.svg?label=Maven%20Central)](https://central.sonatype.com/namespace/io.github.skypurl)
[![Java 25+](https://img.shields.io/badge/Java-25%2B-blue.svg)](https://openjdk.org/projects/jdk/25/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A modern, fluent, and zero-dependency (HTTP) Java client for the [Wyzie Subs API](https://sub.wyzie.io/).

Built with **Java 25**, it leverages the native `java.net.http.HttpClient` and **Virtual Threads** for maximum asynchronous performance without the bloat of external reactive libraries.

## Features

*   **Zero HTTP Dependencies:** Uses the native Java 11+ HttpClient.
*   **Fully Asynchronous:** All operations return `CompletableFuture`.
*   **Fluent Builder Pattern:** Heavily typed requests to prevent API errors.
*   **Memory Optimized:** Direct-to-disk subtitle downloads (no RAM buffering).
*   **Resilient:** Safe JSON parsing that won't break if the API evolves.

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.skypurl</groupId>
    <artifactId>wyziesubs-java</artifactId>
    <version>1.0.0</version> <!-- Check the latest version on Maven Central -->
</dependency>
```
Or for Gradle (build.gradle):

```Groovy
implementation 'io.github.skypurl:wyziesubs-java:1.0.0'
```
## Quick Start
1. Initialize the Client
The configuration is completely immutable and builder-based.
```Java
import io.github.skypurl.wyziesubs.WyzieSubsClient;
import io.github.skypurl.wyziesubs.DefaultWyzieSubsClient;
import io.github.skypurl.wyziesubs.config.WyzieSubsConfig;

public class Main {
    public static void main(String[] args) {
        // Quick setup with default values
        WyzieSubsConfig config = WyzieSubsConfig.defaultWithApiKey("YOUR_API_KEY");
        
        // Or advanced setup (custom HttpClient, timeouts, etc.)
        // WyzieSubsConfig config = WyzieSubsConfig.builder("YOUR_API_KEY")
        //      .baseUrl("[https://custom.wyzie.io](https://custom.wyzie.io)")
        //      .build();

        WyzieSubsClient client = new DefaultWyzieSubsClient(config);
    }
}
```
2. Search for Subtitles
Use the strongly-typed SearchRequest builder to query the API.
```Java
import io.github.skypurl.wyziesubs.request.SearchRequest;
import io.github.skypurl.wyziesubs.enums.Language;
import io.github.skypurl.wyziesubs.enums.SubtitleFormat;

// ... inside your async flow

SearchRequest request = SearchRequest.builder("tt3659388") // IMDb or TMDB ID
    .languages(Language.ENGLISH, Language.FRENCH)
    .formats(SubtitleFormat.SRT)
    .season(1)
    .episode(4)
    .hi(false) // Exclude Hearing Impaired
    .build();

client.search(request)
    .thenAccept(subtitles -> {
        if (subtitles.isEmpty()) {
            System.out.println("No subtitles found.");
            return;
        }
        
        subtitles.forEach(sub -> {
            System.out.printf("Found [%s] %s from %s%n", 
                sub.language(), sub.release(), sub.source());
        });
    })
    .join(); // Block for the example, but keep it async in real apps!
```
3. Download a Subtitle
The download method streams the file directly to your disk, making it extremely memory efficient.
```Java
import java.nio.file.Path;

// Assuming you have a 'Subtitle' object from the search results:
Path destination = Path.of("downloads/the_martian.srt");

client.download(subtitle, destination)
    .thenAccept(path -> System.out.println("File saved to: " + path.toAbsolutePath()))
    .exceptionally(ex -> {
        System.err.println("Download failed: " + ex.getMessage());
        return null;
    });
```
## Error Handling
The SDK provides a clean exception hierarchy rooted in `WyzieSubsException`:

`ApiException`: Thrown when the Wyzie Subs API returns an HTTP error (e.g., 401 Unauthorized, 500 Internal Server Error). It contains the HTTP status code and the raw response body.

`MappingException`: Thrown when the JSON response cannot be parsed (wraps Jackson's JsonProcessingException).

```Java
import io.github.skypurl.wyziesubs.exception.ApiException;
import java.util.concurrent.CompletionException;

client.getEnabledSources()
    .exceptionally(ex -> {
        if (ex instanceof CompletionException ce && ce.getCause() instanceof ApiException apiEx) {
            System.err.println("API Error " + apiEx.getStatusCode() + ": " + apiEx.getResponseBody());
        } else {
            System.err.println("Unexpected error: " + ex.getMessage());
        }
        return null; // Return default value
    });
```
## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.
1. Fork the Project

2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)

3. Commit your Changes (`git commit -m 'Add some AmazingFeature`)

4. Push to the Branch (`git push origin feature/AmazingFeature`)

5. Open a Pull Request

Please make sure to update tests as appropriate and ensure mvn clean verify passes.

## License
Distributed under the MIT License. See LICENSE for more information.
