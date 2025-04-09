package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.EducationLevel;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.models.TeacherType;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private  ClassService classService;

    @Autowired
    private ParentService parentService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-primary")
    public ResponseEntity<Class> createPrimaryClass(@RequestBody Class studentClass, @RequestParam(required = false) Long teacherId) {
        studentClass.setEducationLevel(EducationLevel.PRIMARY);
        studentClass.setSpecialization(null);

        if (teacherId != null) {
            Teacher teacher = classService.findTeacherById(teacherId);
            if (teacher.getType() != TeacherType.EDUCATOR) {
                return ResponseEntity.badRequest().body(null);
            }
            studentClass.setClassTeacher(teacher);
        }

        return ResponseEntity.ok(classService.addClass(studentClass));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-middle")
    public ResponseEntity<Class> createMiddleClass(@RequestBody Class studentClass, @RequestParam Long teacherId) {
        studentClass.setEducationLevel(EducationLevel.MIDDLE);
        studentClass.setSpecialization(null);

        Teacher teacher = classService.findTeacherById(teacherId);
        if (teacher.getType() != TeacherType.TEACHER) {
            return ResponseEntity.badRequest().body(null);
        }

        studentClass.setClassTeacher(teacher);
        return ResponseEntity.ok(classService.addClass(studentClass));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-high")
    public ResponseEntity<Class> createHighClass(@RequestBody Class studentClass, @RequestParam Long teacherId) {
        if (studentClass.getSpecialization() == null || studentClass.getSpecialization().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Teacher teacher = classService.findTeacherById(teacherId);
        if (teacher.getType() != TeacherType.TEACHER) {
            return ResponseEntity.badRequest().body(null);
        }

        studentClass.setEducationLevel(EducationLevel.HIGH);
        studentClass.setClassTeacher(teacher);
        return ResponseEntity.ok(classService.addClass(studentClass));
    }




    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Class> getAllClasses() {
        return classService.getAllClasses();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable Long id) {
        Optional<Class> studentClass = classService.getClassById(id);
        return studentClass.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Class> updateClass(@PathVariable Long id,
                                             @RequestBody Class studentClass,
                                             @RequestParam(required = false) Long teacherId) {

        Optional<Class> existingClassOpt = classService.getClassById(id);
        if (existingClassOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Class existingClass = existingClassOpt.get();

        existingClass.setName(studentClass.getName());
        existingClass.setSpecialization(studentClass.getSpecialization());
        existingClass.setEducationLevel(studentClass.getEducationLevel());

        if (teacherId != null) {
            Teacher teacher = classService.findTeacherById(teacherId);
            existingClass.setClassTeacher(teacher);
        }

        return ResponseEntity.ok(classService.updateClass(id, existingClass));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/classes/{classId}/parent-emails")
    public ResponseEntity<List<String>> getParentEmailsByClass(@PathVariable Long classId) {
        List<String> emails = parentService.getParentEmailsByClassId(classId);
        return ResponseEntity.ok(emails);
    }

}
