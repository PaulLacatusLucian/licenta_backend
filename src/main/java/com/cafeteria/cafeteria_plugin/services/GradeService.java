package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.repositories.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    @Autowired
    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    // Add a new grade
    public Grade addGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    // Get all grades
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    // Get a grade by ID
    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    // Update a grade
    public Grade updateGrade(Long id, Grade updatedGrade) {
        updatedGrade.setId(id);
        return gradeRepository.save(updatedGrade);
    }

    // Delete a grade by ID
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }
}
