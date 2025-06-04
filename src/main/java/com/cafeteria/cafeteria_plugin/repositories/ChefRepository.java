package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Chef;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Interface für küchenpersonalspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für Küchenpersonal (Köche) bereit. Köche sind für die
 * Verwaltung des Cafeteria-Menüs und der Speisenzubereitung verantwortlich.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Küchenpersonal
 * - Eindeutigkeitsprüfungen für Benutzerdaten
 * - Validierung bei Registrierungsprozessen
 * - Sichere Authentifizierung und Autorisierung
 * - Küchenpersonal-Lifecycle-Management
 * <p>
 * Besondere Merkmale:
 * - Eindeutigkeitsprüfung für kritische Felder
 * - Optimierte Existenzprüfungen ohne Volldatensatz-Load
 * - Unterstützung für Bulk-Validierungen
 * - Sichere Benutzerregistrierung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Chef
 * @see User
 * @see MenuItem
 * @see JpaRepository
 * @since 2025-01-01
 */
public interface ChefRepository extends JpaRepository<Chef, Long> {

    /**
     * Überprüft die Existenz eines Küchenpersonals anhand des Benutzernamens.
     * <p>
     * Diese Methode wird für die Validierung bei der Registrierung verwendet,
     * um sicherzustellen, dass Benutzernamen eindeutig sind. Sie ist
     * optimiert für Performance und lädt nicht den gesamten Datensatz.
     * <p>
     * Verwendung:
     * - Registrierungsvalidierung neuer Köche
     * - Benutzername-Eindeutigkeitsprüfung
     * - Präventive Duplikatsvermeidung
     *
     * @param username Der zu prüfende Benutzername
     * @return true wenn ein Koch mit diesem Benutzernamen existiert, false andernfalls
     */
    boolean existsByUsername(String username);

    /**
     * Überprüft die Existenz eines Küchenpersonals anhand der E-Mail-Adresse.
     * <p>
     * Diese Methode wird für die Validierung bei der Registrierung verwendet,
     * um sicherzustellen, dass E-Mail-Adressen eindeutig sind. Sie verhindert
     * Mehrfachregistrierungen mit derselben E-Mail-Adresse.
     * <p>
     * Verwendung:
     * - E-Mail-Eindeutigkeitsprüfung bei Registrierung
     * - Passwort-Reset-Validierung
     * - Kontakt-Informationen-Management
     *
     * @param email Die zu prüfende E-Mail-Adresse
     * @return true wenn ein Koch mit dieser E-Mail-Adresse existiert, false andernfalls
     */
    boolean existsByEmail(String email);
}