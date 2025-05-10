package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.mappers.GradeMapper;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.ClassSessionService;
import com.cafeteria.cafeteria_plugin.services.GradeService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;
    @Autowired
    private ClassSessionService classSessionService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<GradeDTO> addGradeToSession(@PathVariable Long sessionId,
                                                      @RequestParam Long studentId,
                                                      @RequestParam Double gradeValue,
                                                      @RequestParam(required = false) String description) {
        ClassSession session = classSessionService.getSessionById(sessionId);
        Student student = studentService.getStudentById(studentId);

        Grade grade = new Grade();
        grade.setClassSession(session);
        grade.setStudent(student);
        grade.setGrade(gradeValue);
        grade.setDescription(description);

        Grade saved = gradeService.addGrade(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeMapper.toDto(saved));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        List<GradeDTO> dtos = gradeService.getAllGrades()
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable Long id,
                                                @RequestParam Long studentId,
                                                @RequestParam Long sessionId,
                                                @RequestParam Double gradeValue,
                                                @RequestParam(required = false) String description) {
        ClassSession session = classSessionService.getSessionById(sessionId);
        Student student = studentService.getStudentById(studentId);

        Grade updated = new Grade();
        updated.setId(id);
        updated.setStudent(student);
        updated.setClassSession(session);
        updated.setGrade(gradeValue);
        updated.setDescription(description);

        Grade saved = gradeService.updateGrade(id, updated);
        return ResponseEntity.ok(gradeMapper.toDto(saved));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDTO>> getGradesByStudent(@PathVariable Long studentId) {
        List<GradeDTO> grades = gradeService.getGradesByStudent(studentId);
        return ResponseEntity.ok(grades);
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<List<GradeDTO>> getGradesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<GradeDTO> grades = gradeService.getGradesByStudent(student.getId());
        return ResponseEntity.ok(grades);
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}/simple")
    public ResponseEntity<GradeDTO> updateGradeSimple(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updateData) {

        try {
            // Obține nota ca Optional<Grade>
            Optional<Grade> gradeOptional = gradeService.findById(id);

            // Verifică dacă nota există
            if (gradeOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Extrage nota din Optional
            Grade existingGrade = gradeOptional.get();

            // Actualizăm doar valorile care au fost trimise
            if (updateData.containsKey("grade")) {
                double gradeValue;

                // Gestionare pentru diferite tipuri de date care pot veni de la frontend
                Object gradeObj = updateData.get("grade");
                if (gradeObj instanceof Number) {
                    gradeValue = ((Number) gradeObj).doubleValue();
                } else if (gradeObj instanceof String) {
                    try {
                        gradeValue = Double.parseDouble((String) gradeObj);
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest()
                                .body(null); // Sau un DTO de eroare
                    }
                } else {
                    return ResponseEntity.badRequest()
                            .body(null); // Sau un DTO de eroare
                }

                existingGrade.setGrade(gradeValue);
            }

            if (updateData.containsKey("description")) {
                existingGrade.setDescription((String) updateData.get("description"));
            }

            // Salvăm nota actualizată fără a modifica alte relații
            Grade updated = gradeService.updateGrade(id, existingGrade);
            return ResponseEntity.ok(gradeMapper.toDto(updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Sau un DTO de eroare
        }
    }
}
