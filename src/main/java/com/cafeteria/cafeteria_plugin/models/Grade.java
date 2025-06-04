package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

/**
 * Modellklasse für Noten im Schulverwaltungssystem.
 * <p>
 * Diese Klasse repräsentiert eine einzelne Note, die einem Schüler
 * während einer Klassensitzung vergeben wurde. Sie verbindet Schüler,
 * Lehrer, Fach und Bewertung miteinander.
 * <p>
 * Merkmale einer Note:
 * - Numerischer Wert (typischerweise 1-10 im rumänischen System)
 * - Optionale Beschreibung/Kommentar
 * - Verknüpfung zu einer spezifischen Klassensitzung
 * - Zuordnung zu einem Schüler
 * <p>
 * Geschäftslogik:
 * - Ein Schüler kann nur eine Note pro Klassensitzung erhalten
 * - Noten können nur von Lehrern vergeben werden
 * - Abwesende Schüler können keine Noten erhalten
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @see ClassSession
 * @see Teacher
 * @since 2024-12-18
 */
@Data
@Entity
public class Grade {

    /**
     * Eindeutige Identifikationsnummer der Note.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numerischer Wert der Note.
     * <p>
     * Typischerweise im Bereich von 1.0 bis 10.0 im rumänischen Schulsystem:
     * - 1-4: Ungenügend
     * - 5-6: Genügend
     * - 7-8: Gut
     * - 9-10: Sehr gut/Ausgezeichnet
     */
    private Double grade;

    /**
     * Optionale Beschreibung oder Kommentar zur Note.
     * <p>
     * Kann verwendet werden für:
     * - Art der Bewertung (Test, mündliche Prüfung, Hausaufgabe)
     * - Zusätzliche Erklärungen
     * - Feedback für den Schüler
     */
    private String description;

    /**
     * Klassensitzung, in der diese Note vergeben wurde.
     * <p>
     * Diese Many-to-One-Beziehung stellt den Kontext der Note bereit:
     * - Welches Fach
     * - Welcher Lehrer
     * - Welches Datum
     * - Welche Klasse
     * <p>
     * JsonIgnore verhindert zirkuläre Referenzen bei der Serialisierung.
     */
    @Getter
    @ManyToOne
    @JoinColumn(name = "class_session_id", nullable = false)
    @JsonIgnore
    private ClassSession classSession;

    /**
     * Schüler, der diese Note erhalten hat.
     * <p>
     * Diese Many-to-One-Beziehung identifiziert eindeutig,
     * welcher Schüler bewertet wurde.
     */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}