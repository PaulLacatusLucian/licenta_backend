package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository-Interface für lehrerspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für Lehrer bereit. Lehrer sind zentrale Akteure im
 * Schulsystem und unterrichten verschiedene Fächer oder führen Klassen
 * als Klassenbetreuer.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Lehrer
 * - Namens- und fachbasierte Lehrersuche
 * - Klassenlehrer-spezifische Abfragen
 * - Benutzername-basierte Authentifizierung
 * - Typ-spezifische Lehrer-Filterung (Educator vs. Teacher)
 * <p>
 * Besondere Merkmale:
 * - Unterscheidung zwischen Educators (Primarstufe) und Teachers (Sekundarstufe)
 * - Komplexe Join-Queries für Klassenlehrer-Ermittlung
 * - Optimierte Abfragen für häufige Zugriffsmuster
 * - Unterstützung für verschiedene Lehrertypen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Teacher
 * @see TeacherType
 * @see Class
 * @see Subject
 * @see Schedule
 * @see JpaRepository
 * @since 2025-01-01
 */
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * Sucht einen Lehrer anhand seines vollständigen Namens.
     * <p>
     * Diese Methode ermöglicht die Identifikation von Lehrern über
     * ihren Klarnamen. Sie wird für Suchfunktionen und administrative
     * Zwecke verwendet.
     * <p>
     * Verwendung:
     * - Administrative Lehrersuche
     * - Import und Datenabgleich
     * - Benutzerfreundliche Suchfunktionen
     * - Validierung bei Dateneingabe
     *
     * @param name Vollständiger Name des Lehrers
     * @return Optional mit dem Lehrer falls vorhanden, leer falls nicht gefunden
     */
    Optional<Teacher> findByName(String name);

    /**
     * Sucht alle Lehrer, die ein bestimmtes Fach unterrichten.
     * <p>
     * Diese Methode ermöglicht fachspezifische Lehrersuche und wird
     * für Fachbereichsverwaltung und Unterrichtsplanung verwendet.
     * <p>
     * Verwendung:
     * - Fachbereichsleiter-Übersichten
     * - Vertretungsplanung nach Fach
     * - Qualifikations-Management
     * - Unterrichtsverteilung
     *
     * @param subject Name des Unterrichtsfachs
     * @return Liste aller Lehrer, die das angegebene Fach unterrichten
     */
    List<Teacher> findBySubject(String subject);

    /**
     * Sucht den Educator (Klassenlehrer) für eine bestimmte Primarklasse.
     * <p>
     * Diese spezialisierte Query findet den Educator für Primarklassen.
     * Educators sind speziell ausgebildete Lehrer für die Klassen 0-4
     * und haben andere Qualifikationen als reguläre Fachlehrer.
     * <p>
     * Technische Details:
     * - JOIN über classAsTeacher-Beziehung
     * - Filter auf TeacherType.EDUCATOR
     * - Spezifisch für Primarstufen-Verwaltung
     * <p>
     * Verwendung:
     * - Primarklassen-Verwaltung
     * - Educator-spezifische Funktionen
     * - Klassenbetreuer-Ermittlung für Grundschule
     * - Spezialisierte Lehrertyp-Zuordnung
     *
     * @param classId Eindeutige ID der Primarklasse
     * @return Der Educator der angegebenen Klasse oder null falls nicht zugeordnet
     */
    @Query("SELECT t FROM Teacher t WHERE t.classAsTeacher.id = :classId AND t.type = 'EDUCATOR'")
    Teacher findEducatorByClassId(@Param("classId") Long classId);

    /**
     * Sucht einen Lehrer anhand seines Benutzernamens.
     * <p>
     * Diese Methode wird für JWT-basierte Authentifizierung verwendet,
     * um den Lehrer-Kontext aus dem Token zu laden. Sie ist essentiell
     * für alle lehrerspezifischen Funktionen.
     * <p>
     * Verwendung:
     * - JWT-Token-Validierung und Kontextladung
     * - Session-Management für Lehrer
     * - Lehrer-Dashboard-Zugriff
     * - Autorisierungsprüfungen
     * - Unterrichtsstunden-Verwaltung
     *
     * @param username Eindeutiger Benutzername des Lehrers
     * @return Der gefundene Lehrer oder null falls nicht vorhanden
     */
    Teacher findByUsername(String username);
}