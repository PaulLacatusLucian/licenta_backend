package com.cafeteria.cafeteria_plugin.models;

/**
 * Enumeration für die verschiedenen Bildungsebenen im Schulsystem.
 *
 * Diese Enumeration definiert die drei Hauptebenen der Schulbildung
 * im rumänischen Bildungssystem und bestimmt wichtige Geschäftsregeln
 * für Klassenverwaltung, Lehrerzuordnung und Spezialisierungen.
 *
 * Bildungsebenen und ihre Charakteristika:
 * - PRIMARY: Grundschule mit Erziehern als Hauptlehrer
 * - MIDDLE: Mittelschule mit Fachlehrern
 * - HIGH: Oberschule mit Spezialisierungen und Fachlehrern
 *
 * Geschäftsregeln:
 * - PRIMARY-Klassen benötigen EDUCATOR-Typ Lehrer
 * - MIDDLE/HIGH-Klassen benötigen TEACHER-Typ Lehrer
 * - Nur HIGH-Klassen können Spezialisierungen haben
 *
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