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
        // Validări comune
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username și parola sunt necesare");
        }

        // Verificăm dacă username-ul există deja
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username deja există");
        }

        // Creăm utilizator în funcție de tip
        if ("student".equals(user.getUserType())) {
            return createStudentUser(user);
        }

        throw new IllegalArgumentException("Tip de utilizator invalid");
    }

    private User createStudentUser(User user) {
        if (user.getStudent() == null || user.getStudent().getStudentClass() == null ||
                user.getStudent().getStudentClass().getId() == null) {
            throw new IllegalArgumentException("ID-ul clasei este necesar pentru student.");
        }

        Long classId = user.getStudent().getStudentClass().getId();
        Class studentClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu există"));

        Student student = new Student();
        student.setName(user.getName());
        student.setEmail(user.getEmail());
        student.setPhoneNumber(user.getPhoneNumber());
        student.setStudentClass(studentClass);

        // Setăm părintele studentului, fără relație inversă
        if (user.getStudent().getParent() != null) {
            Parent parent = user.getStudent().getParent();
            student.setParent(parent); // Asociem părintele cu studentul
        }

        student = studentRepository.save(student); // Salvăm studentul
        user.setStudent(student);

        return userRepository.save(user); // Salvăm utilizatorul
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
