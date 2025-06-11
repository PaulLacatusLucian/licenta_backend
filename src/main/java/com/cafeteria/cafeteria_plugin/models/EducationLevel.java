package com.cafeteria.cafeteria_plugin.models;

/**
 * Enumeration für die verschiedenen Bildungsebenen im Schulsystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see Class
 * @see TeacherType
 * @since 2025-04-01
 */
public enum EducationLevel {
    /**
     * Grundschule (Klassen 0–4).
     *
     * Charakteristika:
     * - Ein Erzieher unterrichtet alle Fächer
     * - Keine Spezialisierungen erforderlich
     * - Fokus auf grundlegende Fähigkeiten
     * - Erfordert TeacherType.EDUCATOR
     */
    PRIMARY,

    /**
     * Mittelschule (Klassen 5–8).
     *
     * Charakteristika:
     * - Verschiedene Fachlehrer für unterschiedliche Fächer
     * - Keine Spezialisierungen erforderlich
     * - Übergang zu fachspezifischem Unterricht
     * - Erfordert TeacherType.TEACHER
     */
    MIDDLE,

    /**
     * Oberschule (Klassen 9–12).
     *
     * Charakteristika:
     * - Spezialisierte Fachlehrer
     * - Spezialisierungen sind obligatorisch
     * - Vorbereitung auf Universitätsstudium oder Beruf
     * - Erfordert TeacherType.TEACHER
     */
    HIGH
}