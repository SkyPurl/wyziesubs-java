package io.github.skypurl.wyziesubs.exception;

/**
 * Exception levée lorsque l'API Wyzie Subs retourne une réponse HTTP d'erreur
 * (codes 4xx ou 5xx).
 *
 * <p>Exemple d'utilisation dans un {@code catch} :</p>
 * <pre>{@code
 * client.search(request)
 *       .exceptionally(ex -> {
 *           if (ex.getCause() instanceof ApiException apiEx) {
 *               System.err.println("HTTP " + apiEx.getStatusCode());
 *               System.err.println("Body : " + apiEx.getResponseBody());
 *           }
 *           return List.of();
 *       });
 * }</pre>
 */
public final class ApiException extends WyzieSubsException {

    private final int statusCode;
    private final String responseBody;

    /**
     * Crée une {@code ApiException} à partir d'une réponse HTTP en erreur.
     *
     * @param statusCode   Code de statut HTTP reçu (ex: 401, 404, 500).
     * @param responseBody Corps brut de la réponse (peut être vide, jamais {@code null}).
     */
    public ApiException(int statusCode, String responseBody) {
        super("API request failed with HTTP %d : %s".formatted(statusCode, responseBody));
        this.statusCode   = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Retourne le code de statut HTTP de la réponse en erreur.
     *
     * @return Code HTTP (ex: {@code 401}, {@code 404}, {@code 500}).
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Retourne le corps brut de la réponse HTTP en erreur.
     *
     * @return Corps de la réponse, potentiellement vide mais jamais {@code null}.
     */
    public String getResponseBody() {
        return responseBody;
    }
}