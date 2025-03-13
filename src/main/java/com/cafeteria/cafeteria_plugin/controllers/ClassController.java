package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Class> addClass(@RequestBody Class studentClass, @RequestParam Long teacherId) {
        Teacher teacher = classService.findTeacherById(teacherId);
        studentClass.setClassTeacher(teacher);
        return ResponseEntity.ok(classService.addClass(studentClass));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Class> getAllClasses() {
        return classService.getAllClasses();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable Long id) {
        Optional<Class> studentClass = classService.getClassById(id);
        return studentClass.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Class> updateClass(@PathVariable Long id,
                                             @RequestBody Class studentClass,
                                             @RequestParam(required = false) Long teacherId) {

        Optional<Class> existingClassOpt = classService.getClassById(id);
        if (existingClassOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Class existingClass = existingClassOpt.get();

        existingClass.setName(studentClass.getName());
        existingClass.setSpecialization(studentClass.getSpecialization());

        if (teacherId != null) {
            Teacher teacher = classService.findTeacherById(teacherId);
            existingClass.setClassTeacher(teacher);
        }

        return ResponseEntity.ok(classService.updateClass(id, existingClass));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
