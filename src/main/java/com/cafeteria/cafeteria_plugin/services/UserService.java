package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    public UserService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       ClassRepository classRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
    }

    @Transactional
    public User createUser(User user) {
        // Validare input
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username și parola sunt necesare");
        }

        // Verifică dacă username există
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username deja există");
        }

        switch (user.getUserType()) {
            case "student":
                return createStudentUser(user);
            default:
                throw new IllegalArgumentException("Tip de utilizator invalid");
        }
    }

    private User createStudentUser(User user) {
        // Preia clasa studentului dacă există
        if (user.getStudent() == null || user.getStudent().getStudentClass() == null) {
            throw new IllegalArgumentException("Clasa studentului este necesară");
        }

        Long classId = user.getStudent().getStudentClass().getId();
        Class studentClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu a fost găsită"));

        // Creează obiectul Student
        Student student = new Student();
        student.setName(user.getName());
        student.setEmail(user.getEmail());
        student.setPhoneNumber(user.getPhoneNumber());
        student.setStudentClass(studentClass); // Asociază clasa

        // Salvează studentul
        student = studentRepository.save(student);

        // Asociază studentul cu utilizatorul
        user.setStudent(student);

        // Salvează utilizatorul
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
