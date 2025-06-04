package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional // Stellt die Atomarität aller Methoden sicher
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    /**
     * Erstellt einen neuen Benutzer je nach Typ (Student, Lehrer, Elternteil, etc.)
     */
    public User createUser(User user) {
        // Grundvalidierung
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Benutzername, E-Mail und Passwort sind erforderlich.");
        }

        // Überprüfen, ob Benutzername oder E-Mail bereits existiert
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Benutzername ist bereits vergeben.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("E-Mail-Adresse wird bereits verwendet.");
        }

        // Speichern des Benutzers je nach Typ
        return switch (user.getUserType()) {
            case STUDENT -> {
                if (!(user instanceof Student student)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht vom Typ Student.");
                }
                yield studentRepository.save(student);
            }
            case PARENT -> {
                if (!(user instanceof Parent parent)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht vom Typ Eltern.");
                }
                yield parentRepository.save(parent);
            }
            case TEACHER -> {
                if (!(user instanceof Teacher teacher)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht vom Typ Lehrer.");
                }
                yield teacherRepository.save(teacher);
            }
            case ADMIN -> {
                if (!(user instanceof Admin admin)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht vom Typ Admin.");
                }
                yield userRepository.save(admin);
            }
            case CHEF -> {
                if (!(user instanceof Chef chef)) {
                    throw new IllegalArgumentException("Benutzertyp ist nicht vom Typ Koch.");
                }
                yield userRepository.save(chef);
            }
            default -> throw new IllegalArgumentException("Ungültiger Benutzertyp.");
        };
    }

    /**
     * Findet einen Benutzer anhand seines Benutzernamens
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Erzwingt das Aktualisieren des Passworts für einen Benutzer
     */
    public void forceUpdatePassword(User user) {
        userRepository.save(user);
    }
}
