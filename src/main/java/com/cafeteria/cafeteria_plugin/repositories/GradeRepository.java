package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository-Interface für notenspezifische Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see Grade
 * @see JpaRepository
 * @since 2024-12-18
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    /**
     * Löscht alle Noten für einen bestimmten Schüler.
     * <p>
     * Diese Methode wird für Cascading-Delete-Operationen verwendet,
     * wenn ein Schüler aus dem System entfernt wird. Sie stellt sicher,
     * dass keine verwaisten Notendatensätze zurückbleiben.
     * <p>
     * Verwendung:
     * - Schüler-Löschung mit vollständiger Datenbereinigung
     * - Jahresübergang und Datenarchivierung
     * - Datenschutz-konforme Löschungen (DSGVO)
     * - Systemwartung und Datenintegrität
     *
     * @param id Eindeutige ID des Schülers, dessen Noten gelöscht werden sollen
     */
    void deleteByStudentId(Long id);

    /**
     * Sucht alle Noten für einen bestimmten Schüler.
     * <p>
     * Diese Methode wird für Schüler- und Elternübersichten verwendet.
     * Sie lädt alle Noten eines Schülers für Berichte, Zeugnisse und
     * Self-Service-Portale.
     * <p>
     * Verwendung:
     * - Schüler-Dashboard und Notenübersicht
     * - Eltern-Self-Service-Portal
     * - Zeugniserstellung und Berichte
     * - Leistungsanalysen und Statistiken
     *
     * @param studentId Eindeutige ID des Schülers
     * @return Liste aller Noten des Schülers, chronologisch sortiert
     */
    List<Grade> findByStudentId(Long studentId);

    /**
     * Überprüft die Existenz einer Note für einen Schüler in einer bestimmten Unterrichtsstunde.
     * <p>
     * Diese Methode wird für Geschäftslogik-Validierung verwendet.
     * Sie verhindert Duplikate und stellt sicher, dass die Regel
     * "ein Schüler kann nur eine Note pro Unterrichtsstunde haben" eingehalten wird.
     * <p>
     * Geschäftsregeln:
     * - Nur eine Note pro Schüler und Unterrichtsstunde
     * - Schüler mit Note können nicht als fehlend markiert werden
     * - Fehlende Schüler können keine Note erhalten
     * <p>
     * Verwendung:
     * - Notenerfassung mit Duplikatsprüfung
     * - Fehlzeiten-Noten-Konfliktprüfung
     * - UI-Validierung in Echzeit
     * - Batch-Import-Validierung
     *
     * @param studentId      Eindeutige ID des Schülers
     * @param classSessionId Eindeutige ID der Unterrichtsstunde
     * @return true wenn bereits eine Note existiert, false andernfalls
     */
    boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId);
}