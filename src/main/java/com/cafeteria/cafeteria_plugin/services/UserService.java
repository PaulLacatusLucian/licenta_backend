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
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;

    public UserService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       ClassRepository classRepository,
                       ParentRepository parentRepository,
                       TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
        this.parentRepository = parentRepository;
        this.teacherRepository = teacherRepository;
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
        if (user instanceof Student) {
            return createStudentUser((Student) user);
        } else if (user instanceof Parent) {
            return createParentUser((Parent) user);
        } else if (user instanceof Teacher) {
            return createTeacherUser((Teacher) user);
        }

        throw new IllegalArgumentException("Tip de utilizator invalid");
    }

    private Student createStudentUser(Student student) {
        // Validare: Clasa este necesară
        if (student.getStudentClass() == null || student.getStudentClass().getId() == null) {
            throw new IllegalArgumentException("ID-ul clasei este necesar pentru student.");
        }

        // Găsim clasa în baza de date
        Long classId = student.getStudentClass().getId();
        Class studentClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu există"));

        // Setăm clasa studentului
        student.setStudentClass(studentClass);

        // Setăm părintele studentului, dacă există
        if (student.getParent() != null) {
            Parent parent = student.getParent();
            student.setParent(parent); // Asociem părintele cu studentul
        }

        // Salvăm studentul
        return studentRepository.save(student);
    }

    private Parent createParentUser(Parent parent) {
        // Validări pentru părinte
        if (parent.getUsername() == null || parent.getPassword() == null) {
            throw new IllegalArgumentException("Username și parola sunt necesare pentru părinte.");
        }

        // Salvăm părintele
        return parentRepository.save(parent);
    }

    private Teacher createTeacherUser(Teacher teacher) {
        // Validări pentru profesor
        if (teacher.getUsername() == null || teacher.getPassword() == null) {
            throw new IllegalArgumentException("Username și parola sunt necesare pentru profesor.");
        }

        // Salvăm profesorul
        return teacherRepository.save(teacher);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
