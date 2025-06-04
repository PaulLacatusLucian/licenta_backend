package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Interface für semesterspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für Semester-Verwaltung bereit. Das Semester-System
 * verwaltet den aktuellen Schulzeitraum und unterstützt akademische
 * Zyklen und Berichtszeiträume.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Semester
 * - Singleton-Pattern für aktuelles Semester
 * - Semesterübergang und -verwaltung
 * - Akademische Zeitraum-Verfolgung
 * - Einfache Konfigurationsverwaltung
 * <p>
 * Besondere Merkmale:
 * - Singleton-Design mit einzelnem Semester-Datensatz
 * - Minimalistisches Schema für einfache Verwaltung
 * - Zentrale Kontrolle über akademische Zeiträume
 * - Unterstützung für automatische Semesterübergänge
 * - Integration mit Bewertungs- und Berichtssystemen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Semester
 * @see SemesterService
 * @see Grade
 * @see JpaRepository
 * @since 2025-01-01
 */
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    /*
     * Hinweis: Dieses Repository verwendet nur die Standard-JpaRepository-Methoden.
     *
     * Das Semester-System folgt einem Singleton-Pattern:
     * - Es existiert nur ein Semester-Datensatz in der Datenbank
     * - Dieser wird über die ID 1L abgerufen und aktualisiert
     * - Die Geschäftslogik liegt im SemesterService
     *
     * Verfügbare Standard-Operationen:
     * - findById(1L) - Aktuelles Semester abrufen
     * - save(Semester) - Semester aktualisieren
     * - Der Service verwendet diese Methoden für:
     *   - getCurrentSemester() - Laden des aktuellen Semesters
     *   - incrementSemester() - Übergang zum nächsten Semester
     *
     * Erweiterte Funktionen könnten bei Bedarf hinzugefügt werden:
     * - Historische Semester-Verfolgung
     * - Mehrfach-Semester-Support
     * - Semester-Archivierung
     * - Datum-basierte Semester-Ermittlung
     */
}