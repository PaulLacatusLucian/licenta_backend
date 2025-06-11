package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object für Unterrichtsstunden-Informationen.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über eine Unterrichtsstunde
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * vollständige, serialisierbare Darstellung der ClassSession-Entität dar und
 * integriert alle zugehörigen Bewertungen und Fehlzeiten.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Unterrichtsstunden-Informationen
 * - Lehrer- und Fachzuordnung
 * - Zeitraum und Klassenkontext
 * - Aggregierte Noten und Fehlzeiten
 * - Vollständige Unterrichtsdokumentation
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Unterrichtsstunden-Details
 * - Lehrer-Dashboard für Stundenverwaltung
 * - Klassenbuch-Darstellung mit allen Bewertungen
 * - Stundenplan-Integration mit Live-Daten
 * - Berichte und Unterrichtsstatistiken
 * <p>
 * Technische Eigenschaften:
 * - Vollständige Aggregation verwandter Daten
 * - JSON-serialisierbar für REST APIs
 * - Optimiert für komplexe Frontend-Darstellungen
 * - Vermeidung von N+1 Query-Problemen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.ClassSession
 * @see TeacherDTO
 * @see AbsenceDTO
 * @see GradeDTO
 * @since 2025-03-13
 */
@Data
public class ClassSessionDTO {

    /**
     * Eindeutige Identifikation der Unterrichtsstunde.
     * <p>
     * Primärschlüssel der ClassSession-Entität:
     * - Referenz für Noten- und Fehlzeiten-Zuordnung
     * - API-Endpunkt-Parameter für spezifische Stunden
     * - Basis für Update- und Delete-Operationen
     */
    private Long id;

    /**
     * Unterrichtsfach der Stunde.
     * <p>
     * Name des gelehrten Fachs:
     * - Klassifikation für Noten und Bewertungen
     * - Filterkriterium für fachspezifische Auswertungen
     * - Basis für Lehrplan-Zuordnung
     * - Wichtig für Eltern-Übersichten nach Fächern
     */
    private String subject;

    /**
     * Startzeitpunkt der Unterrichtsstunde.
     * <p>
     * Genaue Anfangszeit der Stunde:
     * - Basis für Anwesenheitskontrolle
     * - Chronologische Sortierung
     * - Konfliktprüfung bei Stundenplanung
     * - Zeitstempel für Bewertungen
     */
    private LocalDateTime startTime;

    /**
     * Endzeitpunkt der Unterrichtsstunde.
     * <p>
     * Genaue Endzeit der Stunde:
     * - Dauer-Berechnung für Statistiken
     * - Vollständige Zeitraum-Definition
     * - Überschneidungsprüfung mit anderen Terminen
     */
    private LocalDateTime endTime;

    /**
     * Vollständige Lehrer-Informationen als verschachtelte DTO.
     * <p>
     * Enthält alle relevanten Lehrer-Daten:
     * - Name, Fach, Kontaktinformationen
     * - Qualifikationen und Spezialisierungen
     * - Optimiert für Frontend-Darstellung
     * - Vermeidet zusätzliche API-Calls
     */
    private TeacherDTO teacher;

    /**
     * Liste aller Fehlzeiten in dieser Unterrichtsstunde.
     * <p>
     * Vollständige Fehlzeiten-Dokumentation:
     * - Schüler-spezifische Fehlzeiten mit Details
     * - Basis für Anwesenheitsstatistiken
     * - Integration in Klassenbuch-Darstellung
     * - Eltern-Benachrichtigungen und -Übersichten
     */
    private List<AbsenceDTO> absences;

    /**
     * Liste aller Noten in dieser Unterrichtsstunde.
     * <p>
     * Vollständige Bewertungs-Dokumentation:
     * - Schüler-spezifische Noten mit Beschreibungen
     * - Basis für Leistungsstatistiken
     * - Integration in Noten-Übersichten
     * - Zeugnis- und Berichts-Generierung
     */
    private List<GradeDTO> grades;

    /**
     * Wochentag der Unterrichtsstunde.
     * <p>
     * Deutschen Wochentag-Bezeichnung:
     * - Stundenplan-Integration und -Darstellung
     * - Wiederkehrende Termine-Verwaltung
     * - Wöchentliche Statistiken und Berichte
     * - Lokalisierung für deutsche Benutzeroberfläche
     */
    private String scheduleDay;

    /**
     * Name der unterrichteten Klasse.
     * <p>
     * Bezeichnung der Schulklasse:
     * - Kontext für klassenspezifische Auswertungen
     * - Navigation und Filterung nach Klassen
     * - Klassenbuch-Integration
     * - Organisatorische Zuordnung
     */
    private String className;
}