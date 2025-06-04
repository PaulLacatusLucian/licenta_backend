package com.cafeteria.cafeteria_plugin.email;

import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Web-Controller für die Passwort-Zurücksetzung und Benutzerkontoaktivierung.
 * <p>
 * Diese Controller-Klasse verwaltet den kompletten Workflow der Passwort-Zurücksetzung
 * im Schulsystem und bietet sichere Web-Endpunkte für die Benutzerauthentifizierung.
 * Sie ermöglicht es Benutzern, ihre Passwörter über sichere Token-basierte Links
 * zurückzusetzen und neue Konten zu aktivieren.
 * <p>
 * Hauptfunktionalitäten:
 * - Sichere Token-Validierung für Passwort-Reset-Links
 * - Web-Formular-Darstellung für Passwort-Eingabe
 * - Passwort-Aktualisierung mit sicherer Verschlüsselung
 * - Token-Lifecycle-Management (Ablauf und Verwendung)
 * - Benutzerfreundliche Fehlerbehandlung mit entsprechenden Views
 * - Schutz vor Token-Wiederverwendung und Missbrauch
 * <p>
 * Sicherheitsfeatures:
 * - Zeitbasierte Token-Validierung
 * - Einmalige Token-Verwendung
 * - Sichere Passwort-Verschlüsselung mit PasswordEncoder
 * - Automatische Token-Deaktivierung nach Verwendung
 * - Schutz vor abgelaufenen oder ungültigen Tokens
 * <p>
 * Web-Views:
 * - Passwort-Reset-Formular für neue Passwort-Eingabe
 * - Erfolgsmeldung nach erfolgreicher Passwort-Änderung
 * - Fehlermeldungen für abgelaufene oder bereits verwendete Tokens
 * - Benutzerfreundliche Fehlerbehandlung mit informativen Meldungen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see PasswordResetService
 * @see PasswordResetToken
 * @see UserService
 * @see PasswordEncoder
 * @since 2025-01-01
 */
@Controller
@RequestMapping("/auth")
public class PasswordResetController {

    /**
     * Service für Passwort-Reset-Operationen und Token-Verwaltung.
     */
    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Service für Benutzerverwaltung und Kontooperationen.
     */
    @Autowired
    private UserService userService;

    /**
     * Encoder für sichere Passwort-Verschlüsselung.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Zeigt das Passwort-Reset-Formular für einen gültigen Token an.
     * <p>
     * Diese GET-Methode wird aufgerufen, wenn ein Benutzer auf einen
     * Passwort-Reset-Link klickt. Sie validiert den übergebenen Token
     * und zeigt bei Gültigkeit das entsprechende Formular zur
     * Passwort-Eingabe an.
     * <p>
     * Token-Validierung:
     * - Überprüfung der Token-Existenz und Gültigkeit
     * - Kontrolle des Ablaufdatums
     * - Verifikation, ob Token bereits verwendet wurde
     * - Weiterleitung zu entsprechenden Fehlerseiten bei Problemen
     * <p>
     * View-Routing:
     * - "reset-password-form": Bei gültigem, ungenutztem Token
     * - "reset-password-expired": Bei abgelaufenem oder ungültigem Token
     * - "reset-password-already-used": Bei bereits verwendetem Token
     * <p>
     * Sicherheitsaspekte:
     * - Verhindert Zugriff mit ungültigen Tokens
     * - Schutz vor Mehrfachverwendung von Tokens
     * - Zeitbasierte Token-Validierung
     *
     * @param token Token-String aus dem Passwort-Reset-Link
     * @param model Spring Model für View-Datenübertragung
     * @return View-Name basierend auf Token-Validierungsergebnis
     */
    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        PasswordResetToken resetToken = passwordResetService.validateToken(token);

        if (resetToken == null) {
            return "reset-password-expired";
        }

        if (resetToken.isUsed()) {
            return "reset-password-already-used";
        }

        model.addAttribute("token", token);
        return "reset-password-form";
    }

    /**
     * Verarbeitet die Passwort-Zurücksetzung nach Formular-Übermittlung.
     * <p>
     * Diese POST-Methode wird aufgerufen, wenn ein Benutzer das
     * Passwort-Reset-Formular mit seinem neuen Passwort absendet.
     * Sie führt die finale Token-Validierung durch und aktualisiert
     * das Benutzerpasswort sicher.
     * <p>
     * Verarbeitungsschritte:
     * 1. Erneute Token-Validierung für Sicherheit
     * 2. Überprüfung auf bereits verwendete Tokens
     * 3. Sichere Passwort-Verschlüsselung mit PasswordEncoder
     * 4. Benutzer-Update in der Datenbank
     * 5. Token-Markierung als verwendet
     * 6. Weiterleitung zur Erfolgsseite
     * <p>
     * Sicherheitsmaßnahmen:
     * - Doppelte Token-Validierung (GET und POST)
     * - Sichere Passwort-Hashing mit modernen Algorithmen
     * - Automatische Token-Deaktivierung nach Verwendung
     * - Erzwungene Passwort-Aktualisierung ohne weitere Validierung
     * <p>
     * Fehlerbehandlung:
     * - Graceful Handling von abgelaufenen Tokens
     * - Schutz vor mehrfacher Passwort-Zurücksetzung
     * - Benutzerfreundliche Fehlermeldungen
     *
     * @param token       Token-String zur finalen Validierung
     * @param newPassword Neues Passwort vom Benutzer eingegeben
     * @param model       Spring Model für View-Datenübertragung
     * @return View-Name basierend auf Verarbeitungsergebnis
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                Model model) {
        PasswordResetToken resetToken = passwordResetService.validateToken(token);

        if (resetToken == null) {
            return "reset-password-expired";
        }

        if (resetToken.isUsed()) {
            return "reset-password-already-used";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.forceUpdatePassword(user);
        passwordResetService.markTokenAsUsed(resetToken);

        return "reset-password-success";
    }

}