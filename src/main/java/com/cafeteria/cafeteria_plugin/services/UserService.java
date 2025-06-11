package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Zentraler Service für die Benutzerverwaltung im Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see Student
 * @see Parent
 * @see Teacher
 * @see Chef
 * @see Admin
 * @since 2024-11-28
 */
@Service
@Transactional // Stellt Atomarität aller Methoden sicher
public class UserService {

    /**
     * Repository für grundlegende Benutzeroperationen.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository für schülerspezifische Operationen.
     */
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Repository für klassenspezifische Operationen.
     */
    @Autowired
    private ClassRepository classRepository;

    /**
     * Repository für elternspezifische Operationen.
     */
    @Autowired
    private ParentRepository parentRepository;

    /**
     * Repository für lehrerspezifische Operationen.
     */
    @Autowired
    private TeacherRepository teacherRepository;

    /**
     * Passwort-Encoder für sichere Passwort-Verschlüsselung.
     * Lazy-Loading verhindert zirkuläre Abhängigkeiten.
     */
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    /**
     * Erstellt einen neuen Benutzer im System.
     *
     * Diese Methode führt umfassende Validierungen durch und speichert
     * den Benutzer in der entsprechenden Tabelle basierend auf dem Benutzertyp.
     *
     * Validierungen:
     * - Überprüfung auf null-Werte für kritische Felder
     * - Eindeutigkeitsprüfung für Benutzername und Email
     * - Typspezifische Validierung
     *
     * @param user Der zu erstellende Benutzer (muss gültigen Typ haben)
     * @return Der gespeicherte Benutzer mit generierter ID
     * @throws IllegalArgumentException Falls Validierung fehlschlägt
     * @throws RuntimeException Falls Benutzername oder Email bereits existiert
     */
    public User createUser(User user) {
        // Grundlegende Validierungen
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Benutzername, Email und Passwort sind erforderlich");
        }

        // Eindeutigkeitsprüfungen
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Benutzername existiert bereits");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email bereits verwendet");
        }

        // Typspezifische Speicherung basierend auf Benutzertyp
        return switch (user.getUserType()) {
            case STUDENT -> {
                if (!(user instanceof Student student)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht Student");
                }
                yield studentRepository.save(student);
            }
            case PARENT -> {
                if (!(user instanceof Parent parent)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht Parent");
                }
                yield parentRepository.save(parent);
            }
            case TEACHER -> {
                if (!(user instanceof Teacher teacher)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht Teacher");
                }
                yield teacherRepository.save(teacher);
            }
            case ADMIN -> {
                if (!(user instanceof Admin admin)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht Admin");
                }
                yield userRepository.save(admin);
            }
            case CHEF -> {
                if (!(user instanceof Chef chef)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht Chef");
                }
                yield userRepository.save(chef);
            }
            default -> throw new IllegalArgumentException("Ungültiger Benutzertyp");
        };
    }

    /**
     * Sucht einen Benutzer anhand des Benutzernamens.
     *
     * @param username Der zu suchende Benutzername
     * @return Optional mit dem gefundenen Benutzer oder leer falls nicht gefunden
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Aktualisiert das Passwort eines Benutzers ohne weitere Validierungen.
     *
     * Diese Methode wird hauptsächlich für Passwort-Reset-Funktionalitäten
     * verwendet, wo die Validierung bereits durch Token-Verifikation erfolgt ist.
     *
     * @param user Der Benutzer mit dem neuen (bereits verschlüsselten) Passwort
     */
    public void forceUpdatePassword(User user) {
        userRepository.save(user);
    }
}