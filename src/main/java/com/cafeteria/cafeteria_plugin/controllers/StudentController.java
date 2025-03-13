package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private StudentMapper studentMapper;

    // ✅ Doar TEACHER sau ADMIN poate adăuga studenți
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/class/{classId}")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody Student studentDetails, @PathVariable Long classId) {
        Student savedStudent = studentService.saveStudentWithClass(studentDetails, classId);
        StudentDTO dto = studentMapper.toDTO(savedStudent);
        return ResponseEntity.ok(dto);
    }

    // ✅ STUDENT poate vedea doar propriile date, TEACHER și ADMIN pot vedea orice student
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        StudentDTO dto = studentMapper.toDTO(student);
        return ResponseEntity.ok(dto);
    }

    // ✅ TEACHER și ADMIN pot vedea toți studenții
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> studentDTOs = studentService.getAllStudents().stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    // ✅ STUDENT poate edita propriile date, TEACHER și ADMIN pot edita orice student
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student savedStudent = studentService.updateStudent(id, updatedStudent);
        StudentDTO dto = studentMapper.toDTO(savedStudent);
        return ResponseEntity.ok(dto);
    }

    // ✅ Doar ADMIN poate șterge studenți
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ STUDENT poate vedea doar propriile absențe, TEACHER și ADMIN pot vedea orice absență
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}/absences")
    public ResponseEntity<List<Absence>> getStudentAbsences(@PathVariable Long id) {
        List<Absence> absences = studentService.getAbsencesByStudentId(id);
        return ResponseEntity.ok(absences);
    }

    // ✅ TEACHER și ADMIN pot vedea totalul absențelor
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}/total-absences")
    public ResponseEntity<Integer> getStudentTotalAbsences(@PathVariable Long id) {
        int totalAbsences = absenceService.getTotalAbsencesForStudent(id);
        return ResponseEntity.ok(totalAbsences);
    }

    // ✅ STUDENT poate vedea doar propriile cursuri, TEACHER și ADMIN pot vedea orice curs
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}/upcoming-classes")
    public ResponseEntity<List<Class>> getStudentUpcomingClasses(@PathVariable Long id) {
        List<Class> upcomingClasses = studentService.getUpcomingClasses(id);
        return ResponseEntity.ok(upcomingClasses);
    }
}
