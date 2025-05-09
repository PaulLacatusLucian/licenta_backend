package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.ClassSessionDTO;
import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.mappers.AbsenceMapper;
import com.cafeteria.cafeteria_plugin.mappers.ClassSessionMapper;
import com.cafeteria.cafeteria_plugin.mappers.GradeMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/class-sessions")
public class ClassSessionController {


    @Autowired
    private ClassSessionService classSessionService;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AbsenceService absenceService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private ClassSessionMapper classSessionMapper;
    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private AbsenceMapper absenceMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private ClassService classService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CatalogService catalogService;


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassSessionDTO> addClassSession(@RequestBody ClassSessionDTO dto) {
        Teacher teacher = teacherService.getTeacherById(dto.getTeacher().getId());
        ClassSession session = classSessionMapper.toEntity(dto);
        session.setTeacher(teacher);
        ClassSession saved = classSessionService.addClassSession(session);
        return ResponseEntity.ok(classSessionMapper.toDto(saved));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClassSessionDTO>> getAllClassSessions() {
        List<ClassSessionDTO> dtos = classSessionService.getAllClassSessions()
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassSessionDTO>> getSessionsByTeacher(@PathVariable Long teacherId) {
        List<ClassSessionDTO> dtos = classSessionService.getSessionsByTeacher(teacherId)
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<ClassSessionDTO>> getSessionsBySubject(@PathVariable String subject) {
        List<ClassSessionDTO> dtos = classSessionService.getSessionsBySubject(subject)
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/time")
    public ResponseEntity<List<ClassSessionDTO>> getSessionsByTimeInterval(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<ClassSessionDTO> dtos = classSessionService.getSessionsByTimeInterval(start, end)
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassSession(@PathVariable Long id) {
        classSessionService.deleteClassSession(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/session/{sessionId}/absences")
    public ResponseEntity<?> addAbsenceToSession(
            @PathVariable Long sessionId,
            @RequestParam Long studentId,
            @RequestHeader("Authorization") String token) {
        try {
            // Extrage profesorul din token
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Teacher teacher = teacherService.findByUsername(username);

            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
            }

            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }

            // Verificare: dacă are deja notă, nu poate fi marcat absent
            boolean hasGrade = gradeService.existsByStudentIdAndClassSessionId(studentId, sessionId);
            if (hasGrade) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Elevul are deja notă și nu poate fi marcat absent.");
            }

            // Verificare: student aparține de clasa pentru care e sesiunea
            if (!isStudentInSession(student, session)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Elevul nu este înscris la această sesiune.");
            }

            // MODIFICARE: Verifică direct dacă absența există deja
            boolean absenceExists = absenceService.existsByStudentIdAndClassSessionId(studentId, sessionId);
            if (absenceExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Elevul are deja o absență înregistrată pentru această sesiune.");
            }

            Absence absence = new Absence();
            absence.setClassSession(session);
            absence.setStudent(student);
            absence.setTeacher(teacher);

            try {
                // Salvăm absența pentru a obține ID-ul
                Absence savedAbsence = absenceService.saveAbsence(absence);
                AbsenceDTO dto = absenceMapper.toDto(savedAbsence);

                // Transmitem ID-ul absenței la adăugarea în catalog
                catalogService.addAbsenceEntry(student, session.getSubject(), false, savedAbsence.getId());

                return ResponseEntity.status(HttpStatus.CREATED).body(dto);
            } catch (IllegalArgumentException e) {
                // Această excepție este aruncată când absența există deja
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Elevul are deja o absență înregistrată pentru această sesiune.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la salvarea absenței: " + e.getMessage());
        }
    }

    @PostMapping("/session/{sessionId}/grades")
    public ResponseEntity<?> addGradeToSession(
            @PathVariable Long sessionId,
            @RequestParam Long studentId,
            @RequestParam double gradeValue) {
        try {
            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");

            Student student = studentService.getStudentById(studentId);
            if (student == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

            // Verificare: dacă studentul este absent, nu poate primi notă
            boolean isAbsent = absenceService.existsByStudentIdAndClassSessionId(studentId, sessionId);
            if (isAbsent) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Elevul este absent și nu poate primi notă.");
            }

            // Verificare: student aparține de clasa pentru care e sesiunea
            if (!isStudentInSession(student, session)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Elevul nu este înscris la această sesiune.");
            }

            Grade grade = new Grade();
            grade.setClassSession(session);
            grade.setStudent(student);
            grade.setGrade(gradeValue);
            // Data notei va fi cea a sesiunii

            Grade savedGrade = gradeService.addGrade(grade);
            GradeDTO dto = gradeMapper.toDto(savedGrade);

            // Adaugă nota și în catalog
            catalogService.addGradeEntry(student, session.getSubject(), gradeValue);

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la salvarea notei.");
        }
    }

    // Metodă pentru a verifica dacă studentul aparține sesiunii
    private boolean isStudentInSession(Student student, ClassSession session) {
        return student.getStudentClass().getSchedules().stream()
                .anyMatch(schedule -> schedule.getSubjects().contains(session.getSubject()) &&
                        schedule.getTeacher().getId().equals(session.getTeacher().getId()));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/session/{sessionId}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsForSession(@PathVariable Long sessionId) {
        try {
            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) {
                System.out.println("Session not found: " + sessionId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            System.out.println("Found session: " + sessionId + ", Subject: " + session.getSubject());

            // Debug info for Teacher
            Teacher teacher = session.getTeacher();
            System.out.println("Session Teacher: " + teacher.getName() + ", ID: " + teacher.getId());

            // Improved approach: Get all classes where this teacher teaches this subject
            List<Class> allClasses = classService.getAllClasses();

            // Filter classes more efficiently
            List<Class> relevantClasses = allClasses.stream()
                    .filter(cls -> {
                        if (cls.getSchedules() == null || cls.getSchedules().isEmpty()) {
                            return false;
                        }

                        // Check if any schedule matches our criteria
                        return cls.getSchedules().stream()
                                .anyMatch(schedule -> {
                                    // Fix: Proper null-check for subjects collection
                                    boolean hasSubject = schedule.getSubjects() != null &&
                                            schedule.getSubjects().contains(session.getSubject());

                                    // Fix: Properly compare teacher IDs
                                    boolean hasTeacher = schedule.getTeacher() != null &&
                                            schedule.getTeacher().getId().equals(teacher.getId());

                                    return hasSubject && hasTeacher;
                                });
                    })
                    .collect(Collectors.toList());

            System.out.println("Relevant classes found: " + relevantClasses.size());

            // If no relevant classes found, log that information
            if (relevantClasses.isEmpty()) {
                System.out.println("Warning: No classes found where teacher " +
                        teacher.getName() + " teaches subject " + session.getSubject());
            } else {
                for (Class cls : relevantClasses) {
                    System.out.println("Relevant class: " + cls.getName() + " (ID: " + cls.getId() + ")");
                }
            }

            // Get students more efficiently by directly filtering by class IDs
            List<Long> relevantClassIds = relevantClasses.stream()
                    .map(Class::getId)
                    .collect(Collectors.toList());

            // Get all students
            List<Student> allStudents = studentService.getAllStudents();

            // No relevant classes means no students
            if (relevantClassIds.isEmpty()) {
                System.out.println("No relevant classes found, returning empty student list");
                return ResponseEntity.ok(new ArrayList<>());
            }

            // Filter students by class ID
            List<Student> studentsInRelevantClasses = allStudents.stream()
                    .filter(student -> {
                        if (student.getStudentClass() == null) {
                            return false;
                        }

                        return relevantClassIds.contains(student.getStudentClass().getId());
                    })
                    .collect(Collectors.toList());

            System.out.println("Found " + studentsInRelevantClasses.size() + " students in relevant classes");

            // Convert to DTOs
            List<StudentDTO> studentDTOs = studentsInRelevantClasses.stream()
                    .map(studentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(studentDTOs);
        } catch (Exception e) {
            System.err.println("Error retrieving students for session: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
