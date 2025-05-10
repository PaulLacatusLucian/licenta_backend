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

    public Grade addGrade(Grade grade) {
        return gradeRepository.save(grade);
    }


    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    public Grade updateGrade(Long id, Grade updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota cu ID-ul specificat nu există."));

        existingGrade.setGrade(updatedGrade.getGrade());
        existingGrade.setStudent(updatedGrade.getStudent());
        existingGrade.setClassSession(updatedGrade.getClassSession());
        existingGrade.setDescription(updatedGrade.getDescription());

        return gradeRepository.save(existingGrade);
    }

    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public List<GradeDTO> getGradesByStudent(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    public boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId) {
        return gradeRepository.existsByStudentIdAndClassSessionId(studentId, classSessionId);
    }

    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }
}
