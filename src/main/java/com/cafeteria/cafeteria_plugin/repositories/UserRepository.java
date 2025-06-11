package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository-Interface für grundlegende Benutzeroperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see JpaRepository
 * @since 2024-11-28
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