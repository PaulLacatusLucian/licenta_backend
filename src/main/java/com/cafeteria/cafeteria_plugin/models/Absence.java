package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Modellklasse für Abwesenheiten im Schulverwaltungssystem.
 *
 * Diese Klasse repräsentiert eine registrierte Abwesenheit eines Schülers
 * während einer spezifischen Klassensitzung. Sie ermöglicht die Verfolgung
 * der Anwesenheit und unterstützt die Kommunikation zwischen Schule und Eltern.
 *
 * Merkmale einer Abwesenheit:
 * - Verknüpfung zu einer spezifischen Klassensitzung
 * - Identifikation des abwesenden Schülers
 * - Lehrer, der die Abwesenheit registriert hat
 * - Automatische Zeitstempelung durch Klassensitzung
 *
 * Geschäftslogik:
 * - Ein Schüler kann nur einmal pro Klassensitzung als abwesend markiert werden
 * - Abwesende Schüler können keine Noten in derselben Sitzung erhalten
 * - Nur Lehrer können Abwesenheiten registrieren
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @see ClassSession
 * @see Teacher
 * @since 2024-12-18
 */
@Entity
@Data
public class Absence {

    /**
     * Eindeutige Identifikationsnummer der Abwesenheit.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Schüler, der als abwesend registriert wurde.
     *
     * Diese Many-to-One-Beziehung identifiziert eindeutig den Schüler,
     * der während der Klassensitzung nicht anwesend war.
     */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Klassensitzung, während der die Abwesenheit registriert wurde.
     *
     * Diese Many-to-One-Beziehung stellt den vollständigen Kontext bereit:
     * - Datum und Uhrzeit der Sitzung
     * - Unterrichtsfach
     * - Klassenzimmer und Klasse
     * - Dauer der verpassten Unterrichtszeit
     */
    @ManyToOne
    @JoinColumn(name = "class_session_id", nullable = false)
    private ClassSession classSession;

    /**
     * Lehrer, der die Abwesenheit registriert hat.
     *
     * Diese Many-to-One-Beziehung ermöglicht die Nachverfolgung,
     * welcher Lehrer die Abwesenheit festgestellt und eingetragen hat.
     * Dies ist wichtig für Rückfragen und Verantwortlichkeit.
     */
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private Boolean justified = false;
}