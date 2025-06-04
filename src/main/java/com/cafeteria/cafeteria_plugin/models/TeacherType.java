package com.cafeteria.cafeteria_plugin.models;

import com.cafeteria.cafeteria_plugin.models.EducationLevel;
import com.cafeteria.cafeteria_plugin.models.Teacher;

/**
 * Enumeration für die verschiedenen Lehrertypen im Schulsystem.
 *
 * Diese Enumeration unterscheidet zwischen den zwei Haupttypen
 * von pädagogischem Personal und bestimmt ihre Zuständigkeiten
 * und Berechtigungen im System.
 *
 * Die Typen entsprechen den verschiedenen Bildungsebenen:
 * - EDUCATOR für Grundschulbildung
 * - TEACHER für weiterführende Bildung
 *
 * Geschäftsregeln:
 * - EDUCATOR kann nur PRIMARY-Klassen leiten
 * - TEACHER kann nur MIDDLE/HIGH-Klassen leiten
 * - Bestimmt verfügbare Funktionen und Berechtigungen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Teacher
 * @see EducationLevel
 * @since 2025-04-01
 */
public enum TeacherType {
    /**
     * Erzieher für Grundschulklassen (0–4).
     *
     * Charakteristika:
     * - Unterrichtet alle Fächer in einer Klasse
     * - Fungiert als Hauptbezugsperson für Schüler
     * - Spezialisiert auf altersgerechte Pädagogik
     * - Kann nur PRIMARY-Klassen zugewiesen werden
     * - Hat oft zusätzliche sozialpädagogische Ausbildung
     */
    EDUCATOR,

    /**
     * Fachlehrer für Mittel- und Oberschulklassen (5–12).
     *
     * Charakteristika:
     * - Spezialisiert auf ein oder wenige Fächer
     * - Unterrichtet mehrere Klassen verschiedener Stufen
     * - Kann als Klassenlehrer für MIDDLE/HIGH-Klassen fungieren
     * - Fachspezifische Qualifikationen und Expertise
     * - Kann nicht PRIMARY-Klassen zugewiesen werden
     */
    TEACHER
}