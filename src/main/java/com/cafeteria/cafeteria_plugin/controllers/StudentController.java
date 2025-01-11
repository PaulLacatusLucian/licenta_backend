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

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student studentDetails, @RequestParam Long classId) {
        try {
            Student savedStudent = studentService.saveStudentWithClass(studentDetails, classId);
            return ResponseEntity.ok(savedStudent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pentru obținerea absențelor unui student
    @GetMapping("/{id}/absences")
    public ResponseEntity<List<Absence>> getStudentAbsences(@PathVariable Long id) {
        List<Absence> absences = studentService.getAbsencesByStudentId(id);

        if (absences.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Returnează o listă goală dacă nu există absențe
        } else {
            return ResponseEntity.ok(absences); // Returnează lista de absențe
        }
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // Endpoint pentru numărul total de absențe
    @GetMapping("/{id}/total_absences")
    public ResponseEntity<Integer> getStudentTotalAbsences(@PathVariable Long id) {
        List<Absence> absences = studentService.getAbsencesByStudentId(id);

        if (absences.isEmpty()) {
            return ResponseEntity.ok(0); // Returnează 0 dacă nu există absențe
        } else {
            int totalAbsences = absences.stream()
                    .mapToInt(Absence::getCount) // Extrage numărul de absențe
                    .sum(); // Calculează suma totală
            return ResponseEntity.ok(totalAbsences); // Returnează totalul absențelor
        }
    }

    // Endpoint pentru cursurile viitoare ale studentului
    @GetMapping("/{id}/upcoming-classes")
    public ResponseEntity<List<Class>> getStudentUpcomingClasses(@PathVariable Long id) {
        List<Class> upcomingClasses = studentService.getUpcomingClasses(id);
        return ResponseEntity.ok(upcomingClasses); // Returnează lista cursurilor viitoare
    }


}
