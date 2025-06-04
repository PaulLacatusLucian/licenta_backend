package com.cafeteria.cafeteria_plugin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object für Noten-Informationen.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über eine Schüler-Note
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * vereinfachte, serialisierbare Darstellung der Grade-Entität dar und
 * integriert Kontext-Informationen für vollständige Note-Darstellung.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Noten-Bewertung
 * - Beschreibende Informationen zur Note
 * - Lehrer- und Fach-Kontext
 * - Zeitstempel für chronologische Einordnung
 * - Optimierte Struktur für Frontend-Darstellung
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Noten-Abfragen
 * - Schüler- und Eltern-Portale für Notenübersichten
 * - Lehrer-Tools für Bewertungshistorie
 * - Zeugnis-Generierung und Berichte
 * - Statistische Auswertungen
 * <p>
 * Technische Eigenschaften:
 * - Immutable Design mit All/NoArgsConstructor
 * - JSON-serialisierbar für REST APIs
 * - Flache Struktur für Performance-Optimierung
 * - Integration von Kontext-Informationen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.Grade
 * @see com.cafeteria.cafeteria_plugin.models.ClassSession
 * @see com.cafeteria.cafeteria_plugin.models.Teacher
 * @since 2025-01-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeDTO {
    private Long id;


    /**
     * Numerische Bewertung der Schülerleistung.
     * <p>
     * Hauptbewertung der Note:
     * - Deutsche Notenskala (typisch 1.0 - 6.0)
     * - Basis für Durchschnittsberechnungen
     * - Vergleichswert für Leistungsanalysen
     * - Zeugnis-relevante Bewertung
     * <p>
     * Validierung:
     * - Wertebereich entsprechend Schulsystem
     * - Decimal-Präzision für Zwischennoten
     * - Pflichtfeld für alle Bewertungen
     */
    private Double grade;

    /**
     * Zusätzliche Beschreibung zur Note.
     * <p>
     * Erläuternde Informationen zur Bewertung:
     * - Kontext der Leistungserbringung
     * - Spezifische Bewertungskriterien
     * - Feedback für Schüler und Eltern
     * - Zusätzliche Qualifikation der Note
     * <p>
     * Beispiele:
     * - "Klassenarbeit Kapitel 3"
     * - "Mündliche Beteiligung"
     * - "Projektarbeit Teamwork"
     * - "Nachprüfung"
     */
    private String description;

    /**
     * Name des bewertenden Lehrers.
     * <p>
     * Vollständiger Name des Lehrers, der die Note vergeben hat:
     * - Verantwortlichkeit und Nachverfolgbarkeit
     * - Kontakt-Information für Rückfragen
     * - Autorität der Bewertung
     * - Unterscheidung zwischen verschiedenen Fachlehrern
     */
    private String teacherName;

    /**
     * Unterrichtsfach der bewerteten Leistung.
     * <p>
     * Name des Schulfachs:
     * - Klassifikation für fachspezifische Übersichten
     * - Gruppierung in Noten-Darstellungen
     * - Basis für Fach-Durchschnitte
     * - Zeugnis-Zuordnung
     */
    private String subject;

    /**
     * Zeitstempel der Unterrichtsstunde.
     * <p>
     * Datum und Uhrzeit der Unterrichtsstunde, in der die Note vergeben wurde:
     * - Chronologische Einordnung der Bewertung
     * - Basis für zeitbasierte Analysen
     * - Wichtig für Notenentwicklung über Zeit
     * - Semester- und Periode-Zuordnung
     * - Referenz für Eltern-Kommunikation
     */
    private LocalDateTime sessionDate;

    private Long studentId;
    private String studentName;
}