package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modellklasse für Klassensitzungen im Schulverwaltungssystem.
 * <p>
 * Diese Klasse repräsentiert eine einzelne Unterrichtsstunde oder -sitzung,
 * die von einem Lehrer für ein bestimmtes Fach durchgeführt wird.
 * Sie dient als zentraler Punkt für die Verwaltung von Noten und Anwesenheit.
 * <p>
 * Eine Klassensitzung umfasst:
 * - Zeitraum (Start- und Endzeit)
 * - Unterrichtsfach
 * - Leitender Lehrer
 * - Zugehörige Klasse
 * - Alle Noten, die während der Sitzung vergeben wurden
 * - Alle Abwesenheiten, die registriert wurden
 * <p>
 * Verwendung:
 * - Automatische Generierung aus Stundenplänen
 * - Manuelle Erstellung für spontane Sitzungen
 * - Grundlage für Noten- und Anwesenheitsverwaltung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Teacher
 * @see Grade
 * @see Absence
 * @see Schedule
 * @since 2025-01-19
 */
@Data
@Entity
public class ClassSession {

    /**
     * Eindeutige Identifikationsnummer der Klassensitzung.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unterrichtsfach für diese Sitzung.
     * <p>
     * Beispiele: "Mathematik", "Deutsch", "Geschichte", "Sport"
     * Muss mit den im System definierten Fächern übereinstimmen.
     */
    @Column(nullable = false)
    private String subject;

    /**
     * Lehrer, der diese Sitzung leitet.
     * <p>
     * Diese Many-to-One-Beziehung identifiziert den verantwortlichen Lehrer
     * für Unterricht, Notenvergabe und Anwesenheitskontrolle.
     * JsonIgnore verhindert zirkuläre Referenzen bei der Serialisierung.
     */
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnore
    private Teacher teacher;

    /**
     * Startzeit der Klassensitzung.
     * <p>
     * Vollständiger Zeitstempel mit Datum und Uhrzeit.
     * Wird für Stundenplan-Generierung und Anwesenheitskontrolle verwendet.
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * Endzeit der Klassensitzung.
     * <p>
     * Vollständiger Zeitstempel mit Datum und Uhrzeit.
     * Wird zur Berechnung der Unterrichtsdauer verwendet.
     */
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * Liste aller Abwesenheiten, die während dieser Sitzung registriert wurden.
     * <p>
     * Diese One-to-Many-Beziehung ermöglicht die Verfolgung aller Schüler,
     * die während dieser spezifischen Sitzung abwesend waren.
     * JsonIgnore verhindert übermäßige Datenübertragung bei der Serialisierung.
     */
    @OneToMany(mappedBy = "classSession", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<Absence> absences;

    /**
     * Liste aller Noten, die während dieser Sitzung vergeben wurden.
     * <p>
     * Diese One-to-Many-Beziehung sammelt alle Bewertungen,
     * die der Lehrer in dieser Unterrichtsstunde vorgenommen hat.
     */
    @OneToMany(mappedBy = "classSession", cascade = CascadeType.ALL)
    private List<Grade> grades;

    /**
     * Wochentag der Sitzung in deutscher Sprache.
     * <p>
     * Beispiele: "Montag", "Dienstag", "Mittwoch"
     * Wird für Stundenplan-Anzeige und Filterung verwendet.
     */
    private String scheduleDay;

    /**
     * Name der Klasse, für die diese Sitzung stattfindet.
     * <p>
     * Denormalisiertes Feld für schnelle Abfragen und Anzeige.
     * Beispiele: "5A", "10B", "12C"
     */
    private String className;
}