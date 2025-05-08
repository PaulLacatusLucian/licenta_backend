package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.CatalogEntry;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.CatalogService;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
    @Autowired
    private CatalogService catalogService;

    @Autowired
    private ClassService classService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<CatalogEntry>> getCatalogForClass(@PathVariable Long classId) {
        List<CatalogEntry> entries = catalogService.getCatalogEntriesForClass(classId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('PARENT')")
    public ResponseEntity<List<CatalogEntry>> getCatalogForStudent(@PathVariable Long studentId) {
        List<CatalogEntry> entries = catalogService.getStudentEntries(studentId);
        return ResponseEntity.ok(entries);
    }
}