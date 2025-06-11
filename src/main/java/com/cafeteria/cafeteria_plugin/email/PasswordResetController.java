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
 * @since 2025-03-12
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