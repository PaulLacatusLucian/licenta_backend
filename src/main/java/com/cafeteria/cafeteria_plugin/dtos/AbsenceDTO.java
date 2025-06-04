package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object für Fehlzeiten-Informationen.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über eine Schüler-Fehlzeit
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * vereinfachte, serialisierbare Darstellung der Absence-Entität dar und
 * vermeidet zirkuläre Referenzen in JSON-Serialisierung.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Fehlzeit-Identifikation
 * - Schüler-Informationen als verschachtelte DTO
 * - Unterrichtsstunden-Referenz
 * - Zeitstempel der Fehlzeit
 * - Lehrer-Informationen für Verantwortlichkeit
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Fehlzeiten-Abfragen
 * - Frontend-Darstellung in Klassenbüchern
 * - Eltern-Portale für Fehlzeiten-Übersichten
 * - Lehrer-Tools für Anwesenheitsverwaltung
 * - Berichte und Statistiken
 * <p>
 * Technische Eigenschaften:
 * - Immutable Design für Thread-Safety
 * - JSON-serialisierbar für REST APIs
 * - Lombok-Annotations für automatische Getter/Setter
 * - Vermeidung von Entity-Beziehungen für Performance
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.Absence
 * @see StudentDTO
 * @see TeacherDTO
 * @since 2025-01-01
 */
@Data
public class AbsenceDTO {

    /**
     * Eindeutige Identifikation der Fehlzeit.
     * <p>
     * Primärschlüssel der Fehlzeit-Entität, verwendet für:
     * - Eindeutige Referenzierung in APIs
     * - Update- und Delete-Operationen
     * - Datenbankverknüpfungen
     */
    private Long id;

    /**
     * Vollständige Schüler-Informationen als verschachtelte DTO.
     * <p>
     * Enthält alle relevanten Schüler-Daten für die Anzeige:
     * - Name, Klasse, Kontaktinformationen
     * - Vermeidet zusätzliche API-Calls für Schüler-Details
     * - Optimiert für Frontend-Darstellung
     */
    private StudentDTO student;

    /**
     * Referenz zur zugehörigen Unterrichtsstunde.
     * <p>
     * ID der ClassSession, in der die Fehlzeit auftrat:
     * - Verknüpfung zu Fach und Lehrer
     * - Zeitstempel-Information
     * - Kontext für die Fehlzeit
     */
    private Long classSessionId;

    /**
     * Zeitstempel der Unterrichtsstunde.
     * <p>
     * Datum und Uhrzeit der Unterrichtsstunde, nicht der Fehlzeit-Erfassung:
     * - Zeigt den tatsächlichen Zeitpunkt der Fehlzeit
     * - Wichtig für chronologische Sortierung
     * - Basis für Berichte und Statistiken
     */
    private LocalDateTime sessionDate;

    /**
     * Informationen über den Lehrer, der die Fehlzeit erfasst hat.
     * <p>
     * Vollständige Lehrer-Daten für Verantwortlichkeit und Nachverfolgung:
     * - Name und Fach des erfassenden Lehrers
     * - Kontaktinformationen für Rückfragen
     * - Autoritäts-Nachweis für die Fehlzeit
     * - Unterscheidet zwischen Fachlehrer und Klassenlehrer
     */
    private TeacherDTO teacherWhoMarkedAbsence;

    private String subject;
    private String className;
    private LocalDateTime date;
    private Boolean justified;

}