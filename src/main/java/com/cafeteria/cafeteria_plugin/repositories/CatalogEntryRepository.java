package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.CatalogEntry;
import com.cafeteria.cafeteria_plugin.models.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogEntryRepository extends JpaRepository<CatalogEntry, Long> {
    List<CatalogEntry> findByCatalog_Id(Long catalogId);

    List<CatalogEntry> findByStudent_Id(Long studentId);

    List<CatalogEntry> findByCatalog_IdAndType(Long catalogId, EntryType type);

    List<CatalogEntry> findByStudent_IdAndType(Long studentId, EntryType type);

    Optional<CatalogEntry> findByAbsenceId(Long absenceId);

}