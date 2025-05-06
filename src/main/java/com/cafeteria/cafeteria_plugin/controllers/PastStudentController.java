package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.PastStudent;
import com.cafeteria.cafeteria_plugin.services.PastStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/past-students")
public class PastStudentController {

    @Autowired
    PastStudentService pastStudentService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<PastStudent> getAllPastStudents() {
        return pastStudentService.getAllPastStudents();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPastStudentById(@PathVariable Long id) {
        PastStudent student = pastStudentService.getPastStudentById(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Nu există un absolvent cu acest ID.");
        }
        return ResponseEntity.ok(student);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PastStudent> addPastStudent(@RequestBody PastStudent pastStudent) {
        PastStudent savedStudent = pastStudentService.savePastStudent(pastStudent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePastStudent(@PathVariable Long id) {
        try {
            pastStudentService.deletePastStudent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la ștergere: " + e.getMessage());
        }
    }
}

