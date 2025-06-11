package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository-Interface für unterrichtsstundenspezifische Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see ClassSession
 * @see JpaRepository
 * @since 2025-01-18
 */
@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    /**
     * Sucht alle Unterrichtsstunden für einen bestimmten Lehrer.
     * <p>
     * Diese Methode wird für Lehrerübersichten und -dashboards verwendet.
     * Sie ermöglicht es Lehrern, alle ihre Unterrichtsstunden zu sehen
     * und darauf basierend Noten und Fehlzeiten zu verwalten.
     * <p>
     * Verwendung:
     * - Lehrer-Dashboard und Stundenübersicht
     * - Notenerfassung und Fehlzeitenverwaltung
     * - Unterrichtsplanung und -vorbereitung
     *
     * @param teacherId Eindeutige ID des Lehrers
     * @return Liste aller Unterrichtsstunden des Lehrers, chronologisch sortiert
     */
    List<ClassSession> findByTeacherId(Long teacherId);

    /**
     * Sucht alle Unterrichtsstunden für ein bestimmtes Fach.
     * <p>
     * Diese Methode ermöglicht fachspezifische Auswertungen und Statistiken.
     * Sie wird für Fachbereichsleiter und administrative Übersichten verwendet.
     * <p>
     * Verwendung:
     * - Fachspezifische Berichte und Statistiken
     * - Unterrichtsverteilung und -planung
     * - Qualitätssicherung und Fachbereichsmanagement
     *
     * @param subject Name des Unterrichtsfachs
     * @return Liste aller Unterrichtsstunden des Fachs, nach Datum sortiert
     */
    List<ClassSession> findBySubject(String subject);

    /**
     * Sucht alle Unterrichtsstunden in einem bestimmten Zeitraum.
     * <p>
     * Diese Methode wird für Kalender-Ansichten, Berichte und Statistiken
     * verwendet. Sie ermöglicht zeitbasierte Analysen und Planungen.
     * <p>
     * Verwendung:
     * - Wochen- und Monatskalender
     * - Zeitraumbasierte Berichte
     * - Unterrichtsstatistiken und -auslastung
     * - Vertretungsplanung
     *
     * @param start Startzeit des Suchzeitraums (inklusive)
     * @param end   Endzeit des Suchzeitraums (inklusive)
     * @return Liste aller Unterrichtsstunden im Zeitraum, chronologisch sortiert
     */
    List<ClassSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Alternative Methode zur Suche von Unterrichtsstunden für einen Lehrer.
     * <p>
     * Diese Methode verwendet explizite JPA-Pfadnavigation über die Teacher-Entität
     * und kann in bestimmten Fällen bessere Performance bieten als die direkte
     * Suche über teacherId.
     * <p>
     * Technische Details:
     * - Verwendet JPA-Pfadnavigation (teacher.id)
     * - Kann bessere Query-Optimierung ermöglichen
     * - Redundant zu findByTeacherId, aber technisch unterschiedlich
     *
     * @param teacherId Eindeutige ID des Lehrers
     * @return Liste aller Unterrichtsstunden des Lehrers
     */
    List<ClassSession> findByTeacher_Id(Long teacherId);
}