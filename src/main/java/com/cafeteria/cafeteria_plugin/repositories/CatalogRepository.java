package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository-Interface für katalogspezifische Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see Catalog
 * @see Class
 * @see JpaRepository
 * @since 2025-01-31
 */
public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    /**
     * Sucht einen Katalog für eine bestimmte Klasse.
     * <p>
     * Diese Methode implementiert die Geschäftslogik, dass jede Klasse
     * genau einen Katalog hat. Sie wird verwendet, um das digitale
     * Klassenbuch einer Klasse zu laden oder dessen Existenz zu prüfen.
     * <p>
     * Verwendung in verschiedenen Kontexten:
     * - Lehrer: Zugriff auf Klassenbuch für Noteneintragung
     * - Eltern: Ansicht der Noten und Fehlzeiten ihres Kindes
     * - Verwaltung: Katalogverwaltung und -archivierung
     *
     * @param classId Eindeutige ID der Schulklasse
     * @return Optional mit dem Katalog falls vorhanden, leer falls nicht existiert
     */
    Optional<Catalog> findByStudentClass_Id(Long classId);
}