package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Semester;
import com.cafeteria.cafeteria_plugin.services.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/semesters")
public class SemesterController {

    @Autowired
    SemesterService semesterService;


    @GetMapping("/current")
    public ResponseEntity<Semester> getCurrentSemester() {
        Semester currentSemester = semesterService.getCurrentSemester();
        return ResponseEntity.ok(currentSemester);
    }


    @PostMapping("/next")
    public ResponseEntity<Semester> incrementSemester() {
        Semester updatedSemester = semesterService.incrementSemester();
        return ResponseEntity.ok(updatedSemester);
    }
}

