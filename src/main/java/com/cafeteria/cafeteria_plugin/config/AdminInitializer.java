package com.cafeteria.cafeteria_plugin.config;

import com.cafeteria.cafeteria_plugin.models.Admin;
import com.cafeteria.cafeteria_plugin.models.User.UserType;
import com.cafeteria.cafeteria_plugin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Konfigurationsklasse für die automatische Erstellung eines Standard-Administrators.
 *
 * Diese Klasse implementiert CommandLineRunner und wird beim Anwendungsstart ausgeführt.
 * Sie erstellt automatisch einen Standard-Administrator-Account, falls noch keiner existiert.
 * Dies stellt sicher, dass das System immer einen funktionsfähigen Admin-Zugang hat.
 *
 * Standard-Anmeldedaten:
 * - Benutzername: admin_user.admin
 * - Passwort: admin123!
 * - Email: admin@cafeteria.com
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see CommandLineRunner
 * @see Admin
 * @since 2025-04-03
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    /**
     * Repository für Benutzerverwaltung und Datenbankzugriff.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Zusätzliche Repository-Referenz für Admin-spezifische Operationen.
     *
     * @deprecated Diese Referenz zeigt auf dasselbe Repository wie userRepository
     */
    @Autowired
    private UserRepository adminRepository;

    /**
     * Passwort-Encoder für sichere Passwort-Verschlüsselung mit BCrypt.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Wird beim Anwendungsstart ausgeführt und erstellt einen Standard-Administrator.
     *
     * Diese Methode prüft, ob bereits ein Administrator mit dem Standard-Benutzernamen
     * existiert. Falls nicht, wird ein neuer Administrator mit vordefinierten
     * Anmeldedaten erstellt und in der Datenbank gespeichert.
     *
     * @param args Kommandozeilenargumente (nicht verwendet)
     * @throws Exception Falls ein Fehler bei der Erstellung auftritt
     */
    @Override
    public void run(String... args) {
        String defaultUsername = "admin_user.admin";
        String defaultEmail = "admin@cafeteria.com";

        boolean adminExists = userRepository.existsByUsername(defaultUsername);

        if (!adminExists) {
            Admin admin = new Admin();
            admin.setUsername(defaultUsername);
            admin.setEmail(defaultEmail);
            admin.setPassword(passwordEncoder.encode("admin123!"));
            admin.setUserType(UserType.ADMIN);

            adminRepository.save(admin);
            System.out.println("✅ Standard-Administrator erstellt: " + defaultUsername + " / admin123!");
        } else {
            System.out.println("✅ Standard-Administrator existiert bereits.");
        }
    }
}