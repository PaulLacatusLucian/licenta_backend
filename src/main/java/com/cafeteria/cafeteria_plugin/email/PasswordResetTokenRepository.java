package com.cafeteria.cafeteria_plugin.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository-Interface für Passwort-Reset-Token-Datenzugriff und Persistierung.
 * <p>
 * Dieses Spring Data JPA Repository-Interface bietet spezialisierte
 * Datenzugriffsmethoden für die Verwaltung von Passwort-Reset-Tokens
 * in der Datenbank. Es erweitert das Standard-JpaRepository um
 * sicherheitsspezifische Query-Methoden für die Token-Authentifizierung
 * und Lifecycle-Verwaltung.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see PasswordResetToken
 * @see PasswordResetService
 * @see JpaRepository
 * @since 2025-03-13
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Sucht einen Passwort-Reset-Token anhand seines eindeutigen Token-Strings.
     * <p>
     * Diese Methode führt eine sichere Datenbankabfrage durch, um einen
     * spezifischen Token basierend auf seinem String-Wert zu finden.
     * Sie ist die zentrale Methode für die Token-Validierung und wird
     * in allen sicherheitskritischen Authentifizierungsoperationen verwendet.
     * @param token Eindeutiger Token-String zur Suche
     * @return Optional mit PasswordResetToken falls gefunden, sonst Optional.empty()
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Löscht alle Passwort-Reset-Tokens für einen spezifischen Benutzer.
     * <p>
     * Diese Bereinigungsmethode entfernt alle bestehenden Reset-Tokens
     * eines Benutzers aus der Datenbank. Sie wird typischerweise verwendet,
     * um veraltete oder kompromittierte Tokens zu entfernen und die
     * Sicherheit bei Benutzerkontoänderungen zu gewährleisten.
     * <p>
     * @param userId ID des Benutzers, dessen alle Reset-Tokens gelöscht werden sollen
     */
    void deleteAllByUser_Id(Long userId);

}