package com.cafeteria.cafeteria_plugin.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Service für E-Mail-Versand und Kommunikationsmanagement im Schulsystem.
 * <p>
 * Diese Service-Klasse bietet umfassende E-Mail-Funktionalitäten für das
 * Cafeteria-Plugin und ermöglicht verschiedene Arten der elektronischen
 * Kommunikation zwischen den Systembenutzern. Sie unterstützt sowohl
 * administrative E-Mails als auch die Kommunikation zwischen Eltern und Lehrern.
 * @author Paul Lacatus
 * @version 1.0
 * @see JavaMailSender
 * @see SimpleMailMessage
 * @see MimeMessage
 * @since 2025-03-12
 */
@Service
public class EmailService {

    /**
     * Spring Mail Sender für E-Mail-Versand.
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sendet eine Passwort-Reset-E-Mail an einen Benutzer.
     * <p>
     * Diese Methode wird typischerweise aufgerufen, wenn ein neues Benutzerkonto
     * erstellt wird oder ein Benutzer sein Passwort zurücksetzen möchte.
     * Die E-Mail enthält einen sicheren Link zur Passwort-Festlegung und
     * personalisierte Begrüßung mit dem Benutzernamen.
     * @param to        E-Mail-Adresse des Empfängers
     * @param username  Benutzername für personalisierte Anrede
     * @param resetLink Sicherer Link zur Passwort-Festlegung
     */
    public void sendResetPasswordEmail(String to, String username, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Passwort zurücksetzen – Cafeteria System");
        message.setText("Hallo, " + username + ",\n\n"
                + "Dein Konto wurde erstellt. Um dein Passwort festzulegen, klicke bitte auf den folgenden Link:\n\n"
                + resetLink + "\n\n"
                + "Vielen Dank!");
        mailSender.send(message);
    }

    /**
     * Sendet eine einfache Textnachricht an einen Empfänger.
     * @param to      E-Mail-Adresse des Empfängers
     * @param subject Betreff der E-Mail
     * @param text    Textinhalt der Nachricht
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendMessageFromParent(String parentEmail, String teacherEmail, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(teacherEmail);
        helper.setSubject("[Elternnachricht] " + subject);

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 10px;'>" +
                "<h2 style='color: #69a79c; margin-bottom: 20px;'>Johann Ettinger Gymnasium - Elternnachricht</h2>" +
                "<div style='background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                "<p style='margin-bottom: 15px;'><strong>Sehr geehrte Lehrkraft,</strong></p>" +
                "<p style='margin-bottom: 15px;'>Sie haben eine neue Nachricht von einem Elternteil über das Schulportal erhalten:</p>" +
                "<hr style='border: 1px solid #e0e0e0; margin: 20px 0;'>" +
                "<div style='background-color: #f7f7f7; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<p style='margin: 10px 0;'><strong>Betreff:</strong> " + subject + "</p>" +
                "<p style='margin: 10px 0;'><strong>Von:</strong> " + parentEmail + "</p>" +
                "</div>" +
                "<div style='margin: 20px 0;'>" +
                "<p style='margin-bottom: 10px;'><strong>Nachricht:</strong></p>" +
                "<div style='background-color: #fafafa; padding: 15px; border-left: 4px solid #69a79c; border-radius: 4px;'>" +
                "<p style='margin: 0; white-space: pre-wrap;'>" + content.replace("\n", "<br>") + "</p>" +
                "</div>" +
                "</div>" +
                "<hr style='border: 1px solid #e0e0e0; margin: 20px 0;'>" +
                "<p style='margin-top: 20px; color: #666; font-size: 14px;'>" +
                "Bitte antworten Sie direkt auf diese E-Mail, um mit dem Elternteil in Kontakt zu treten." +
                "</p>" +
                "<p style='color: #999; font-size: 12px; margin-top: 10px;'>" +
                "Diese Nachricht wurde automatisch über das Schulportal generiert. " +
                "Bei technischen Problemen wenden Sie sich bitte an die IT-Abteilung." +
                "</p>" +
                "</div>" +
                "<div style='text-align: center; margin-top: 20px; color: #999; font-size: 12px;'>" +
                "<p>© " + java.time.Year.now().getValue() + " Johann Ettinger Gymnasium | Bulevardul Cloșca 72, Satu Mare</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";

        String plainTextContent = "Sehr geehrte Lehrkraft,\n\n" +
                "Sie haben eine neue Nachricht von einem Elternteil über das Schulportal erhalten:\n\n" +
                "Betreff: " + subject + "\n" +
                "Von: " + parentEmail + "\n\n" +
                "Nachricht:\n" + content + "\n\n" +
                "Bitte antworten Sie direkt auf diese E-Mail, um mit dem Elternteil in Kontakt zu treten.\n\n" +
                "Mit freundlichen Grüßen,\n" +
                "Johann Ettinger Gymnasium";

        helper.setText(plainTextContent, htmlContent);
        helper.setFrom("no-reply@lgerm-ettinger.ro", "Johann Ettinger Gymnasium - Schulportal");
        helper.setReplyTo(parentEmail);

        mailSender.send(message);
    }

    /**
     * Sendet eine Kontaktformular-Nachricht an die Schule.
     *
     * Diese Methode verarbeitet Nachrichten vom öffentlichen Kontaktformular
     * und leitet sie an die offizielle Schul-E-Mail-Adresse weiter.
     * Die E-Mail enthält alle vom Benutzer eingegebenen Informationen
     * und ermöglicht eine direkte Antwort an den Absender.
     *
     * @param senderName    Name des Absenders
     * @param senderEmail   E-Mail-Adresse des Absenders
     * @param subject       Betreff der Nachricht
     * @param content       Inhalt der Nachricht
     * @throws MessagingException           Bei Fehlern in der E-Mail-Konfiguration
     * @throws UnsupportedEncodingException Bei Problemen mit der Zeichenkodierung
     */
    public void sendContactFormMessage(String senderName, String senderEmail, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Schul-E-Mail-Adresse als Empfänger
        helper.setTo("ltg.johann.ettinger@gmail.com");

        // Betreff mit Prefix für bessere Identifikation
        helper.setSubject("[Kontaktformular] " + subject);

        // HTML-formatierter E-Mail-Inhalt für bessere Lesbarkeit
        String htmlContent = "<html><body>" +
                "<h3>Neue Nachricht über das Kontaktformular</h3>" +
                "<hr>" +
                "<p><strong>Absender:</strong> " + senderName + "</p>" +
                "<p><strong>E-Mail:</strong> " + senderEmail + "</p>" +
                "<p><strong>Betreff:</strong> " + subject + "</p>" +
                "<hr>" +
                "<p><strong>Nachricht:</strong></p>" +
                "<p>" + content.replace("\n", "<br>") + "</p>" +
                "<hr>" +
                "<p><small>Diese Nachricht wurde über das Kontaktformular der Schulwebsite gesendet.</small></p>" +
                "</body></html>";

        helper.setText(htmlContent, true);

        // Absender-Konfiguration
        helper.setFrom("no-reply@lgerm-ettinger.ro", "Johann Ettinger Gymnasium - Kontaktformular");

        // Reply-To auf die E-Mail des Absenders setzen für direkte Antworten
        helper.setReplyTo(senderEmail, senderName);

        mailSender.send(message);
    }
}