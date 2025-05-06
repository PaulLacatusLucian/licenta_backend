package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.mappers.ScheduleMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.mappers.TeacherMapper;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.User.UserType;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import com.cafeteria.cafeteria_plugin.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private JwtUtil jwtUtil;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Teacher> addTeacher(@RequestBody Teacher teacher) {
        String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", ".");
        teacher.setUsername(baseUsername + ".prof");
        teacher.setPassword(passwordEncoder.encode(baseUsername.replace(".", "_") + "123!"));

        teacher.setUserType(UserType.TEACHER);

        return ResponseEntity.ok(teacherService.addTeacher(teacher));
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        try {
            teacherService.deleteTeacher(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeacherDTO> getCurrentTeacher(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);

        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(teacherMapper.toDto(teacher));
    }


    @GetMapping("/me/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentDTO>> getMyStudents(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<Student> students = teacherService.getStudentsForTeacher(teacher.getId());
        List<StudentDTO> studentDTOs = students.stream()
                .map(studentMapper::toDTO)
                .toList();

        return ResponseEntity.ok(studentDTOs);
    }


    @GetMapping("/me/weekly-schedule")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ScheduleDTO>> getMySchedule(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<Schedule> schedule = teacherService.getWeeklyScheduleForTeacher(teacher.getId());
        List<ScheduleDTO> scheduleDTOs = schedule.stream()
                .map(scheduleMapper::toDto)
                .toList();
        return ResponseEntity.ok(scheduleDTOs);
    }


    @GetMapping("/me/sessions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ClassSession>> getMySessions(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<ClassSession> sessions = teacherService.getSessionsForTeacher(teacher.getId());
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/my-class/parent-emails")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<String>> getParentEmailsForOwnClass(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Teacher teacher = teacherService.findByUsername(username);

        if (teacher.getClassAsTeacher() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<String> emails = parentService.getParentEmailsByClassId(teacher.getClassAsTeacher().getId());
        return ResponseEntity.ok(emails);
    }
}
