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

    /**
     * Sendet eine Nachricht von einem Elternteil an einen Lehrer über das Schulportal.
     * @param parentEmail  E-Mail-Adresse des Elternteils (für Reply-To)
     * @param teacherEmail E-Mail-Adresse des Lehrers (Empfänger)
     * @param subject      Betreff der Nachricht
     * @param content      Inhalt der Nachricht
     * @throws MessagingException           Bei Fehlern in der E-Mail-Konfiguration
     * @throws UnsupportedEncodingException Bei Problemen mit der Zeichenkodierung
     */
    public void sendMessageFromParent(String parentEmail, String teacherEmail, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(teacherEmail);
        helper.setSubject(subject);
        helper.setText(content, false);

        helper.setFrom("no-reply@school.edu", "Nachricht von Elternteil über das Schulportal");
        helper.setReplyTo(parentEmail);

        mailSender.send(message);
    }
}