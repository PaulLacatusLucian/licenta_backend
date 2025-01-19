package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.services.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // Adaugă un profesor
    @PostMapping
    public ResponseEntity<Teacher> addTeacher(@RequestBody Teacher teacher) {
        // Generăm username și parola
        String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", ".");
        teacher.setUsername(baseUsername + ".prof");
        teacher.setPassword(baseUsername.replace(".", "_") + "123!");

        // Setăm userType
        teacher.setUserType("teacher");

        return ResponseEntity.ok(teacherService.addTeacher(teacher));
    }

    // Obține toți profesorii
    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    // Obține un profesor după ID
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    // Actualizează un profesor
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }

    // Șterge un profesor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        try {
            teacherService.deleteTeacher(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsForTeacher(@PathVariable Long id) {
        try {
            List<Student> students = teacherService.getStudentsForTeacher(id);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/weekly-schedule")
    public ResponseEntity<List<Schedule>> getWeeklySchedule(@PathVariable Long id) {
        try {
            List<Schedule> weeklySchedule = teacherService.getWeeklyScheduleForTeacher(id);
            return ResponseEntity.ok(weeklySchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<ClassSession>> getSessionsForTeacher(@PathVariable Long id) {
        try {
            List<ClassSession> sessions = teacherService.getSessionsForTeacher(id);
            return ResponseEntity.ok(sessions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
