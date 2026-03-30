package io.github.skypurl.wyziesubs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.skypurl.wyziesubs.exception.MappingException;

/**
 * Utilitaire de désérialisation JSON centralisé.
 *
 * <p>Expose une instance unique et pré-configurée de {@link ObjectMapper}.
 * Toutes les erreurs Jackson sont encapsulées en {@link MappingException}
 * afin de ne pas exposer les dépendances internes au consommateur du SDK.</p>
 */
public final class JsonMapper {

    private static final ObjectMapper INSTANCE = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Classe utilitaire : pas d'instanciation
    private JsonMapper() {}

    /**
     * Désérialise un JSON vers un objet du type {@code clazz}.
     *
     * @param json  Chaîne JSON à désérialiser.
     * @param clazz Classe cible.
     * @param <T>   Type de retour.
     * @return Instance désérialisée.
     * @throws MappingException si le JSON est invalide ou incompatible.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return INSTANCE.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new MappingException(
                    "Failed to deserialize JSON into %s : %s".formatted(clazz.getSimpleName(), e.getOriginalMessage()),
                    e
            );
        }
    }

    /**
     * Désérialise un JSON vers un type générique (ex: {@code List<Subtitle>}).
     *
     * <p>Exemple :</p>
     * <pre>{@code
     * List<Subtitle> subtitles = JsonMapper.fromJson(json, new TypeReference<>() {});
     * }</pre>
     *
     * @param json    Chaîne JSON à désérialiser.
     * @param typeRef Référence de type générique Jackson.
     * @param <T>     Type de retour.
     * @return Instance désérialisée.
     * @throws MappingException si le JSON est invalide ou incompatible.
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return INSTANCE.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new MappingException(
                    "Failed to deserialize JSON into %s : %s".formatted(typeRef.getType().getTypeName(), e.getOriginalMessage()),
                    e
            );
        }
    }
}