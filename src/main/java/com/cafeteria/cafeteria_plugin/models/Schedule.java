package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Modellklasse für Stundenplaneinträge im Schulverwaltungssystem.
 * <p>
 * Diese Klasse repräsentiert einen einzelnen Eintrag im Wochenstundenplan
 * einer Klasse. Sie definiert, wann welcher Lehrer welche Fächer für eine
 * bestimmte Klasse unterrichtet.
 * <p>
 * Ein Stundenplaneintrag umfasst:
 * - Zeitraum (Wochentag, Start- und Endzeit)
 * - Zugeordnete Klasse
 * - Unterrichtende Lehrkraft
 * - Liste der Fächer, die in diesem Zeitslot unterrichtet werden
 * <p>
 * Verwendung:
 * - Grundlage für die Generierung von Klassensitzungen
 * - Anzeige von Wochenstundenplänen für Schüler und Lehrer
 * - Planung und Organisation des Schulbetriebs
 * - Konfliktprüfung bei Terminplanung
 * <p>
 * Geschäftslogik:
 * - Ein Eintrag kann mehrere Fächer umfassen (z.B. Doppelstunden)
 * - Verschiedene Lehrer können für verschiedene Fächer zuständig sein
 * - Automatische Validierung von Zeitkonflikten
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Class
 * @see Teacher
 * @see ClassSession
 * @since 2024-12-18
 */
@Data
@Entity
public class Schedule {

    /**
     * Eindeutige Identifikationsnummer des Stundenplaneintrags.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Schulklasse, für die dieser Stundenplaneintrag gilt.
     * <p>
     * Diese Many-to-One-Beziehung ordnet den Stundenplaneintrag einer
     * spezifischen Klasse zu. JsonBackReference verhindert zirkuläre
     * Referenzen bei der JSON-Serialisierung, da die Klasse bereits
     * eine Liste ihrer Stundenpläne enthält.
     */
    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonBackReference
    @ToString.Exclude
    private Class studentClass;

    /**
     * Wochentag, an dem der Unterricht stattfindet.
     * <p>
     * Gespeichert in deutscher Sprache für bessere Benutzerfreundlichkeit:
     * "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"
     * <p>
     * Wird für die Filterung nach Tagen und die Anzeige von
     * tagesspezifischen Stundenplänen verwendet.
     */
    private String scheduleDay;

    /**
     * Startzeit des Unterrichts im Format "H:mm".
     * <p>
     * Beispiele: "8:00", "9:45", "14:30"
     * Wird für die chronologische Sortierung und Anzeige verwendet.
     * Das Format ermöglicht einfache Parsing und Vergleichsoperationen.
     */
    private String startTime;

    /**
     * Endzeit des Unterrichts im Format "H:mm".
     * <p>
     * Beispiele: "8:50", "10:35", "15:20"
     * Wird für die Berechnung der Unterrichtsdauer und
     * Überschneidungsprüfungen verwendet.
     */
    private String endTime;

    /**
     * Liste der Fächer, die in diesem Zeitslot unterrichtet werden.
     * <p>
     * Ermöglicht flexible Stundenplangestaltung:
     * - Einzelstunden: ["Mathematik"]
     * - Doppelstunden: ["Mathematik", "Mathematik"]
     * - Kombinierte Stunden: ["Geschichte", "Geografie"]
     * <p>
     * Wird als separate Tabelle gespeichert für effiziente Abfragen
     * und flexible Erweiterung.
     */
    @ElementCollection
    private List<String> subjects;

    /**
     * Lehrer, der für diesen Stundenplaneintrag verantwortlich ist.
     * <p>
     * Diese Many-to-One-Beziehung identifiziert die Lehrkraft, die
     * den Unterricht in diesem Zeitslot durchführt. Ein Lehrer kann
     * multiple Stundenplaneinträge haben (verschiedene Klassen/Zeiten).
     * <p>
     * Der Foreign Key ermöglicht effiziente Abfragen nach Lehrer-Stundenplänen
     * und Verfügbarkeitsprüfungen.
     */
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}