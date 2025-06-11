package com.cafeteria.cafeteria_plugin.models;

import com.cafeteria.cafeteria_plugin.models.CatalogEntry;

/**
 * Enumeration für Katalogeintragtypen im Bewertungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see CatalogEntry
 * @since 2025-05-08
 */
public enum EntryType {
    /**
     * Noteneintrag - Nummerische Bewertung der Schülerleistung.
     * <p>
     * Eigenschaften:
     * - Verwendet das gradeValue-Feld
     * - justified-Feld bleibt null
     * - Erstellung durch Lehrer während oder nach Klassensitzungen
     * - Basis für Leistungsberichte und Zeugnisse
     */
    GRADE,

    /**
     * Abwesenheitseintrag - Registrierung von Schülerabwesenheiten.
     * <p>
     * Eigenschaften:
     * - Verwendet das justified-Feld (entschuldigt/unentschuldigt)
     * - gradeValue-Feld bleibt null
     * - Automatische Erstellung bei Anwesenheitskontrolle
     * - Wichtig für Disziplinarmaßnahmen und Elternkommunikation
     */
    ABSENCE
}
