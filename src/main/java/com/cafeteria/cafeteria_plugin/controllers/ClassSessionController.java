package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import com.cafeteria.cafeteria_plugin.services.ClassSessionService;
import com.cafeteria.cafeteria_plugin.services.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/class-sessions")
public class ClassSessionController {

    @Autowired
    private ClassSessionService classSessionService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private AbsenceService absenceService;

    @PostMapping
    public ResponseEntity<ClassSession> addClassSession(@RequestBody ClassSession classSession) {
        ClassSession savedSession = classSessionService.addClassSession(classSession);
        return ResponseEntity.ok(savedSession);
    }

    @GetMapping
    public ResponseEntity<List<ClassSession>> getAllClassSessions() {
        List<ClassSession> sessions = classSessionService.getAllClassSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassSession>> getSessionsByTeacher(@PathVariable Long teacherId) {
        List<ClassSession> sessions = classSessionService.getSessionsByTeacher(teacherId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<ClassSession>> getSessionsBySubject(@PathVariable String subject) {
        List<ClassSession> sessions = classSessionService.getSessionsBySubject(subject);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/time")
    public ResponseEntity<List<ClassSession>> getSessionsByTimeInterval(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<ClassSession> sessions = classSessionService.getSessionsByTimeInterval(start, end);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassSession(@PathVariable Long id) {
        classSessionService.deleteClassSession(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/session/{sessionId}")
    public ResponseEntity<Grade> addGradeToSession(
            @PathVariable Long sessionId,
            @RequestParam Long studentId,
            @RequestParam double gradeValue) {
        try {
            // Fetch class session
            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Fetch student
            Student student = gradeService.getStudentById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Create and save grade
            Grade grade = new Grade();
            grade.setClassSession(session);
            grade.setStudent(student);
            grade.setGrade(gradeValue);
            grade.setTeacher(session.getTeacher()); // Set teacher from session

            Grade savedGrade = gradeService.addGrade(grade);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/session/{sessionId}/absences")
    public ResponseEntity<Absence> addAbsenceToSession(
            @PathVariable Long sessionId,
            @RequestParam Long studentId) { // Adăugăm teacherId
        try {
            // Preluăm sesiunea
            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Preluăm studentul
            Student student = gradeService.getStudentById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Preluăm profesorul care înregistrează absența
            Teacher teacher = session.getTeacher(); // Creează metoda dacă nu există
            if (teacher == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Creăm absența
            Absence absence = new Absence();
            absence.setClassSession(session);
            absence.setStudent(student);
            absence.setDate(session.getStartTime().toLocalDate()); // Data este preluată din sesiunea selectată
            absence.setSubject(session.getSubject()); // Setăm materia din sesiune
            absence.setTeacher(teacher); // Setăm profesorul care înregistrează absența

            // Salvăm absența
            Absence savedAbsence = absenceService.saveAbsence(absence);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedAbsence);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




}
