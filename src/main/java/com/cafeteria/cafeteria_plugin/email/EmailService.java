package com.cafeteria.cafeteria_plugin.email;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String username, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Schimbare parolă - Cafeteria System");
        message.setText("Bună, " + username + ",\n\n"
                + "Contul tău a fost creat. Pentru a-ți seta parola, accesează linkul de mai jos:\n\n"
                + resetLink + "\n\n"
                + "Mulțumim!");
        mailSender.send(message);
    }
}
