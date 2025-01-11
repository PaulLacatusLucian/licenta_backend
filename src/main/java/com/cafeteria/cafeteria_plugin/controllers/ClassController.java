package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import org.springframework.http.ResponseEntity;
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

    // Adaugă o clasă și asociază un profesor prin ID
    @PostMapping
    public ResponseEntity<Class> addClass(@RequestBody Class studentClass, @RequestParam Long teacherId) {
        Teacher teacher = classService.findTeacherById(teacherId);
        studentClass.setClassTeacher(teacher);
        return ResponseEntity.ok(classService.addClass(studentClass));
    }

    // Obține toate clasele
    @GetMapping
    public List<Class> getAllClasses() {
        return classService.getAllClasses();
    }

    // Obține o clasă după ID
    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable Long id) {
        Optional<Class> studentClass = classService.getClassById(id);
        if (studentClass.isPresent()) {
            return ResponseEntity.ok(studentClass.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizează o clasă și (opțional) profesorul asociat
    @PutMapping("/{id}")
    public ResponseEntity<Class> updateClass(@PathVariable Long id, @RequestBody Class studentClass, @RequestParam(required = false) Long teacherId) {
        if (teacherId != null) {
            Teacher teacher = classService.findTeacherById(teacherId);
            studentClass.setClassTeacher(teacher);
        }
        return ResponseEntity.ok(classService.updateClass(id, studentClass));
    }

    // Șterge o clasă după ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
