package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parents")
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;

    public ParentController(ParentService parentService, StudentService studentService) {
        this.parentService = parentService;
        this.studentService = studentService;
    }

    // ✅ Doar ADMIN poate vedea toți părinții
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Parent>> getAllParents() {
        return ResponseEntity.ok(parentService.getAllParents());
    }

    // ✅ PARENT poate vedea doar propriul profil, ADMIN poate vedea orice părinte
    @PreAuthorize("hasAuthority('PARENT') or hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Parent> getParentById(@PathVariable Long id) {
        Parent parent = parentService.getParentById(id);
        return ResponseEntity.ok(parent);
    }

    // ✅ Doar ADMIN sau PARENT poate modifica datele unui părinte
    @PreAuthorize("hasAuthority('PARENT') or hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Parent> updateParent(@PathVariable Long id, @RequestBody Parent parent) {
        return ResponseEntity.ok(parentService.updateParent(id, parent));
    }

    // ✅ Doar ADMIN poate șterge un părinte
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('PARENT') or hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/{id}/student")
    public ResponseEntity<?> getStudentForParent(@PathVariable Long id) {
        Parent parent = parentService.getParentById(id);

        return studentService.getStudentByParentId(parent.getId())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No student associated with this parent")));
    }

}
