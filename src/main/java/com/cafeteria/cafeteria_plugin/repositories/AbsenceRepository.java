package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository-Interface für fehlzeitenspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für Schülerfehlzeiten bereit. Sie enthält custom Queries
 * für komplexe Abfragen und Statistiken über Anwesenheit und Fehlzeiten.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Fehlzeiten
 * - Schülerspezifische Fehlzeitensuche
 * - Fehlzeitenstatistiken und -zählung
 * - Unterrichtsstunden-Fehlzeiten-Validierung
 * - Duplikatsprüfung für Fehlzeiten
 * <p>
 * Besondere Merkmale:
 * - Custom Queries für Aggregationsoperationen
 * - Effiziente Zähloperationen ohne Vollständiges Laden
 * - Existenzprüfungen für Geschäftslogik-Validierung
 * - Optimierte Abfragen für Statistiken
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Absence
 * @see Student
 * @see ClassSession
 * @see JpaRepository
 * @since 2025-01-01
 */
@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    /**
     * Zählt alle Fehlzeiten für einen bestimmten Schüler.
     * <p>
     * Diese Query wird für Statistiken und Übersichten verwendet.
     * Sie zählt effizient ohne die Notwendigkeit, alle Datensätze zu laden.
     * Wird hauptsächlich für Eltern- und Schüler-Dashboards verwendet.
     *
     * @param studentId Eindeutige ID des Schülers
     * @return Gesamtanzahl der Fehlzeiten des Schülers
     */
    @Query("SELECT COUNT(a) FROM Absence a WHERE a.student.id = :studentId")
    int countByStudentId(@Param("studentId") Long studentId);

    /**
     * Sucht alle Fehlzeiten für einen bestimmten Schüler.
     * <p>
     * Diese Methode lädt alle Fehlzeiten eines Schülers für detaillierte
     * Ansichten und Berichte. Wird für Eltern-Self-Service und
     * Lehrerübersichten verwendet.
     *
     * @param studentId Eindeutige ID des Schülers
     * @return Liste aller Fehlzeiten des Schülers, chronologisch sortiert
     */
    List<Absence> findByStudentId(Long studentId);

    /**
     * Überprüft die Existenz einer Fehlzeit für einen Schüler in einer bestimmten Unterrichtsstunde.
     * <p>
     * Diese Methode wird für Geschäftslogik-Validierung verwendet,
     * um Duplikate zu vermeiden. Ein Schüler kann nur eine Fehlzeit
     * pro Unterrichtsstunde haben.
     *
     * @param studentId      Eindeutige ID des Schülers
     * @param classSessionId Eindeutige ID der Unterrichtsstunde
     * @return true wenn bereits eine Fehlzeit existiert, false andernfalls
     */
    boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId);

    @Query("SELECT a FROM Absence a WHERE a.student.id IN :studentIds AND (a.justified = false OR a.justified IS NULL)")
    List<Absence> findUnjustifiedAbsencesByStudentIds(@Param("studentIds") List<Long> studentIds);
}
