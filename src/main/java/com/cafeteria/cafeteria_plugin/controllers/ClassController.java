package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Class;
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

    @PostMapping
    public ResponseEntity<Class> addClass(@RequestBody Class studentClass) {
        return ResponseEntity.ok(classService.addClass(studentClass));
    }

    @GetMapping
    public List<Class> getAllClasses() {
        return classService.getAllClasses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable Long id) {
        Optional<Class> studentClass = classService.getClassById(id);
        if (studentClass.isPresent()) {
            System.out.println("Found class with ID: " + id);
            return ResponseEntity.ok(studentClass.get());
        } else {
            System.out.println("Class not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Class> updateClass(@PathVariable Long id, @RequestBody Class studentClass) {
        return ResponseEntity.ok(classService.updateClass(id, studentClass));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
