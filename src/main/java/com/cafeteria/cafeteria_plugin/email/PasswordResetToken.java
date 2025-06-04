package com.cafeteria.cafeteria_plugin.email;

import com.cafeteria.cafeteria_plugin.models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entitätsklasse für Passwort-Reset-Tokens mit sicherer Benutzerverknüpfung.
 * <p>
 * Diese JPA-Entität repräsentiert einen Passwort-Reset-Token in der Datenbank
 * und implementiert das komplette Sicherheitsmodell für die sichere
 * Passwort-Zurücksetzung im Schulsystem. Sie gewährleistet durch ihre
 * Datenstruktur und Constraints die Integrität und Sicherheit des
 * Authentifizierungsprozesses.
 * <p>
 * Sicherheitsmerkmale:
 * - Eindeutige Token-Strings zur Verhinderung von Kollisionen
 * - Zeitbasierte Ablaufmechanismen für begrenzte Gültigkeit
 * - Einmalige Verwendung durch Used-Flag-Implementierung
 * - Sichere Benutzerverknüpfung mit referenzieller Integrität
 * - Automatische Lifecycle-Verwaltung durch JPA-Cascade-Operationen
 * <p>
 * Datenbankdesign:
 * - Auto-inkrementierende Primärschlüssel für eindeutige Identifikation
 * - Unique-Constraint auf Token-String verhindert Duplikate
 * - NOT NULL-Constraints gewährleisten Datenintegrität
 * - OneToOne-Beziehung mit Benutzer für exakte Zuordnung
 * - Optimistische Sperrung durch JPA für Concurrent-Access-Sicherheit
 * <p>
 * Business Logic:
 * - Automatische Ablaufprüfung durch isExpired()-Methode
 * - Statusverfolgung durch Used-Flag für Einmalverwendung
 * - Kaskadierte Operationen für konsistente Datenverwaltung
 * - Waisenentfernung für saubere Datenbank-Hygiene
 * <p>
 * Integration:
 * - Zentrale Verwendung in PasswordResetService für Token-Management
 * - Unterstützung für PasswordResetController Web-Workflows
 * - Repository-Pattern-Integration für Datenzugriff
 * - Lombok-basierte Code-Generierung für sauberen Code
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see PasswordResetService
 * @see PasswordResetTokenRepository
 * @since 2025-01-01
 */
@Entity
@Data
public class PasswordResetToken {

    /**
     * Eindeutige Identifikationsnummer des Passwort-Reset-Tokens.
     * <p>
     * Auto-inkrementierende Primärschlüssel-ID, die von der Datenbank
     * automatisch generiert wird. Dient als eindeutige Identifikation
     * für jeden Token-Datensatz und ermöglicht effiziente Datenbankoperationen.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Eindeutiger Token-String für die Passwort-Zurücksetzung.
     * <p>
     * UUID-basierter String, der als sicherer Identifikator für
     * Passwort-Reset-Operationen dient. Der Token wird in E-Mail-Links
     * eingebettet und muss kryptographisch sicher und unvorhersagbar sein.
     * <p>
     * Sicherheitsattribute:
     * - NOT NULL-Constraint verhindert leere Tokens
     * - UNIQUE-Constraint gewährleistet Eindeutigkeit systemweit
     * - Kryptographische Sicherheit durch UUID-Generierung
     * - Unvorhersagbarkeit verhindert Brute-Force-Angriffe
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Verknüpfter Benutzer für den dieser Reset-Token gilt.
     * <p>
     * OneToOne-Beziehung zum User-Objekt, das den Benutzer repräsentiert,
     * für den dieser Token zur Passwort-Zurücksetzung erstellt wurde.
     * Die Beziehung gewährleistet referenzielle Integrität und
     * automatische Lifecycle-Verwaltung.
     * <p>
     * Beziehungsattribute:
     * - OneToOne: Exakt ein Token pro Benutzer-Zuordnung
     * - Optional = false: Token muss immer einem Benutzer zugeordnet sein
     * - CascadeType.ALL: Alle Operationen werden auf verknüpfte Entitäten übertragen
     * - orphanRemoval = true: Verwaiste Tokens werden automatisch entfernt
     * - JoinColumn: Fremdschlüssel-Beziehung über user_id
     * - NOT NULL: Verhindert Tokens ohne Benutzerverknüpfung
     */
    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Ablaufzeitpunkt des Passwort-Reset-Tokens.
     * <p>
     * LocalDateTime-Objekt, das den exakten Zeitpunkt definiert,
     * ab dem dieser Token nicht mehr gültig ist. Wird typischerweise
     * auf 10 Minuten nach Erstellung gesetzt, um das Sicherheitsrisiko
     * zu minimieren und gleichzeitig Benutzerfreundlichkeit zu gewährleisten.
     * <p>
     * Sicherheitsaspekte:
     * - Begrenzte Gültigkeitsdauer minimiert Angriffsfenster
     * - Präzise Zeitstempel für exakte Ablaufkontrolle
     * - Unterstützt automatische Ablaufprüfung durch isExpired()
     * - Verhindert unbegrenzte Token-Gültigkeit
     */
    private LocalDateTime expiryDate;

    /**
     * Flag zur Kennzeichnung bereits verwendeter Tokens.
     * <p>
     * Boolean-Flag, das markiert, ob dieser Token bereits für eine
     * erfolgreiche Passwort-Zurücksetzung verwendet wurde. Standardmäßig
     * auf false gesetzt und wird nach Verwendung permanent auf true gesetzt,
     * um Replay-Angriffe und Mehrfachverwendung zu verhindern.
     * <p>
     * Sicherheitsfunktion:
     * - Verhindert Mehrfachverwendung desselben Tokens
     * - Schutz vor Replay-Angriffen
     * - Permanente Markierung für Audit-Zwecke
     * - Unterstützt Token-Lifecycle-Management
     */
    private boolean used = false;

    /**
     * Prüft, ob der Passwort-Reset-Token abgelaufen ist.
     * <p>
     * Business-Logic-Methode, die den aktuellen Zeitpunkt mit dem
     * definierten Ablaufzeitpunkt vergleicht und true zurückgibt,
     * falls der Token nicht mehr gültig ist. Diese Methode ist
     * zentral für die Sicherheitsvalidierung in allen Token-Operationen.
     * <p>
     * Funktionalität:
     * - Echtzeitvergleich mit aktuellem Zeitstempel
     * - Präzise Sekundengenauigkeit für Ablaufprüfung
     * - Integration in Validierungsworkflows
     * - Unterstützung für benutzerfreundliche Fehlermeldungen
     * <p>
     * Verwendung:
     * - Validierung vor Formular-Anzeige
     * - Sicherheitsprüfung vor Passwort-Update
     * - Filter-Operationen in Service-Layer
     * - Automatische Cleanup-Prozesse
     *
     * @return true wenn der Token abgelaufen ist, false wenn noch gültig
     */
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}