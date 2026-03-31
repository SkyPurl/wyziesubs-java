package io.github.skypurl.wyziesubs.demo;

import io.github.skypurl.wyziesubs.DefaultWyzieSubsClient;
import io.github.skypurl.wyziesubs.WyzieSubsClient;
import io.github.skypurl.wyziesubs.config.WyzieSubsConfig;
import io.github.skypurl.wyziesubs.enums.Language;
import io.github.skypurl.wyziesubs.enums.SubtitleFormat;
import io.github.skypurl.wyziesubs.enums.SubtitleSource;
import io.github.skypurl.wyziesubs.model.SourcesResponse;
import io.github.skypurl.wyziesubs.model.Subtitle;
import io.github.skypurl.wyziesubs.request.SearchRequest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Demonstration class for the WyzieSubs Java SDK.
 *
 * <p>To run this demo:</p>
 * <pre>
 * export WYZIE_API_KEY="your-api-key-here"
 * mvn exec:java -Dexec.mainClass="io.github.skypurl.wyziesubs.demo.DemoMain"
 * </pre>
 */
public class DemoMain {

    public static void main(String[] args) {
        // 1. Retrieve API key from environment variables
        String apiKey = System.getenv("WYZIE_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("❌ Error: WYZIE_API_KEY environment variable is not set.");
            System.err.println("💡 Use: export WYZIE_API_KEY=\"your-api-key-here\"");
            System.exit(1);
        }

        System.out.println("🚀 Starting WyzieSubs Java SDK Demo\n");

        // 2. Initialize client with default configuration
        WyzieSubsConfig config = WyzieSubsConfig.defaultWithApiKey(apiKey);
        WyzieSubsClient client = new DefaultWyzieSubsClient(config);

        // 3. Test 1: Fetch enabled sources
        System.out.println("📡 Test 1: Fetching enabled sources...");
        testGetEnabledSources(client);

        // 4. Test 2: Search subtitles for "The Martian" (IMDb: tt3659388)
        System.out.println("\n🔍 Test 2: Searching subtitles for 'The Martian'...");
        List<Subtitle> subtitles = testSearch(client);

        // 5. Test 3: Download the first subtitle found
        if (!subtitles.isEmpty()) {
            System.out.println("\n⬇️  Test 3: Downloading the first subtitle...");
            testDownload(client, subtitles.get(0));
        } else {
            System.out.println("\n⚠️  No subtitles found, skipping download test.");
        }

        System.out.println("\n✅ Demo completed successfully!");
    }

    // ----
    // Test 1: getEnabledSources()
    // ----

    private static void testGetEnabledSources(WyzieSubsClient client) {
        try {
            SourcesResponse response = client.getEnabledSources()
                    .exceptionally(ex -> {
                        System.err.println("❌ Error fetching sources: " + ex.getMessage());
                        return new SourcesResponse(List.of());
                    })
                    .join();

            if (response.sources().isEmpty()) {
                System.out.println("⚠️  No enabled sources found.");
            } else {
                System.out.println("✅ Enabled sources (" + response.sources().size() + "):");
                response.sources().forEach(source -> System.out.println("   - " + source));
            }
        } catch (Exception e) {
            System.err.println("❌ Unexpected exception: " + e.getMessage());
        }
    }

    // ----
    // Test 2: search()
    // ----

    private static List<Subtitle> testSearch(WyzieSubsClient client) {
        SearchRequest request = SearchRequest.builder("tt3659388")  // The Martian
                .languages(Language.ENGLISH, Language.FRENCH)
                .formats(SubtitleFormat.SRT)
                .sources(SubtitleSource.OPENSUBTITLES, SubtitleSource.SUBDL)
                .build();

        try {
            List<Subtitle> subtitles = client.search(request)
                    .exceptionally(ex -> {
                        System.err.println("❌ Error during search: " + ex.getMessage());
                        return List.of();
                    })
                    .join();

            if (subtitles.isEmpty()) {
                System.out.println("⚠️  No subtitles found for this search.");
            } else {
                System.out.println("✅ Subtitles found (" + subtitles.size() + "):");
                subtitles.stream()
                        .limit(5)  // Display only the first 5 results
                        .forEach(subtitle -> {
                            System.out.println("   ┌─ ID: " + subtitle.id());
                            System.out.println("   ├─ Language: " + subtitle.display() + " (" + subtitle.language() + ")");
                            System.out.println("   ├─ Format: " + subtitle.format());
                            System.out.println("   ├─ Source: " + subtitle.source());
                            System.out.println("   ├─ Filename: " + subtitle.fileName());
                            System.out.println("   └─ URL: " + subtitle.url());
                            System.out.println();
                        });

                if (subtitles.size() > 5) {
                    System.out.println("   ... and " + (subtitles.size() - 5) + " more results.");
                }
            }

            return subtitles;
        } catch (Exception e) {
            System.err.println("❌ Unexpected exception: " + e.getMessage());
            return List.of();
        }
    }

    // ----
    // Test 3: download()
    // ----

    private static void testDownload(WyzieSubsClient client, Subtitle subtitle) {
        Path destination = Paths.get("demo-subtitle-" + subtitle.id() + "." + subtitle.format());

        try {
            System.out.println("📥 Downloading: " + subtitle.fileName());
            System.out.println("📂 Destination: " + destination.toAbsolutePath());

            Path downloadedFile = client.download(subtitle, destination)
                    .exceptionally(ex -> {
                        System.err.println("❌ Error during download: " + ex.getMessage());
                        return null;
                    })
                    .join();

            if (downloadedFile != null) {
                System.out.println("✅ Download successful!");
                System.out.println("📄 File saved: " + downloadedFile.toAbsolutePath());
                System.out.println("📊 Size: " + downloadedFile.toFile().length() + " bytes");
            }
        } catch (Exception e) {
            System.err.println("❌ Unexpected exception: " + e.getMessage());
        }
    }
}