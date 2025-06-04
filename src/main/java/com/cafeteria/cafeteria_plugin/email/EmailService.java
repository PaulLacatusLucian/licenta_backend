package com.cafeteria.cafeteria_plugin.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

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

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

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
