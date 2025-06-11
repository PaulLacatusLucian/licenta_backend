package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.CatalogEntry;
import com.cafeteria.cafeteria_plugin.models.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository-Interface für katalogeintragspeziﬁsche Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see CatalogEntry
 * @see EntryType
 * @see JpaRepository
 * @since 2025-03-08
 */
public interface CatalogEntryRepository extends JpaRepository<CatalogEntry, Long> {

    /**
     * Sucht alle Katalogeinträge für einen bestimmten Katalog.
     * <p>
     * Diese Methode wird für die Darstellung des kompletten Klassenbuchs
     * einer Klasse verwendet. Sie lädt alle Einträge (Noten und Fehlzeiten)
     * für die gesamte Klasse.
     *
     * @param catalogId Eindeutige ID des Katalogs
     * @return Liste aller Einträge im Katalog, sortiert nach Datum
     */
    List<CatalogEntry> findByCatalog_Id(Long catalogId);

    /**
     * Sucht alle Katalogeinträge für einen bestimmten Schüler.
     * <p>
     * Diese Methode wird für schülerspezifische Übersichten verwendet,
     * sowohl für Eltern-Self-Service als auch für Lehrertools.
     * Sie lädt alle Einträge eines Schülers klassenübergreifend.
     *
     * @param studentId Eindeutige ID des Schülers
     * @return Liste aller Einträge des Schülers, chronologisch sortiert
     */
    List<CatalogEntry> findByStudent_Id(Long studentId);

    /**
     * Sucht Katalogeinträge für einen bestimmten Katalog und Eintragstyp.
     * <p>
     * Diese Methode ermöglicht gefilterte Ansichten des Klassenbuchs,
     * z.B. nur Noten oder nur Fehlzeiten einer Klasse anzuzeigen.
     * Wird für spezialisierte Berichte und Statistiken verwendet.
     *
     * @param catalogId Eindeutige ID des Katalogs
     * @param type Typ des Eintrags (GRADE für Noten, ABSENCE für Fehlzeiten)
     * @return Liste der gefilterten Katalogeinträge
     */
    List<CatalogEntry> findByCatalog_IdAndType(Long catalogId, EntryType type);

    /**
     * Sucht Katalogeinträge für einen bestimmten Schüler und Eintragstyp.
     * <p>
     * Diese Methode ermöglicht es, nur bestimmte Arten von Einträgen
     * für einen Schüler zu laden. Wird für fokussierte Ansichten und
     * spezielle Auswertungen verwendet.
     *
     * @param studentId Eindeutige ID des Schülers
     * @param type Typ des Eintrags (GRADE für Noten, ABSENCE für Fehlzeiten)
     * @return Liste der gefilterten Einträge des Schülers
     */
    List<CatalogEntry> findByStudent_IdAndType(Long studentId, EntryType type);

    Optional<CatalogEntry> findByAbsenceId(Long absenceId);

}