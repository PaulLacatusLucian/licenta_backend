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
import com.cafeteria.cafeteria_plugin.services.*;
import jdk.jfr.Unsigned;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
}
