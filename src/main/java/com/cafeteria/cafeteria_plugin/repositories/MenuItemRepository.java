package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository-Interface für menüartikelspezifische Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see MenuItem
 * @see JpaRepository
 * @since 2025-11-28
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Sucht einen Menüartikel anhand seines Namens.
     * <p>
     * Diese Methode wird für die Artikelsuche und -validierung verwendet.
     * Artikelnamen sind eindeutig im System und dienen als natürlicher
     * Schlüssel für Benutzerinteraktionen.
     * <p>
     * Verwendung:
     * - Bestellverarbeitung und Artikelvalidierung
     * - Menüverwaltung und -aktualisierung
     * - Preis- und Bestandsprüfungen
     * - Import und Synchronisation von Menüdaten
     * - Suchfunktionen im Cafeteria-Portal
     * <p>
     * Geschäftslogik:
     * - Artikelnamen müssen eindeutig sein
     * - Wird für Bestellhistorie-Referenzen verwendet
     * - Ermöglicht benutzerfreundliche Artikelsuche
     *
     * @param name Der Name des gesuchten Menüartikels
     * @return Optional mit dem Menüartikel falls vorhanden, leer falls nicht existiert
     */
    Optional<MenuItem> findByName(String name);
}