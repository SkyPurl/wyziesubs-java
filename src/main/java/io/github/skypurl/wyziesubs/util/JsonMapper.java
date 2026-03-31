package io.github.skypurl.wyziesubs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.skypurl.wyziesubs.exception.MappingException;

/**
 * Centralized JSON deserialization utility.
 *
 * <p>Exposes a single, pre-configured {@link ObjectMapper} instance.
 * Jackson-specific errors are wrapped in {@link MappingException} to
 * avoid exposing internal dependencies to the SDK consumer.</p>
 */
public final class JsonMapper {

    private static final ObjectMapper INSTANCE = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonMapper() {}

    /**
     * Deserializes JSON into an object of type {@code clazz}.
     *
     * @param json  JSON string to deserialize.
     * @param clazz Target class.
     * @param <T>   Return type.
     * @return Deserialized instance.
     * @throws MappingException if JSON is invalid or incompatible.
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
     * Deserializes JSON into a generic type (e.g., {@code List<Subtitle>}).
     *
     * <p>Example:</p>
     * <pre>{@code
     * List<Subtitle> subtitles = JsonMapper.fromJson(json, new TypeReference<>() {});
     * }</pre>
     *
     * @param json    JSON string to deserialize.
     * @param typeRef Jackson generic type reference.
     * @param <T>     Return type.
     * @return Deserialized instance.
     * @throws MappingException if JSON is invalid or incompatible.
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