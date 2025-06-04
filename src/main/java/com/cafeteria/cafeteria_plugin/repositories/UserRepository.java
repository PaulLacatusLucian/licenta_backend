package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository-Interface für grundlegende Benutzeroperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt CRUD-Operationen
 * für die abstrakte User-Klasse bereit. Sie dient als Basis für alle
 * benutzerbezogenen Datenbankoperationen im Schulverwaltungssystem.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen (Create, Read, Update, Delete)
 * - Benutzername-basierte Suche für Authentifizierung
 * - Email-basierte Suche für Passwort-Reset
 * - Eindeutigkeitsprüfungen für Benutzername und Email
 * <p>
 * Verwendung:
 * - Authentifizierung und Login-Prozesse
 * - Benutzerregistrierung mit Duplikatsprüfung
 * - Passwort-Reset-Funktionalitäten
 * - Administrative Benutzerverwaltung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see JpaRepository
 * @since 2025-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Sucht einen Benutzer anhand seines eindeutigen Benutzernamens.
     * <p>
     * Diese Methode wird hauptsächlich für Authentifizierungsprozesse verwendet,
     * da der Benutzername das primäre Anmeldekennzeichen ist.
     *
     * @param username Der eindeutige Benutzername
     * @return Optional mit dem gefundenen Benutzer oder leer falls nicht vorhanden
     */
    Optional<User> findByUsername(String username);

    /**
     * Sucht einen Benutzer anhand seiner Email-Adresse.
     * <p>
     * Diese Methode wird für Passwort-Reset-Funktionalitäten und
     * Email-basierte Benutzersuche verwendet.
     *
     * @param email Die eindeutige Email-Adresse
     * @return Optional mit dem gefundenen Benutzer oder leer falls nicht vorhanden
     */
    Optional<Object> findByEmail(String email);

    /**
     * Überprüft, ob ein Benutzername bereits im System existiert.
     * <p>
     * Diese Methode wird bei der Benutzerregistrierung verwendet,
     * um Duplikate zu vermeiden und die Eindeutigkeit sicherzustellen.
     *
     * @param username Der zu überprüfende Benutzername
     * @return true wenn der Benutzername bereits existiert, false andernfalls
     */
    boolean existsByUsername(String username);

    /**
     * Überprüft, ob eine Email-Adresse bereits im System existiert.
     * <p>
     * Diese Methode wird bei der Benutzerregistrierung verwendet,
     * um zu gewährleisten, dass jede Email-Adresse nur einmal verwendet wird.
     *
     * @param email Die zu überprüfende Email-Adresse
     * @return true wenn die Email bereits existiert, false andernfalls
     */
    boolean existsByEmail(String email);
}