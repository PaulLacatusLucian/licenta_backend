package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository-Interface für klassenspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für Schulklassen bereit. Klassen sind zentrale Entitäten
 * im Schulsystem und verbinden Schüler, Lehrer, Stundenpläne und Kataloge.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Schulklassen
 * - Lehrerspezifische Klassensuche
 * - Namensbasierte Klassensuche
 * - Klassenverwaltung und -organisation
 * - Jahresübergang und Klassenarchivierung
 * <p>
 * Besondere Merkmale:
 * - Ein-zu-Eins-Beziehung zwischen Klassenlehrer und Klasse
 * - Eindeutige Klassennamen pro Schuljahr
 * - Optimierte Abfragen für häufige Zugriffe
 * - Unterstützung für Klassenlehrer-Verwaltung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Class
 * @see Teacher
 * @see Student
 * @see Schedule
 * @see JpaRepository
 * @since 2025-01-01
 */
public interface ClassRepository extends JpaRepository<Class, Long> {

    /**
     * Sucht eine Klasse anhand der Klassenlehrer-ID.
     * <p>
     * Diese Methode implementiert die Geschäftslogik, dass jeder Klassenlehrer
     * maximal eine Klasse betreuen kann. Sie wird verwendet, um die Klasse
     * eines bestimmten Lehrers zu finden oder zu prüfen, ob ein Lehrer
     * bereits eine Klasse zugeteilt hat.
     * <p>
     * Verwendung:
     * - Klassenlehrerzuteilung validieren
     * - Lehrerübersichten und -dashboards
     * - Klassenorganisation und -verwaltung
     *
     * @param classTeacherId Eindeutige ID des Klassenlehrers
     * @return Optional mit der Klasse falls vorhanden, leer falls der Lehrer keine Klasse hat
     */
    Optional<Class> findByClassTeacherId(Long classTeacherId);

    /**
     * Sucht eine Klasse anhand des Klassennamens.
     * <p>
     * Diese Methode wird für die Klassensuche und -validierung verwendet.
     * Klassennamen sind eindeutig pro Schuljahr und folgen standardisierten
     * Benennungskonventionen (z.B. "10A", "9B", etc.).
     * <p>
     * Verwendung:
     * - Jahresübergang und automatische Klassenerstellung
     * - Klassennamen-Eindeutigkeitsprüfung
     * - Import und Migration von Klassendaten
     * - Manuelle Klassensuche in der Verwaltung
     *
     * @param newClassName Der Name der gesuchten Klasse
     * @return Optional mit der Klasse falls vorhanden, leer falls nicht existiert
     */
    Optional<Class> findByName(String newClassName);
}