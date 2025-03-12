package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AbsenceService absenceService;

    // ✅ Doar TEACHER sau ADMIN poate adăuga studenți
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping("/class/{classId}")
    public ResponseEntity<Student> createStudent(@RequestBody Student studentDetails, @PathVariable Long classId) {
        Student savedStudent = studentService.saveStudentWithClass(studentDetails, classId);
        return ResponseEntity.ok(savedStudent);
    }

    // ✅ STUDENT poate vedea doar propriile date, TEACHER și ADMIN pot vedea orice student
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    // ✅ Doar TEACHER sau ADMIN poate vedea toți studenții
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // ✅ STUDENT poate edita propriile date, TEACHER și ADMIN pot edita orice student
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student savedStudent = studentService.updateStudent(id, updatedStudent);
        return ResponseEntity.ok(savedStudent);
    }

    // ✅ Doar ADMIN poate șterge studenți
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ STUDENT poate vedea doar propriile absențe, TEACHER și ADMIN pot vedea orice absență
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/{id}/absences")
    public ResponseEntity<List<Absence>> getStudentAbsences(@PathVariable Long id) {
        List<Absence> absences = studentService.getAbsencesByStudentId(id);
        return ResponseEntity.ok(absences);
    }

    // ✅ TEACHER și ADMIN pot vedea totalul absențelor
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/{id}/total-absences")
    public ResponseEntity<Integer> getStudentTotalAbsences(@PathVariable Long id) {
        int totalAbsences = absenceService.getTotalAbsencesForStudent(id);
        return ResponseEntity.ok(totalAbsences);
    }

    // ✅ STUDENT poate vedea doar propriile cursuri, TEACHER și ADMIN pot vedea orice curs
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/{id}/upcoming-classes")
    public ResponseEntity<List<Class>> getStudentUpcomingClasses(@PathVariable Long id) {
        List<Class> upcomingClasses = studentService.getUpcomingClasses(id);
        return ResponseEntity.ok(upcomingClasses);
    }
}
