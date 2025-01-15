package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.PastStudent;
import com.cafeteria.cafeteria_plugin.services.PastStudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/past-students")
public class PastStudentController {

    private final PastStudentService pastStudentService;

    public PastStudentController(PastStudentService pastStudentService) {
        this.pastStudentService = pastStudentService;
    }

    @GetMapping
    public List<PastStudent> getAllPastStudents() {
        return pastStudentService.getAllPastStudents();
    }

    @GetMapping("/{id}")
    public PastStudent getPastStudentById(@PathVariable Long id) {
        return pastStudentService.getPastStudentById(id);
    }

    @PostMapping
    public ResponseEntity<PastStudent> addPastStudent(@RequestBody PastStudent pastStudent) {
        PastStudent savedStudent = pastStudentService.savePastStudent(pastStudent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePastStudent(@PathVariable Long id) {
        pastStudentService.deletePastStudent(id);
        return ResponseEntity.noContent().build();
    }
}
