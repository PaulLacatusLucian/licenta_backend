package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.mappers.GradeMapper;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.ClassSessionRepository;
import com.cafeteria.cafeteria_plugin.repositories.GradeRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassSessionRepository classSessionRepository;

    // Neue Note speichern
    public Grade addGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    // Alle Noten abrufen
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    // Einzelne Note nach ID abrufen
    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    // Vorhandene Note aktualisieren
    public Grade updateGrade(Long id, Grade updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Die Note mit der angegebenen ID existiert nicht."));

        existingGrade.setGrade(updatedGrade.getGrade());
        existingGrade.setStudent(updatedGrade.getStudent());
        existingGrade.setClassSession(updatedGrade.getClassSession());
        existingGrade.setDescription(updatedGrade.getDescription());

        return gradeRepository.save(existingGrade);
    }

    // Note löschen
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    // Note speichern (alternativ)
    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    // Alle Noten eines bestimmten Schülers abrufen, konvertiert in DTOs
    public List<GradeDTO> getGradesByStudent(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }

    // Schüler nach ID abrufen
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden."));
    }

    // Überprüfen, ob eine Note bereits für die gegebene Unterrichtseinheit existiert
    public boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId) {
        return gradeRepository.existsByStudentIdAndClassSessionId(studentId, classSessionId);
    }

    // Note nach ID suchen
    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }
}
