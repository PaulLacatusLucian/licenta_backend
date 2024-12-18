package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Class;
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
    private AbsenceRepository absenceRepository; // Adaugă repository-ul Absence

    @Transactional
    public Student saveStudentWithClass(Student studentDetails, Long classId) {
        // Caută clasa asociată
        Class studentClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu există"));

        // Asociază clasa studentului
        studentDetails.setStudentClass(studentClass);

        // Salvează studentul
        return studentRepository.save(studentDetails);
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // Modifică această metodă pentru a obține absențele din AbsenceRepository
    public List<Absence> getAbsencesByStudentId(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    public List<Class> getUpcomingClasses(Long studentId) {
        // Înlocuiește cu logica ta pentru cursurile viitoare
        return Collections.emptyList(); // Deocamdată returnează o listă goală
    }
}
