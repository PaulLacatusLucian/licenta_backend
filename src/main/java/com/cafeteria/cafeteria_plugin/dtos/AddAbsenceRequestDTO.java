package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

/**
 * Data Transfer Object für Fehlzeit-Erstellungsanfragen.
 * <p>
 * Diese Klasse kapselt die minimal notwendigen Informationen für die
 * Erstellung einer neuen Fehlzeit. Sie dient als Eingabe-DTO für
 * REST API Endpunkte und stellt sicher, dass nur die erforderlichen
 * Daten für die Fehlzeit-Erfassung übertragen werden.
 * <p>
 * Das DTO enthält:
 * - Schüler-Identifikation für die Fehlzeit
 * - Unterrichtsstunden-Referenz (optional für Updates)
 * - Minimale Datenstruktur für effiziente Übertragung
 * <p>
 * Verwendungsszenarien:
 * - REST API Requests für neue Fehlzeiten
 * - Lehrer-Interface für Anwesenheitskontrolle
 * - Batch-Import von Fehlzeiten
 * - Mobile Apps für schnelle Erfassung
 * - Update-Operationen für bestehende Fehlzeiten
 * <p>
 * Technische Eigenschaften:
 * - Minimale Datenstruktur für Performance
 * - Validierbar durch Bean Validation
 * - JSON-serialisierbar für REST APIs
 * - Lombok-Annotations für automatische Getter/Setter
 * - Flexibel für verschiedene API-Endpunkte
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see AbsenceDTO
 * @see com.cafeteria.cafeteria_plugin.models.Absence
 * @see com.cafeteria.cafeteria_plugin.controllers.AbsenceController
 * @since 2025-03-13
 */
@Data
public class AddAbsenceRequestDTO {

    /**
     * Eindeutige Identifikation des fehlenden Schülers.
     * <p>
     * Primärschlüssel des Schülers, für den die Fehlzeit erfasst wird:
     * - Pflichtfeld für alle Fehlzeit-Operationen
     * - Referenz zur Student-Entität
     * - Basis für Geschäftslogik-Validierung
     * - Verknüpfung zu Eltern-Benachrichtigungen
     * <p>
     * Validierung:
     * - Muss existierender Schüler sein
     * - Schüler muss der Klasse der Unterrichtsstunde zugeordnet sein
     * - Keine Duplikate pro Unterrichtsstunde erlaubt
     */
    private Long studentId;

    /**
     * Referenz zur zugehörigen Unterrichtsstunde (optional).
     * <p>
     * ID der ClassSession, in der die Fehlzeit auftrat:
     * - Notwendig für Update-Operationen
     * - Optional bei direkter Session-basierten Erstellung
     * - Kontext für Fach und Zeitraum
     * - Basis für Konfliktprüfung mit Noten
     * <p>
     * Verwendung:
     * - Update-Endpunkte: Pflichtfeld für Klassifikation
     * - Create-Endpunkte: Optional wenn Session über URL-Parameter
     * - Batch-Operationen: Notwendig für Zuordnung
     * - Validierung: Prüfung auf Schüler-Session-Kompatibilität
     */
    private Long classSessionId;

    private Boolean justified;

}