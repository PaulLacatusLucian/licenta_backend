package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/year")
public class YearController {

    private final StudentService studentService;

    public YearController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Endpoint pentru avansarea anului școlar
     */
    @PostMapping("/start-new-year")
    public ResponseEntity<String> startNewYear() {
        try {
            studentService.advanceYear();
            return ResponseEntity.ok("Anul școlar a fost avansat cu succes!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare la avansarea anului școlar: " + e.getMessage());
        }
    }
}
