package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.services.ClassSessionService;
import com.cafeteria.cafeteria_plugin.services.GradeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;
    private final ClassSessionService classSessionService;

    public GradeController(GradeService gradeService, ClassSessionService classSessionService) {
        this.gradeService = gradeService;
        this.classSessionService = classSessionService;
    }

    // Endpoint to add a grade
    @PostMapping
    public ResponseEntity<Grade> addGrade(
            @RequestParam Long studentId,
            @RequestParam Long teacherId,
            @RequestParam Double gradeValue) {
        try {
            Grade savedGrade = gradeService.addGrade(studentId, teacherId, gradeValue);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint to get all grades
    @GetMapping
    public List<Grade> getAllGrades() {
        return gradeService.getAllGrades();
    }

    // Endpoint to get a grade by ID
    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
        Optional<Grade> grade = gradeService.getGradeById(id);
        return grade.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint to update a grade by ID
    @PutMapping("/{id}")
    public ResponseEntity<Grade> updateGrade(
            @PathVariable Long id,
            @RequestParam Long studentId,
            @RequestParam Long teacherId,
            @RequestParam Double gradeValue) {
        try {
            Grade updatedGrade = gradeService.updateGrade(id, studentId, teacherId, gradeValue);
            return ResponseEntity.ok(updatedGrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint to delete a grade by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/session/{sessionId}")
    public ResponseEntity<Grade> addGradeToSession(@PathVariable Long sessionId, @RequestBody Grade grade) {
        ClassSession session = classSessionService.getSessionById(sessionId);
        grade.setClassSession(session); // Associate the session with the grade
        Grade savedGrade = gradeService.addGrade(grade); // Save the grade
        return ResponseEntity.ok(savedGrade);
    }
}
