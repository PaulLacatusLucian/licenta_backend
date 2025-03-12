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
@Transactional // Asigură atomicitatea tuturor metodelor
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

    public User createUser(User user) {
        // Validări de bază
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Username, email și parola sunt necesare");
        }

        // Verificăm dacă există username-ul sau email-ul
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username deja există");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email deja utilizat");
        }

        // Salvăm utilizatorul în funcție de tip
        return switch (user.getUserType()) {
            case STUDENT -> {
                if (!(user instanceof Student student)) {
                    throw new IllegalArgumentException("Tipul de utilizator nu este Student");
                }
                yield studentRepository.save(student);
            }
            case PARENT -> {
                if (!(user instanceof Parent parent)) {
                    throw new IllegalArgumentException("Tipul de utilizator nu este Parent");
                }
                yield parentRepository.save(parent);
            }
            case TEACHER -> {
                if (!(user instanceof Teacher teacher)) {
                    throw new IllegalArgumentException("Tipul de utilizator nu este Teacher");
                }
                yield teacherRepository.save(teacher);
            }
            case ADMIN -> {
                if (!(user instanceof Admin admin)) {
                    throw new IllegalArgumentException("Tipul de utilizator nu este Admin");
                }
                yield userRepository.save(admin);
            }
            case CHEF -> {
                if (!(user instanceof Chef chef)) {
                    throw new IllegalArgumentException("Tipul de utilizator nu este Chef");
                }
                yield userRepository.save(chef);
            }
            default -> throw new IllegalArgumentException("Tip de utilizator invalid");
        };
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void forceUpdatePassword(User user) {
        userRepository.save(user);
    }

}
