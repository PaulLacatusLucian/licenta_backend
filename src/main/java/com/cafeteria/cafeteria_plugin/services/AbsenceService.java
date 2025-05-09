package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.AbsenceRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AbsenceService {

    @Autowired
    private AbsenceRepository absenceRepository;
    @Autowired
    private StudentRepository studentRepository;


    // Adăugarea unei absențe
    public Absence addAbsence(Absence absence) {
        return absenceRepository.save(absence);
    }

    // Obținerea tuturor absențelor
    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    // Obținerea unei absențe după ID
    public Optional<Absence> getAbsenceById(Long id) {
        return absenceRepository.findById(id);
    }

    // Actualizarea unei absențe
    public Absence updateAbsence(Long id, Absence updatedAbsence) {
        return absenceRepository.findById(id)
                .map(existingAbsence -> {
                    existingAbsence.setClassSession(updatedAbsence.getClassSession());
                    existingAbsence.setStudent(updatedAbsence.getStudent());
                    return absenceRepository.save(existingAbsence);
                }).orElseThrow(() -> new IllegalArgumentException("Absence not found"));
    }

    // Ștergerea unei absențe
    public void deleteAbsence(Long id) {
        absenceRepository.deleteById(id);
    }

    // Calcularea numărului total de absențe pentru un student
    public int getTotalAbsencesForStudent(Long studentId) {
        return absenceRepository.countByStudentId(studentId);
    }

    // Validare și salvare absență
    public Absence saveAbsence(Absence absence) {
        if (absence.getStudent() == null || absence.getClassSession() == null) {
            throw new IllegalArgumentException("Student and Class Session are required fields.");
        }

        // Dacă vrei, poți păstra această verificare (dacă vrei să eviți duplicate)
        boolean exists = absenceRepository.existsByStudentIdAndClassSessionId(
                absence.getStudent().getId(),
                absence.getClassSession().getId()
        );

        if (exists) {
            throw new IllegalArgumentException("Absence already exists for this student in this class session.");
        }

        return absenceRepository.save(absence);
    }

    public List<Absence> getAbsencesForStudent(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    public boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId) {
        return absenceRepository.existsByStudentIdAndClassSessionId(studentId, classSessionId);
    }

    public Absence justifyAbsence(Absence absence) {
        absence.setJustified(true);
        return absenceRepository.save(absence);
    }

    public List<Absence> getUnjustifiedAbsencesForClass(Long classId) {
        // Obținem toți elevii din clasă
        List<Student> studentsInClass = studentRepository.findByStudentClassId(classId);

        if (studentsInClass.isEmpty()) {
            return new ArrayList<>();
        }

        // Obținem ID-urile elevilor
        List<Long> studentIds = studentsInClass.stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        // Returnăm absențele nemotivate pentru acești elevi
        return absenceRepository.findUnjustifiedAbsencesByStudentIds(studentIds);
    }
}
