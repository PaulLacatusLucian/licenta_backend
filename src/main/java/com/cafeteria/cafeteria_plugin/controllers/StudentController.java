package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Endpoint pentru crearea unui student și asocierea acestuia cu o clasă
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student studentDetails, @RequestParam Long classId) {
        try {
            Student savedStudent = studentService.saveStudentWithClass(studentDetails, classId);
            return ResponseEntity.ok(savedStudent); // Returnează studentul salvat
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Eroare dacă clasa nu există
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Returnează 404 dacă studentul nu există
    }

    // Endpoint pentru obținerea absențelor unui student
    @GetMapping("/{id}/absences")
    public ResponseEntity<List<Absence>> getStudentAbsences(@PathVariable Long id) {
        List<Absence> absences = studentService.getAbsencesByStudentId(id);

        if (absences.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Returnează 200 OK cu listă goală dacă nu sunt absențe
        } else {
            return ResponseEntity.ok(absences); // Returnează absențele dacă sunt
        }
    }

    @GetMapping("/{id}/total_absences")
    public ResponseEntity<Integer> getStudentTotalAbsences(@PathVariable Long id) {
        List<Absence> absences = studentService.getAbsencesByStudentId(id);

        if (absences.isEmpty()) {
            return ResponseEntity.ok(0); // Returnează 200 OK cu 0 absente
        } else {
            // Altfel, adunăm toate absențele
            int totalAbsences = absences.stream()
                    .mapToInt(Absence::getCount)  // Extrage numărul de absențe din fiecare obiect Absence
                    .sum();  // Suma totală a absențelor

            return ResponseEntity.ok(totalAbsences); // Returnează 200 OK cu numărul total de absențe
        }
    }




    @GetMapping("/{id}/upcoming-classes")
    public ResponseEntity<List<Class>> getStudentUpcomingClasses(@PathVariable Long id) {
        List<Class> upcomingClasses = studentService.getUpcomingClasses(id);

        return ResponseEntity.ok(upcomingClasses); // Returnează 200 OK cu lista cursurilor viitoare
    }
}
