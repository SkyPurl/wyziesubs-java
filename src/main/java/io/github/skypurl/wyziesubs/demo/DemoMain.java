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
 * Classe de démonstration du SDK WyzieSubs Java.
 *
 * <p>Pour exécuter :</p>
 * <pre>
 * export WYZIE_API_KEY="your-api-key-here"
 * mvn exec:java -Dexec.mainClass="io.github.skypurl.wyziesubs.demo.DemoMain"
 * </pre>
 */
public class DemoMain {

    public static void main(String[] args) {
        // 1. Récupère la clé API depuis les variables d'environnement
        String apiKey = System.getenv("WYZIE_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("❌ Erreur : La variable d'environnement WYZIE_API_KEY n'est pas définie.");
            System.err.println("💡 Utilise : export WYZIE_API_KEY=\"your-api-key-here\"");
            System.exit(1);
        }

        System.out.println("🚀 Démarrage de la démo WyzieSubs Java SDK\n");

        // 2. Crée le client avec la configuration par défaut
        WyzieSubsConfig config = WyzieSubsConfig.defaultWithApiKey(apiKey);
        WyzieSubsClient client = new DefaultWyzieSubsClient(config);

        // 3. Test 1 : Récupère les sources activées
        System.out.println("📡 Test 1 : Récupération des sources activées...");
        testGetEnabledSources(client);

        // 4. Test 2 : Recherche de sous-titres pour "The Martian" (IMDb: tt3659388)
        System.out.println("\n🔍 Test 2 : Recherche de sous-titres pour 'The Martian'...");
        List<Subtitle> subtitles = testSearch(client);

        // 5. Test 3 : Téléchargement du premier sous-titre trouvé
        if (!subtitles.isEmpty()) {
            System.out.println("\n⬇️  Test 3 : Téléchargement du premier sous-titre...");
            testDownload(client, subtitles.get(0));
        } else {
            System.out.println("\n⚠️  Aucun sous-titre trouvé, impossible de tester le téléchargement.");
        }

        System.out.println("\n✅ Démo terminée avec succès !");
    }

    // -------------------------------------------------------------------------
    // Test 1 : getEnabledSources()
    // -------------------------------------------------------------------------

    private static void testGetEnabledSources(WyzieSubsClient client) {
        try {
            SourcesResponse response = client.getEnabledSources()
                    .exceptionally(ex -> {
                        System.err.println("❌ Erreur lors de la récupération des sources : " + ex.getMessage());
                        return new SourcesResponse(List.of());
                    })
                    .join();

            if (response.sources().isEmpty()) {
                System.out.println("⚠️  Aucune source activée trouvée.");
            } else {
                System.out.println("✅ Sources activées (" + response.sources().size() + ") :");
                response.sources().forEach(source -> System.out.println("   - " + source));
            }
        } catch (Exception e) {
            System.err.println("❌ Exception inattendue : " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Test 2 : search()
    // -------------------------------------------------------------------------

    private static List<Subtitle> testSearch(WyzieSubsClient client) {
        SearchRequest request = SearchRequest.builder("tt3659388")  // The Martian
                .languages(Language.ENGLISH, Language.FRENCH)
                .formats(SubtitleFormat.SRT)
                .sources(SubtitleSource.OPENSUBTITLES, SubtitleSource.SUBDL)
                .build();

        try {
            List<Subtitle> subtitles = client.search(request)
                    .exceptionally(ex -> {
                        System.err.println("❌ Erreur lors de la recherche : " + ex.getMessage());
                        return List.of();
                    })
                    .join();

            if (subtitles.isEmpty()) {
                System.out.println("⚠️  Aucun sous-titre trouvé pour cette recherche.");
            } else {
                System.out.println("✅ Sous-titres trouvés (" + subtitles.size() + ") :");
                subtitles.stream()
                        .limit(5)  // Affiche seulement les 5 premiers
                        .forEach(subtitle -> {
                            System.out.println("   ┌─ ID: " + subtitle.id());
                            System.out.println("   ├─ Langue: " + subtitle.display() + " (" + subtitle.language() + ")");
                            System.out.println("   ├─ Format: " + subtitle.format());
                            System.out.println("   ├─ Source: " + subtitle.source());
                            System.out.println("   ├─ Fichier: " + subtitle.fileName());
                            System.out.println("   └─ URL: " + subtitle.url());
                            System.out.println();
                        });

                if (subtitles.size() > 5) {
                    System.out.println("   ... et " + (subtitles.size() - 5) + " autres résultats.");
                }
            }

            return subtitles;
        } catch (Exception e) {
            System.err.println("❌ Exception inattendue : " + e.getMessage());
            return List.of();
        }
    }

    // -------------------------------------------------------------------------
    // Test 3 : download()
    // -------------------------------------------------------------------------

    private static void testDownload(WyzieSubsClient client, Subtitle subtitle) {
        Path destination = Paths.get("demo-subtitle-" + subtitle.id() + "." + subtitle.format());

        try {
            System.out.println("📥 Téléchargement de : " + subtitle.fileName());
            System.out.println("📂 Destination : " + destination.toAbsolutePath());

            Path downloadedFile = client.download(subtitle, destination)
                    .exceptionally(ex -> {
                        System.err.println("❌ Erreur lors du téléchargement : " + ex.getMessage());
                        return null;
                    })
                    .join();

            if (downloadedFile != null) {
                System.out.println("✅ Téléchargement réussi !");
                System.out.println("📄 Fichier sauvegardé : " + downloadedFile.toAbsolutePath());
                System.out.println("📊 Taille : " + downloadedFile.toFile().length() + " octets");
            }
        } catch (Exception e) {
            System.err.println("❌ Exception inattendue : " + e.getMessage());
        }
    }
}