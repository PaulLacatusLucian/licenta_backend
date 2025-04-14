package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.ClassSessionDTO;
import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.mappers.ScheduleMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private JwtUtil jwtUtil;


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/class/{classId}")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody Student studentDetails, @PathVariable Long classId) {
        Student savedStudent = studentService.saveStudentWithClass(studentDetails, classId);
        StudentDTO dto = studentMapper.toDTO(savedStudent);
        return ResponseEntity.ok(dto);
    }


    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        StudentDTO dto = studentMapper.toDTO(student);
        return ResponseEntity.ok(dto);
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> studentDTOs = studentService.getAllStudents().stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }


    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student savedStudent = studentService.updateStudent(id, updatedStudent);
        StudentDTO dto = studentMapper.toDTO(savedStudent);
        return ResponseEntity.ok(dto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<StudentDTO> getCurrentStudent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);

        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(studentMapper.toDTO(student));
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/total-absences")
    public ResponseEntity<Map<String, Integer>> getTotalAbsencesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        int total = absenceService.getTotalAbsencesForStudent(student.getId());
        return ResponseEntity.ok(Map.of("total", total));
    }


    @GetMapping("/me/upcoming-classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ScheduleDTO>> getUpcomingClassesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        List<Schedule> schedules = studentService.getUpcomingSchedules(student.getId());
        List<ScheduleDTO> dtos = schedules.stream().map(scheduleMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }
}
