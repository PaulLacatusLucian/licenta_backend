package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.email.EmailService;
import com.cafeteria.cafeteria_plugin.email.passwordReset.PasswordResetService;
import com.cafeteria.cafeteria_plugin.email.passwordReset.PasswordResetToken;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.UserService;
import com.cafeteria.cafeteria_plugin.services.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST-Controller für Benutzerauthentifizierung und -registrierung.
 *
 * Diese Klasse behandelt alle authentifizierungsbezogenen Operationen:
 * - Benutzeranmeldung mit JWT-Token-Generierung
 * - Registrierung verschiedener Benutzertypen
 * - Passwort-Reset-Funktionalität
 * - Komplexe Registrierung (Schüler mit Eltern)
 *
 * Sicherheitsmerkmale:
 * - JWT-basierte Authentifizierung
 * - BCrypt-Passwort-Verschlüsselung
 * - Rollenbasierte Zugriffskontrolle
 * - Email-Verifikation für Passwort-Resets
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see UserService
 * @see JwtUtil
 * @see PasswordEncoder
 * @since 2024-11-28
 */
@RestController
@RequestMapping("/auth/")
public class UserController {

    /**
     * Service für Benutzerverwaltungsoperationen.
     */
    @Autowired
    private UserService userService;

    /**
     * Service für Koch-spezifische Operationen.
     */
    @Autowired
    private ChefService chefService;

    /**
     * Service für Klassenverwaltung.
     */
    @Autowired
    private ClassService classService;

    /**
     * Passwort-Encoder für sichere Verschlüsselung.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * JWT-Utility für Token-Generierung und -Validierung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Service für Email-Versendung.
     */
    @Autowired
    private EmailService emailService;

    /**
     * Service für Passwort-Reset-Token-Verwaltung.
     */
    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Authentifiziert einen Benutzer und generiert ein JWT-Token.
     *
     * Diese Methode validiert die Anmeldedaten und erstellt bei erfolgreicher
     * Authentifizierung ein JWT-Token mit Benutzertyp-Informationen.
     *
     * @param credentials Map mit 'username' und 'password' Schlüsseln
     * @return ResponseEntity mit JWT-Token bei Erfolg oder Fehlermeldung
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Benutzername und Passwort sind erforderlich"));
            }

            User user = userService.findByUsername(username).orElse(null);

            Optional<User> testuser = userService.findByUsername(username);
            if (testuser.isPresent()) {
                System.out.println("Benutzer gefunden: " + testuser.get().getUsername());
                System.out.println("Gespeichertes Passwort in DB: " + testuser.get().getPassword());
            } else {
                System.out.println("Benutzer existiert nicht!");
            }

            if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Ungültige Anmeldedaten"));
            }

            String token = jwtUtil.generateToken(username, user.getUserType());

            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Fehler bei der Anmeldung: " + e.getMessage()));
        }
    }

    /**
     * Registriert einen Schüler zusammen mit den Elterninformationen.
     *
     * Diese komplexe Registrierungsmethode erstellt sowohl einen Schüler
     * als auch die zugehörigen Elterndaten in einer Transaktion.
     * Nur Administratoren können diese Funktion verwenden.
     *
     * @param userData Map mit verschachtelten Schüler- und Elterndaten
     * @return ResponseEntity mit Erfolgsmeldung oder Fehlern
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-with-parent")
    public ResponseEntity<?> registerStudentWithParent(@RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> studentData = (Map<String, Object>) userData.get("student");
            if (studentData == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Schülerdaten fehlen"));
            }

            System.out.println("Schülerdaten: " + studentData);
            System.out.println("Extrahierte Schüler-Email: " + studentData.get("email"));

            // === SCHÜLER ERSTELLEN ===
            Student student = new Student();
            student.setUserType(User.UserType.STUDENT);
            student.setName((String) studentData.get("name"));
            student.setEmail((String) studentData.get("email"));
            student.setPhoneNumber((String) studentData.get("phoneNumber"));

            String studentBaseName = student.getName().toLowerCase().replaceAll("\\s+", "_");
            student.setUsername(studentBaseName + ".student");
            String studentRawPassword = studentBaseName.replace(".", "") + "123!";
            student.setPassword(passwordEncoder.encode(studentRawPassword));

            // === KLASSE ZUWEISEN ===
            Map<String, Object> studentClassData = (Map<String, Object>) studentData.get("studentClass");
            if (studentClassData != null) {
                Long classId = Long.parseLong(studentClassData.get("id").toString());
                Class studentSchoolClass = classService.getClassById(classId)
                        .orElseThrow(() -> new RuntimeException("Klasse mit ID " + classId + " nicht in Datenbank gefunden"));
                student.setStudentClass(studentSchoolClass);
            }

            // === ELTERN ERSTELLEN UND SPEICHERN VOR SCHÜLER ===
            Map<String, Object> parentData = (Map<String, Object>) studentData.get("parent");
            if (parentData != null) {
                Parent parent = new Parent();
                String parentBaseName = ((String) parentData.get("motherName")).toLowerCase().replaceAll("\\s+", "_");
                parent.setUsername(parentBaseName + ".parent");
                String parentRawPassword = parentBaseName.replace(".", "") + "123!";
                parent.setPassword(passwordEncoder.encode(parentRawPassword));
                parent.setUserType(User.UserType.PARENT);
                parent.setMotherName((String) parentData.get("motherName"));
                parent.setMotherEmail((String) parentData.get("motherEmail"));
                parent.setMotherPhoneNumber((String) parentData.get("motherPhoneNumber"));
                parent.setFatherName((String) parentData.get("fatherName"));
                parent.setFatherEmail((String) parentData.get("fatherEmail"));
                parent.setFatherPhoneNumber((String) parentData.get("fatherPhoneNumber"));
                parent.setEmail((String) parentData.get("email"));

                // Eltern zuerst in DB speichern
                userService.createUser(parent);

                // Eltern mit Schüler verknüpfen
                student.setParent(parent);
            }

            System.out.println("Schüler vor Speicherung: " + student);

            // Schüler in DB speichern
            userService.createUser(student);

            // Token für Passwort-Reset erstellen
            PasswordResetToken studentToken = passwordResetService.createTokenForUser(student);
            String studentResetLink = "http://localhost:8080/auth/reset-password?token=" + studentToken.getToken();
            emailService.sendResetPasswordEmail(student.getEmail(), student.getUsername(), studentResetLink);

            if (student.getParent() != null) {
                PasswordResetToken parentToken = passwordResetService.createTokenForUser(student.getParent());
                String parentResetLink = "http://localhost:8080/auth/reset-password?token=" + parentToken.getToken();
                emailService.sendResetPasswordEmail(student.getParent().getEmail(), student.getParent().getFatherName(), parentResetLink);
            }

            return ResponseEntity.ok(Map.of("message", "Schüler und Eltern erfolgreich erstellt!"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Fehler: " + e.getMessage()));
        }
    }

    /**
     * Registriert einen neuen Lehrer im System.
     * Nur Administratoren können neue Lehrer erstellen.
     *
     * @param teacher Lehrerobjekt mit grundlegenden Informationen
     * @return ResponseEntity mit Erfolgsmeldung und generierten Anmeldedaten
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody Teacher teacher) {
        try {
            if (teacher.getName() == null || teacher.getSubject() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name und Fach sind erforderlich"));
            }

            String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", "_");
            teacher.setUsername(baseUsername + ".prof");
            String rawPassword = baseUsername.replace(".", "") + "123!";
            teacher.setPassword(passwordEncoder.encode(rawPassword));
            teacher.setUserType(User.UserType.TEACHER);

            userService.createUser(teacher);
            PasswordResetToken token = passwordResetService.createTokenForUser(teacher);
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token.getToken();
            emailService.sendResetPasswordEmail(teacher.getEmail(), teacher.getUsername(), resetLink);

            return ResponseEntity.ok(Map.of(
                    "message", "Lehrer erfolgreich erstellt!",
                    "username", teacher.getUsername()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Fehler: " + e.getMessage()));
        }
    }

    /**
     * Registriert einen neuen Administrator.
     * Öffentlich zugänglich für die Erstellung des ersten Admins.
     *
     * @param request Map mit 'name' und 'email' für den neuen Admin
     * @return ResponseEntity mit generierten Anmeldedaten
     */
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String email = request.get("email");

            if (name == null || email == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name und Email sind erforderlich"));
            }

            String baseUsername = name.toLowerCase().replaceAll("\\s+", "_");
            String username = baseUsername + ".admin";
            String rawPassword = baseUsername.replace(".", "") + "123!";

            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(rawPassword));
            admin.setEmail(email);

            userService.createUser(admin);

            return ResponseEntity.ok(Map.of(
                    "message", "Administrator erfolgreich erstellt!",
                    "username", username,
                    "password", rawPassword
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Fehler bei der Admin-Erstellung: " + e.getMessage()));
        }
    }

    /**
     * Registriert einen neuen Koch für die Cafeteria.
     * Nur Administratoren können Köche erstellen.
     *
     * @param chef Kochobjekt mit grundlegenden Informationen
     * @return ResponseEntity mit generierten Anmeldedaten
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-chef")
    public ResponseEntity<?> registerChef(@RequestBody Chef chef) {
        try {
            if (chef.getName() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name des Kochs ist erforderlich"));
            }

            // Automatische Generierung von Benutzername und Passwort
            String baseUsername = chef.getName().toLowerCase().replaceAll("\\s+", "_");
            String username = baseUsername + ".chef";
            String rawPassword = baseUsername.replace("_", "") + "123!";

            chef.setUsername(username);
            chef.setPassword(passwordEncoder.encode(rawPassword));
            chef.setUserType(User.UserType.CHEF);

            // WICHTIG: Koch in users-Tabelle speichern, nicht nur in chefs
            userService.createUser(chef);
            PasswordResetToken token = passwordResetService.createTokenForUser(chef);
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token.getToken();
            emailService.sendResetPasswordEmail(chef.getEmail(), chef.getUsername(), resetLink);

            return ResponseEntity.ok(Map.of(
                    "message", "Koch erfolgreich registriert!",
                    "username", username,
                    "password", rawPassword
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Fehler: " + e.getMessage())
            );
        }
    }
}