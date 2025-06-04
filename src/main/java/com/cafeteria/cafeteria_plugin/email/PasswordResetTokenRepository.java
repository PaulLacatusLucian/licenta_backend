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
 * <p>
 * Hauptfunktionalitäten:
 * - Token-basierte Suche für Validierungsoperationen
 * - Benutzer-spezifische Bereinigung veralteter Tokens
 * - Standard-CRUD-Operationen durch JpaRepository-Vererbung
 * - Optimierte Datenbankabfragen für sicherheitskritische Operationen
 * - Typsichere Optional-basierte Rückgabewerte für robuste Fehlerbehandlung
 * <p>
 * Sicherheitsaspekte:
 * - Sichere Token-Suche ohne SQL-Injection-Risiken
 * - Atomare Löschoperationen für Datenintegrität
 * - Optional-basierte Rückgaben für Null-Safety
 * - Effiziente Indizierung für performante Token-Lookups
 * - Transaktionale Sicherheit durch Spring Data JPA
 * <p>
 * Performance-Optimierungen:
 * - Query-by-Example für optimierte Datenbankabfragen
 * - Lazy Loading für Benutzer-Beziehungen
 * - Batch-Operationen für Bulk-Löschungen
 * - Connection-Pooling durch JPA-Integration
 * <p>
 * Integration:
 * - Zentrale Verwendung in PasswordResetService
 * - Unterstützung für PasswordResetController-Workflows
 * - Spring Data JPA Auto-Configuration
 * - Transaktionsmanagement durch @Transactional-Annotation
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see PasswordResetToken
 * @see PasswordResetService
 * @see JpaRepository
 * @since 2025-01-01
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Sucht einen Passwort-Reset-Token anhand seines eindeutigen Token-Strings.
     * <p>
     * Diese Methode führt eine sichere Datenbankabfrage durch, um einen
     * spezifischen Token basierend auf seinem String-Wert zu finden.
     * Sie ist die zentrale Methode für die Token-Validierung und wird
     * in allen sicherheitskritischen Authentifizierungsoperationen verwendet.
     * <p>
     * Query-Eigenschaften:
     * - WHERE-Klausel auf Token-String für exakte Übereinstimmung
     * - Case-sensitive Suche für maximale Sicherheit
     * - Automatische SQL-Injection-Schutz durch JPA Parameter Binding
     * - Optimierte Abfrage durch Unique-Index auf Token-Spalte
     * <p>
     * Rückgabeverhalten:
     * - Optional.of(token) bei gefundenem Token
     * - Optional.empty() wenn kein Token mit diesem String existiert
     * - Null-Safety durch Optional-Pattern
     * - Thread-sichere Operationen durch JPA-Transaktionsmanagement
     * <p>
     * Verwendung:
     * - Token-Validierung in PasswordResetService.validateToken()
     * - Sicherheitsprüfungen vor Passwort-Updates
     * - Controller-Validierung für Web-Formulare
     * - Authentifizierungs-Workflows im Schulsystem
     * <p>
     * Performance:
     * - O(1) Lookup durch Unique-Index auf Token-Spalte
     * - Minimale Netzwerk-Roundtrips durch effiziente Query
     * - Connection-Pooling für skalierbare Performance
     * - Query-Caching durch Second-Level-Cache
     *
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
     * Löschstrategie:
     * - Batch-DELETE-Operation für alle Tokens eines Benutzers
     * - Kaskadierte Löschung durch JPA-Relationship-Management
     * - Atomare Transaktion für Datenintegrität
     * - Automatische Freigabe von Datenbankressourcen
     * <p>
     * Sicherheitsaspekte:
     * - Vollständige Bereinigung aller Benutzer-Tokens
     * - Verhindert Accumulation veralteter Tokens
     * - Schutz vor Token-basierten Angriffen nach Kontowechsel
     * - Audit-Trail durch Transaktionsprotokollierung
     * <p>
     * Anwendungsszenarien:
     * - Benutzerabmeldung mit Token-Invalidierung
     * - Passwort-Änderung mit Sicherheitsbereinigung
     * - Kontosperrung mit sofortiger Token-Deaktivierung
     * - Regelmäßige Systemwartung und Token-Hygiene
     * - Sicherheitsbreach-Response mit vollständiger Bereinigung
     * <p>
     * Performance:
     * - Batch-Operation für effiziente Mehrfach-Löschung
     * - Index-optimierte WHERE-Klausel auf user_id
     * - Minimale Datenbank-Roundtrips durch Bulk-Delete
     * - Transaktionale Gruppierung für Konsistenz
     *
     * @param userId ID des Benutzers, dessen alle Reset-Tokens gelöscht werden sollen
     */
    void deleteAllByUser_Id(Long userId);

}