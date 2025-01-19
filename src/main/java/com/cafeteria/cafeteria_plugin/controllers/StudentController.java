package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AbsenceService absenceService;

    // CRUD pentru studenți
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student studentDetails, @RequestParam Long classId) {
        Student savedStudent = studentService.saveStudentWithClass(studentDetails, classId);
        return ResponseEntity.ok(savedStudent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student savedStudent = studentService.updateStudent(id, updatedStudent);
        return ResponseEntity.ok(savedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint-uri legate de absențe
    @GetMapping("/{id}/absences")
    public ResponseEntity<List<Absence>> getStudentAbsences(@PathVariable Long id) {
        List<Absence> absences = studentService.getAbsencesByStudentId(id);
        return ResponseEntity.ok(absences);
    }

    @GetMapping("/{id}/total-absences")
    public ResponseEntity<Integer> getStudentTotalAbsences(@PathVariable Long id) {
        int totalAbsences = absenceService.getTotalAbsencesForStudent(id);
        return ResponseEntity.ok(totalAbsences);
    }

    // Endpoint-uri legate de cursuri viitoare
    @GetMapping("/{id}/upcoming-classes")
    public ResponseEntity<List<Class>> getStudentUpcomingClasses(@PathVariable Long id) {
        List<Class> upcomingClasses = studentService.getUpcomingClasses(id);
        return ResponseEntity.ok(upcomingClasses);
    }
}
