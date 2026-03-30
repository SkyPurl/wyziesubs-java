package io.github.skypurl.wyziesubs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Représente un sous-titre retourné par l'endpoint {@code GET /search}.
 *
 * <p>Les champs {@code format}, {@code source} et {@code origin} sont volontairement
 * typés en {@code String} (et non en enums) pour garantir la résilience de la
 * désérialisation si l'API introduit de nouvelles valeurs.</p>
 *
 * <p>Tous les champs sont nullable sauf {@code id} et {@code url}.</p>
 *
 * @param id               Identifiant unique du sous-titre.
 * @param url              URL de téléchargement direct.
 * @param flagUrl          URL du drapeau de la langue (peut être {@code null}).
 * @param format           Format du fichier (ex: {@code "srt"}, {@code "ass"}).
 * @param encoding         Encodage du fichier (ex: {@code "UTF-8"}).
 * @param display          Nom affiché de la langue (ex: {@code "English"}).
 * @param language         Code ISO 639-1 de la langue (ex: {@code "en"}).
 * @param media            Titre du média associé.
 * @param isHearingImpaired Indique si le sous-titre est adapté aux malentendants.
 * @param source           Source du sous-titre (ex: {@code "opensubtitles"}).
 * @param release          Release principale associée.
 * @param releases         Liste de toutes les releases compatibles.
 * @param fileName         Nom du fichier de sous-titre.
 * @param origin           Origine du média (ex: {@code "WEB-DL"}).
 * @param matchedRelease   Release qui a déclenché le match (peut être {@code null}).
 * @param matchedFilter    Filtre qui a déclenché le match (peut être {@code null}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Subtitle(
        String id,
        String url,
        String flagUrl,
        String format,
        String encoding,
        String display,
        String language,
        String media,
        @JsonProperty("isHearingImpaired") Boolean isHearingImpaired,
        String source,
        String release,
        List<String> releases,
        String fileName,
        String origin,
        String matchedRelease,
        String matchedFilter
) {}