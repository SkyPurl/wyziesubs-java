package io.github.skypurl.wyziesubs.util;

/**
 * Contrat commun pour toutes les énumérations utilisées comme paramètres d'API.
 * Permet à {@link io.github.skypurl.wyziesubs.request.SearchRequest} de dépendre uniquement
 * de ce package {@code util}, évitant toute dépendance cyclique avec {@code enums}.
 */
public interface ApiParameter {
    String getValue();
}