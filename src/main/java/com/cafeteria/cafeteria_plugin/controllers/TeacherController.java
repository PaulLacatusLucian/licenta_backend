package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.mappers.TeacherMapper;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.User.UserType;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeacherMapper teacherMapper;

    // ✅ Doar ADMIN poate adăuga profesori
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Teacher> addTeacher(@RequestBody Teacher teacher) {
        // Generăm username și criptăm parola
        String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", ".");
        teacher.setUsername(baseUsername + ".prof");
        teacher.setPassword(passwordEncoder.encode(baseUsername.replace(".", "_") + "123!"));

        // Setăm userType corect
        teacher.setUserType(UserType.TEACHER);

        return ResponseEntity.ok(teacherService.addTeacher(teacher));
    }

    @GetMapping("/teachers/available")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TeacherDTO> getAvailableTeachers() {
        return teacherService.findAvailableTeachers().stream()
                .map(TeacherMapper::toDto)
                .toList();
    }

    // ✅ Doar ADMIN sau TEACHER poate vedea profesorii
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    // ✅ Doar ADMIN sau TEACHER poate vedea detaliile unui profesor
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    // ✅ Doar ADMIN poate actualiza profesorii
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }

    // ✅ Doar ADMIN poate șterge un profesor
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

    // ✅ Doar TEACHER și ADMIN pot vedea elevii profesorului
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsForTeacher(@PathVariable Long id) {
        try {
            List<Student> students = teacherService.getStudentsForTeacher(id);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Doar TEACHER și ADMIN pot vedea orarul săptămânal
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}/weekly-schedule")
    public ResponseEntity<List<Schedule>> getWeeklySchedule(@PathVariable Long id) {
        try {
            List<Schedule> weeklySchedule = teacherService.getWeeklyScheduleForTeacher(id);
            return ResponseEntity.ok(weeklySchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ✅ Doar TEACHER și ADMIN pot vedea sesiunile profesorului
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<ClassSession>> getSessionsForTeacher(@PathVariable Long id) {
        try {
            List<ClassSession> sessions = teacherService.getSessionsForTeacher(id);
            return ResponseEntity.ok(sessions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-type")
    public ResponseEntity<List<Teacher>> getTeachersByType(@RequestParam TeacherType type) {
        return ResponseEntity.ok(
                teacherService.getAllTeachers().stream()
                        .filter(t -> t.getType() == type)
                        .toList()
        );
    }

}
