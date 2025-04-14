package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.AddAbsenceRequestDTO;
import com.cafeteria.cafeteria_plugin.mappers.AbsenceMapper;
import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import com.cafeteria.cafeteria_plugin.services.ClassSessionService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/absences")
public class AbsenceController {

    @Autowired
    private AbsenceService absenceService;
    @Autowired
    private ClassSessionService classSessionService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AbsenceMapper absenceMapper;
    @Autowired
    private JwtUtil jwtUtil;


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<AbsenceDTO> addAbsenceToSession(@PathVariable Long sessionId, @RequestBody AddAbsenceRequestDTO request) {
        ClassSession session = classSessionService.getSessionById(sessionId);
        Student student = studentService.getStudentById(request.getStudentId());

        Absence absence = new Absence();
        absence.setStudent(student);
        absence.setClassSession(session);

        Absence saved = absenceService.addAbsence(absence);
        return ResponseEntity.status(HttpStatus.CREATED).body(absenceMapper.toDto(saved));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AbsenceDTO>> getAllAbsences() {
        List<AbsenceDTO> dtos = absenceService.getAllAbsences()
                .stream().map(absenceMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AbsenceDTO> getAbsenceById(@PathVariable Long id) {
        Optional<Absence> absence = absenceService.getAbsenceById(id);
        return absence.map(a -> ResponseEntity.ok(absenceMapper.toDto(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AbsenceDTO> updateAbsence(@PathVariable Long id, @RequestBody AddAbsenceRequestDTO request) {
        ClassSession session = classSessionService.getSessionById(request.getClassSessionId());
        Student student = studentService.getStudentById(request.getStudentId());

        Absence updated = new Absence();
        updated.setId(id);
        updated.setStudent(student);
        updated.setClassSession(session);

        Absence saved = absenceService.updateAbsence(id, updated);
        return ResponseEntity.ok(absenceMapper.toDto(saved));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<List<AbsenceDTO>> getAbsencesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        List<Absence> absences = absenceService.getAbsencesForStudent(student.getId());
        List<AbsenceDTO> dtoList = absences.stream().map(absenceMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}
