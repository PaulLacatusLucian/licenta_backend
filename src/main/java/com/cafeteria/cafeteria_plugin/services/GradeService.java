package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.repositories.ClassSessionRepository;
import com.cafeteria.cafeteria_plugin.repositories.GradeRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassSessionRepository classSessionRepository;

    @Autowired
    public GradeService(GradeRepository gradeRepository,
                        StudentRepository studentRepository,
                        TeacherRepository teacherRepository, ClassSessionRepository classSessionRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.classSessionRepository = classSessionRepository;
    }

    public Grade addGrade(Long classSessionId, Long studentId, Double gradeValue) {
        // Găsiți sesiunea de clasă
        ClassSession classSession = classSessionRepository.findById(classSessionId)
                .orElseThrow(() -> new RuntimeException("Class session not found"));

        // Găsiți elevul
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Găsiți profesorul asociat sesiunii de clasă
        Teacher teacher = classSession.getTeacher();
        if (teacher == null) {
            throw new RuntimeException("No teacher associated with the class session");
        }

        // Creați și salvați nota
        Grade grade = new Grade();
        grade.setClassSession(classSession);
        grade.setStudent(student);
        grade.setGrade(gradeValue);
        grade.setTeacher(teacher);

        return gradeRepository.save(grade);
    }


    // Get all grades
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    // Get a grade by ID
    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    // Update a grade
    public Grade updateGrade(Long id, Long studentId, Long teacherId, Double gradeValue) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota cu ID-ul specificat nu există."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu ID-ul specificat nu există."));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul cu ID-ul specificat nu există."));

        existingGrade.setStudent(student);
        existingGrade.setTeacher(teacher);
        existingGrade.setGrade(gradeValue);

        return gradeRepository.save(existingGrade);
    }

    // Delete a grade by ID
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    // Save grade
    public Grade addGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu ID-ul specificat nu există."));
    }

    public Teacher getTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul cu ID-ul specificat nu există."));
    }

    public List<GradeDTO> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId)
                .stream()
                .map(grade -> new GradeDTO(
                        grade.getGrade(),
                        grade.getTeacher().getName(),
                        grade.getTeacher().getSubject(),
                        grade.getClassSession().getStartTime() // Presupunând că ai un câmp pentru data notei
                ))
                .collect(Collectors.toList());
    }
}
