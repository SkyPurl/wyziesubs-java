package io.github.skypurl.wyziesubs.exception;

/**
 * Exception racine du SDK WyzieSubs.
 *
 * <p>Toutes les exceptions métier du SDK étendent cette classe, ce qui permet
 * à l'utilisateur de capturer l'ensemble des erreurs SDK avec un seul
 * {@code catch (WyzieSubsException e)} si nécessaire.</p>
 */
public class WyzieSubsException extends RuntimeException {

    /**
     * Crée une exception avec un message descriptif.
     *
     * @param message Description de l'erreur.
     */
    public WyzieSubsException(String message) {
        super(message);
    }

    /**
     * Crée une exception avec un message descriptif et la cause originelle.
     *
     * @param message Description de l'erreur.
     * @param cause   Exception d'origine ayant provoqué cette erreur.
     */
    public WyzieSubsException(String message, Throwable cause) {
        super(message, cause);
    }
}