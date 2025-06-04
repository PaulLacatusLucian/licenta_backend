package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

/**
 * Modellklasse für Schulklassen im Verwaltungssystem.
 * <p>
 * Diese Klasse repräsentiert eine Schulklasse mit allen zugehörigen Informationen
 * wie Klassenlehrer, Schüler, Stundenpläne und Bildungsebene.
 * <p>
 * Bildungsebenen:
 * - PRIMARY (0-4): Grundschule mit Erziehern
 * - MIDDLE (5-8): Mittelschule mit Fachlehrern
 * - HIGH (9-12): Oberschule mit Spezialisierungen
 * <p>
 * Eine Klasse kann:
 * - Einen zugewiesenen Klassenlehrer haben
 * - Multiple Stundenpläne für verschiedene Fächer haben
 * - Eine Spezialisierung haben (nur Oberschule)
 * - Mehrere Schüler enthalten
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Teacher
 * @see Student
 * @see Schedule
 * @see EducationLevel
 * @since 2024-12-18
 */
@Data
@Entity
public class Class {

    /**
     * Eindeutige Identifikationsnummer der Klasse.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name der Klasse (z.B. "5A", "10B", "12C").
     * Folgt dem Schema: [Jahrgangsstufe][Buchstabe]
     */
    private String name;

    /**
     * Klassenlehrer, der für diese Klasse verantwortlich ist.
     * <p>
     * Diese One-to-One-Beziehung stellt sicher, dass jede Klasse
     * einen zugewiesenen Lehrer hat. Der Lehrertyp muss zur
     * Bildungsebene der Klasse passen:
     * - PRIMARY: Benötigt EDUCATOR
     * - MIDDLE/HIGH: Benötigt TEACHER
     */
    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "teacher_id", referencedColumnName = "id", unique = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Teacher classTeacher;

    /**
     * Liste aller Stundenpläne für diese Klasse.
     * <p>
     * Eine Klasse kann mehrere Stundenpläne haben, die verschiedene
     * Fächer, Zeiten und Lehrer abdecken. Diese One-to-Many-Beziehung
     * ermöglicht flexible Stundenplanung.
     */
    @OneToMany(mappedBy = "studentClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Schedule> schedules;

    /**
     * Bildungsebene der Klasse, die Regeln und Berechtigungen bestimmt.
     * <p>
     * - PRIMARY: Grundschule (0-4) - Ein Erzieher unterrichtet alle Fächer
     * - MIDDLE: Mittelschule (5-8) - Verschiedene Fachlehrer
     * - HIGH: Oberschule (9-12) - Spezialisierte Lehrer und Spezialisierungen
     */
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    /**
     * Spezialisierung der Klasse (nur für Oberschule).
     * <p>
     * Mögliche Spezialisierungen:
     * - "Mathematik-Informatik"
     * - "Mathematik-Informatik-Bilingv"
     * - "Filologie"
     * - "Bio-Chemie"
     * <p>
     * Für PRIMARY und MIDDLE muss dieses Feld null sein.
     * Für HIGH ist es obligatorisch.
     */
    private String specialization;
}