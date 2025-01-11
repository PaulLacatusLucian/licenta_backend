package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.ClassRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cafeteria.cafeteria_plugin.repositories.AbsenceRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AbsenceRepository absenceRepository; // Repository pentru absențe

    @Transactional
    public Student saveStudentWithClass(Student studentDetails, Long classId) {
        // Caută clasa asociată
        Class studentClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu există"));

        // Asociază clasa studentului
        studentDetails.setStudentClass(studentClass);

        // Asociază părintele doar în cadrul studentului
        Parent parent = studentDetails.getParent();
        if (parent != null) {
            // Validări sau alte operațiuni asupra obiectului Parent, dacă sunt necesare
            // Nu mai setăm relația inversă
        }

        // Salvează studentul împreună cu părintele
        return studentRepository.save(studentDetails);
    }

    public Optional<Student> getStudentById(Long id) {
        // Caută studentul în baza de date și returnează inclusiv părintele
        return studentRepository.findById(id);
    }

    // Obține absențele studentului folosind AbsenceRepository
    public List<Absence> getAbsencesByStudentId(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    // Exemplu pentru cursurile viitoare (poate fi extins ulterior)
    public List<Class> getUpcomingClasses(Long studentId) {
        // Înlocuiește cu logica ta pentru cursurile viitoare
        return Collections.emptyList(); // Deocamdată returnează o listă goală
    }

    public Optional<Student> getStudentByParentId(Long parentId) {
        return studentRepository.findByParentId(parentId);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}

