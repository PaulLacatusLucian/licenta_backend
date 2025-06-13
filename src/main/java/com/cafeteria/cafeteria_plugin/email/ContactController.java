package com.cafeteria.cafeteria_plugin.email;

import com.cafeteria.cafeteria_plugin.email.ContactMessageDTO;
import com.cafeteria.cafeteria_plugin.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller für öffentliche Kontaktformular-Anfragen.
 *
 * Dieser Controller verwaltet die Verarbeitung von Kontaktformular-Nachrichten
 * von der Schulwebsite. Er ist öffentlich zugänglich und erfordert keine
 * Authentifizierung, da er für Eltern, Interessenten und andere Besucher
 * der Website gedacht ist.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see EmailService
 * @see ContactMessageDTO
 * @since 2025-01-15
 */
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*") // Anpassen Sie dies an Ihre Frontend-Domain
public class ContactController {

    @Autowired
    private EmailService emailService;

    /**
     * Verarbeitet Kontaktformular-Nachrichten und sendet sie an die Schule.
     *
     * Dieser Endpoint ist öffentlich zugänglich und ermöglicht es Besuchern
     * der Website, Nachrichten an die Schulverwaltung zu senden. Die Nachrichten
     * werden per E-Mail an die offizielle Schul-E-Mail-Adresse weitergeleitet.
     *
     * @param contactMessage DTO mit den Kontaktformular-Daten
     * @return ResponseEntity mit Erfolgsstatus oder Fehlermeldung
     */
    @PostMapping("/contact")
    public ResponseEntity<?> sendContactMessage( @RequestBody ContactMessageDTO contactMessage) {
        try {
            // Validierung der Eingaben
            if (contactMessage.getName() == null || contactMessage.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name ist erforderlich");
            }

            if (contactMessage.getEmail() == null || !isValidEmail(contactMessage.getEmail())) {
                return ResponseEntity.badRequest().body("Gültige E-Mail-Adresse ist erforderlich");
            }

            if (contactMessage.getSubject() == null || contactMessage.getSubject().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Betreff ist erforderlich");
            }

            if (contactMessage.getMessage() == null || contactMessage.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Nachricht ist erforderlich");
            }

            // E-Mail versenden
            emailService.sendContactFormMessage(
                    contactMessage.getName().trim(),
                    contactMessage.getEmail().trim(),
                    contactMessage.getSubject().trim(),
                    contactMessage.getMessage().trim()
            );

            return ResponseEntity.ok().body("Nachricht erfolgreich gesendet");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Senden der Nachricht. Bitte versuchen Sie es später erneut.");
        }
    }

    /**
     * Validiert E-Mail-Adressen mit einem einfachen Regex-Pattern.
     *
     * @param email zu validierende E-Mail-Adresse
     * @return true wenn die E-Mail-Adresse gültig ist, sonst false
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
}