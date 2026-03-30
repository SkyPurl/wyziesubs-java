package io.github.skypurl.wyziesubs.exception;

/**
 * Exception levée lors d'un échec de désérialisation JSON.
 *
 * <p>Encapsule les {@code JsonProcessingException} de Jackson afin que
 * l'utilisateur du SDK n'ait pas à importer ou connaître les exceptions
 * internes de Jackson dans ses propres blocs {@code catch}.</p>
 *
 * <p>La cause originelle (Jackson) reste accessible via {@link #getCause()}
 * pour le debug avancé.</p>
 */
public final class MappingException extends WyzieSubsException {

    /**
     * Crée une {@code MappingException} en encapsulant l'exception Jackson.
     *
     * @param message Description du contexte de l'erreur (ex: quel endpoint).
     * @param cause   L'exception Jackson d'origine ({@code JsonProcessingException}).
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}