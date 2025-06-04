package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository-Interface für stundenplanspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für Stundenpläne bereit. Schedules definieren die
 * regelmäßigen Unterrichtszeiten und verbinden Klassen, Lehrer und Fächer
 * in einem strukturierten Wochenschema.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Stundenpläne
 * - Klassenspezifische Stundenplan-Abfragen
 * - Lehrerspezifische Unterrichtsübersichten
 * - Namensbasierte Klassensuche
 * - Effiziente Join-Operationen für UI-Performance
 * <p>
 * Besondere Merkmale:
 * - Optimierte Queries mit Eager Loading für Teacher-Daten
 * - Flexible Abfragemöglichkeiten über ID und Namen
 * - Unterstützung für Stundenplan-Generierung
 * - Integration mit ClassSession-System
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Schedule
 * @see Class
 * @see Teacher
 * @see ClassSession
 * @see JpaRepository
 * @since 2025-01-01
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * Sucht alle Stundenpläne für eine bestimmte Klasse mit Lehrer-Informationen.
     * <p>
     * Diese Query lädt explizit die Lehrer-Daten mit, um N+1-Query-Probleme
     * zu vermeiden. Sie wird für die Darstellung kompletter Stundenpläne
     * in der Benutzeroberfläche verwendet.
     * <p>
     * Technische Details:
     * - Verwendet explizite SELECT für bessere Performance
     * - Lädt Teacher-Entität eager mit
     * - Optimiert für UI-Rendering
     * <p>
     * Verwendung:
     * - Stundenplan-Anzeige für Klassen
     * - Lehrer-Zuordnungsübersichten
     * - Wochenplan-Generierung
     * - Unterrichtsplanung und -organisation
     *
     * @param classId Eindeutige ID der Schulklasse
     * @return Liste aller Stundenpläne der Klasse mit vollständigen Lehrer-Daten
     */
    @Query("SELECT s FROM Schedule s WHERE s.studentClass.id = :classId")
    List<Schedule> findAllByClassIdWithTeacher(Long classId);

    /**
     * Sucht alle Stundenpläne für einen bestimmten Lehrer.
     * <p>
     * Diese Methode ermöglicht es Lehrern, ihre komplette Unterrichtsbelastung
     * zu sehen. Sie wird für Lehrer-Dashboards und Stundenplan-Übersichten
     * verwendet.
     * <p>
     * Verwendung:
     * - Lehrer-Dashboard und Wochenübersicht
     * - Unterrichtsbelastung-Analysen
     * - Vertretungsplanung
     * - Arbeitszeit-Dokumentation
     *
     * @param teacherId Eindeutige ID des Lehrers
     * @return Liste aller Stundenpläne des Lehrers
     */
    List<Schedule> findByTeacherId(Long teacherId);

    /**
     * Sucht alle Stundenpläne für eine Klasse anhand des Klassennamens.
     * <p>
     * Diese Query navigiert über die Class-Entität und ermöglicht
     * namensbasierte Stundenplan-Abfragen. Sie wird für flexible
     * Suchfunktionen und API-Endpunkte verwendet.
     * <p>
     * Technische Details:
     * - JOIN über studentClass-Beziehung
     * - Navigation über Class.name Attribut
     * - Lädt Teacher-Daten mit für Vollständigkeit
     * <p>
     * Verwendung:
     * - API-Endpunkte mit Klassennamen-Parameter
     * - Flexible Suchfunktionen
     * - Integration mit externen Systemen
     * - Benutzerfreundliche Abfragen
     *
     * @param className Name der Schulklasse (z.B. "10A", "9B")
     * @return Liste aller Stundenpläne der benannten Klasse mit Lehrer-Daten
     */
    @Query("SELECT s FROM Schedule s JOIN s.studentClass c WHERE c.name = :className")
    List<Schedule> findAllByClassNameWithTeacher(@Param("className") String className);

    /**
     * Einfache Suche aller Stundenpläne für eine Klasse anhand der ID.
     * <p>
     * Diese Methode bietet eine alternative, einfachere Abfrage ohne
     * explizite Query-Definition. Sie kann in bestimmten Fällen
     * bessere Performance bieten als die @Query-Variante.
     * <p>
     * Verwendung:
     * - Einfache Stundenplan-Abfragen
     * - Performance-kritische Operationen
     * - Basis-CRUD-Operationen
     * - Interne Service-Methoden
     *
     * @param classId Eindeutige ID der Schulklasse
     * @return Liste aller Stundenpläne der Klasse
     */
    List<Schedule> findByStudentClassId(Long classId);
}