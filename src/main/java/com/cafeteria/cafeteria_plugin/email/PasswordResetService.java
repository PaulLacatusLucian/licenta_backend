package com.cafeteria.cafeteria_plugin.email;

import com.cafeteria.cafeteria_plugin.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service für die Verwaltung von Passwort-Reset-Tokens und sichere Authentifizierung.
 * <p>
 * Diese Service-Klasse bildet das Herzstück der sicheren Passwort-Zurücksetzung
 * im Schulsystem und verwaltet den kompletten Lifecycle von Reset-Tokens.
 * Sie gewährleistet die Sicherheit durch zeitlich begrenzte, einmalig verwendbare
 * Tokens und bietet robuste Validierungsmechanismen für die Benutzerauthentifizierung.
 * <p>
 * Hauptfunktionalitäten:
 * - Generierung sicherer UUID-basierter Reset-Tokens
 * - Zeitgesteuerte Token-Gültigkeit mit konfigurierbarer Ablaufzeit
 * - Umfassende Token-Validierung mit mehrschichtiger Sicherheitsprüfung
 * - Automatische Token-Deaktivierung nach Verwendung
 * - Datenbankgestützte Token-Persistierung und -Verwaltung
 * <p>
 * Sicherheitskonzept:
 * - Kryptographisch sichere UUID-Generierung für unvorhersagbare Tokens
 * - Kurze Ablaufzeiten (10 Minuten) zur Minimierung des Sicherheitsrisikos
 * - Einmalige Token-Verwendung verhindert Replay-Angriffe
 * - Kombinierte Validierung von Ablauf- und Verwendungsstatus
 * - Sichere Speicherung in der Datenbank mit entsprechenden Constraints
 * <p>
 * Token-Lifecycle:
 * 1. Erstellung: Generierung mit UUID und Ablaufzeit
 * 2. Validierung: Mehrschichtige Sicherheitsprüfung
 * 3. Verwendung: Einmalige Nutzung zur Passwort-Zurücksetzung
 * 4. Deaktivierung: Permanente Markierung als verwendet
 * <p>
 * Integration:
 * - Enge Zusammenarbeit mit PasswordResetController für Web-Workflow
 * - Unterstützung des EmailService für Token-Versand
 * - Repository-basierte Datenpersistierung für Ausfallsicherheit
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see PasswordResetToken
 * @see PasswordResetTokenRepository
 * @see User
 * @see PasswordResetController
 * @since 2025-03-13
 */
@Service
public class PasswordResetService {

    /**
     * Repository für Passwort-Reset-Token-Operationen und Datenpersistierung.
     */
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    /**
     * Erstellt einen neuen Passwort-Reset-Token für einen Benutzer.
     * @param user Benutzer für den der Reset-Token erstellt werden soll
     * @return Neu erstellter und persistierter PasswordResetToken
     */
    public PasswordResetToken createTokenForUser(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(token);
        return token;
    }

    /**
     * Validiert einen Passwort-Reset-Token auf Gültigkeit und Verwendbarkeit.
     * @param token Token-String zur Validierung
     * @return Gültiges PasswordResetToken-Objekt oder null bei ungültigem Token
     */
    public PasswordResetToken validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired() && !t.isUsed())
                .orElse(null);
    }

    /**
     * Markiert einen Passwort-Reset-Token als verwendet und deaktiviert ihn permanent.
     * @param token Zu deaktivierender PasswordResetToken
     */
    public void markTokenAsUsed(PasswordResetToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }
}