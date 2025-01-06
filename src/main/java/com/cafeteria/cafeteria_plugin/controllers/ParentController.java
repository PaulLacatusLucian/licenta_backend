package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/parents")
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;

    public ParentController(ParentService parentService, StudentService studentService) {
        this.parentService = parentService;
        this.studentService = studentService;
    }

    @GetMapping
    public List<Parent> getAllParents() {
        return parentService.getAllParents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parent> getParentById(@PathVariable Long id) {
        Optional<Parent> parent = parentService.getParentById(id);
        return parent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Parent> updateParent(@PathVariable Long id, @RequestBody Parent parent) {
        return ResponseEntity.ok(parentService.updateParent(id, parent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/student")
    public ResponseEntity<?> getStudentForParent(@PathVariable Long id) {
        Optional<Parent> parent = parentService.getParentById(id);
        if (parent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parent not found");
        }

        Parent parentEntity = parent.get();
        Optional<Student> student = studentService.getStudentByParentId(parentEntity.getId());
        if (student.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No student associated with this parent");
        }

        return ResponseEntity.ok(student.get());
    }
}
