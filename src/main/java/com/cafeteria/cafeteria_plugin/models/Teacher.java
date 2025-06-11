package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * Modellklasse für Lehrer und Erzieher im Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see TeacherType
 * @see Class
 * @see Schedule
 * @see ClassSession
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher extends User {

    /**
     * Vollständiger Name des Lehrers.
     * Wird für Anzeige und Identifikation im System verwendet.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Hauptfach oder Spezialisierung des Lehrers.
     *
     * Für EDUCATOR: Typischerweise "Grundschullehrer" oder "Erzieher"
     * Für TEACHER: Spezifisches Fach wie "Mathematik", "Deutsch", etc.
     */
    @Column(nullable = false)
    private String subject;

    /**
     * Typ des Lehrers, der die Zielgruppe und Berechtigungen bestimmt.
     *
     * - EDUCATOR: Für Grundschulklassen (0-4), kann alle Fächer unterrichten
     * - TEACHER: Für höhere Klassen (5-12), spezialisiert auf bestimmte Fächer
     */
    @Enumerated(EnumType.STRING)
    private TeacherType type;

    /**
     * Klasse, für die dieser Lehrer als Klassenlehrer fungiert.
     *
     * Diese One-to-One-Beziehung ist optional, da nicht jeder Lehrer
     * eine Klassenleitung haben muss. JsonBackReference verhindert
     * zirkuläre Referenzen bei der JSON-Serialisierung.
     */
    @JsonBackReference
    @OneToOne(mappedBy = "classTeacher")
    @ToString.Exclude
    private Class classAsTeacher;
}