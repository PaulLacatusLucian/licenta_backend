package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object für Stundenplan-Informationen.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über einen Stundenplan-Eintrag
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * vollständige, serialisierbare Darstellung der Schedule-Entität dar und
 * integriert Lehrer- und Klasseninformationen für umfassende Stundenplan-Darstellung.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Zeitplan-Informationen
 * - Wochentag- und Zeitraum-Definition
 * - Fächer-Liste für Multi-Fach-Stunden
 * - Lehrer-Zuordnung mit vollständigen Details
 * - Klassenkontext für Organisationszwecke
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Stundenplan-Abfragen
 * - Frontend-Darstellung von Wochenplänen
 * - Schüler- und Eltern-Portale für Stundenübersichten
 * - Lehrer-Tools für Unterrichtsplanung
 * - Mobile Apps für Stundenplan-Anzeige
 * <p>
 * Technische Eigenschaften:
 * - Optimiert für kalendarische Darstellungen
 * - JSON-serialisierbar für REST APIs
 * - Integrierte Lehrer-Informationen
 * - Flexible Fächer-Unterstützung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.Schedule
 * @see TeacherDTO
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-03-24
 */
@Data
public class ScheduleDTO {

    /**
     * Eindeutige Identifikation des Stundenplan-Eintrags.
     * <p>
     * Primärschlüssel der Schedule-Entität:
     * - Referenz für Stundenplan-Operationen
     * - API-Endpunkt-Parameter für spezifische Einträge
     * - Basis für Update- und Delete-Operationen
     * - Verknüpfung zu ClassSession-Generierung
     */
    private Long id;

    /**
     * Wochentag des Stundenplan-Eintrags.
     * <p>
     * Deutsche Bezeichnung des Wochentags:
     * - Lokalisierte Darstellung (Montag, Dienstag, etc.)
     * - Basis für wochenbasierte Stundenplan-Gruppierung
     * - Sortierkriterium für chronologische Darstellung
     * - Integration mit Kalender-Komponenten
     * <p>
     * Gültige Werte: Montag, Dienstag, Mittwoch, Donnerstag,
     * Freitag, Samstag, Sonntag
     */
    private String scheduleDay;

    /**
     * Startuhrzeit der Unterrichtsstunde.
     * <p>
     * Zeitformat für Stundenbeginn:
     * - Format: "HH:mm" (24-Stunden-Format)
     * - Basis für Zeitraum-Berechnung
     * - Sortierung der Tagesstunden
     * - Integration mit Zeitplan-Visualisierung
     * <p>
     * Beispiele: "08:00", "13:45", "15:30"
     */
    private String startTime;

    /**
     * Enduhrzeit der Unterrichtsstunde.
     * <p>
     * Zeitformat für Stundenende:
     * - Format: "HH:mm" (24-Stunden-Format)
     * - Vollständige Zeitraum-Definition
     * - Überschneidungsprüfung möglich
     * - Dauer-Berechnung für Statistiken
     * <p>
     * Beispiele: "08:45", "14:30", "16:15"
     */
    private String endTime;

    /**
     * Liste der unterrichteten Fächer.
     * <p>
     * Alle Fächer in dieser Stundenplan-Einheit:
     * - Unterstützung für Block-Unterricht
     * - Mehrfach-Fach-Stunden (z.B. fächerübergreifend)
     * - Flexible Unterrichtsgestaltung
     * - Basis für Fach-spezifische Auswertungen
     * <p>
     * Typische Inhalte:
     * - Einzelfach: ["Mathematik"]
     * - Block-Unterricht: ["Physik", "Chemie"]
     * - Projektstunden: ["Deutsch", "Geschichte"]
     */
    private List<String> subjects;

    /**
     * Vollständige Lehrer-Informationen als verschachtelte DTO.
     * <p>
     * Enthält alle relevanten Lehrer-Daten:
     * - Name, Fach, Kontaktinformationen
     * - Qualifikationen und Spezialisierungen
     * - Optimiert für Frontend-Darstellung
     * - Vermeidet zusätzliche API-Calls
     * <p>
     * Der Lehrer ist verantwortlich für:
     * - Durchführung der Unterrichtsstunde
     * - Noten- und Fehlzeiten-Erfassung
     * - Unterrichtsplanung und -vorbereitung
     * - Eltern-Kommunikation zu diesem Fach
     */
    private TeacherDTO teacher;

    /**
     * Name der unterrichteten Klasse.
     * <p>
     * Bezeichnung der Schulklasse:
     * - Organisatorische Zuordnung
     * - Kontext für klassenspezifische Auswertungen
     * - Navigation und Filterung nach Klassen
     * - Integration mit Klassenverwaltung
     * <p>
     * Beispiele: "10A", "9B", "12C", "5D"
     */
    private String className;
}