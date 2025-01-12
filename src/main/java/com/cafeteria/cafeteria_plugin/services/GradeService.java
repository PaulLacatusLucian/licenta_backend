package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Semester;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.repositories.GradeRepository;
import com.cafeteria.cafeteria_plugin.repositories.SemesterRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SemesterRepository semesterRepository;

    @Autowired
    public GradeService(GradeRepository gradeRepository,
                        StudentRepository studentRepository,
                        TeacherRepository teacherRepository,
                        SemesterRepository semesterRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.semesterRepository = semesterRepository;
    }

    // Add a new grade
    public Grade addGrade(Long studentId, Long teacherId, Long semesterId, Double gradeValue) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu ID-ul specificat nu există."));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul cu ID-ul specificat nu există."));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semestrul cu ID-ul specificat nu există."));

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setSemester(semester);
        grade.setGrade(gradeValue);

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
    public Grade updateGrade(Long id, Long studentId, Long teacherId, Long semesterId, Double gradeValue) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota cu ID-ul specificat nu există."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu ID-ul specificat nu există."));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul cu ID-ul specificat nu există."));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semestrul cu ID-ul specificat nu există."));

        existingGrade.setStudent(student);
        existingGrade.setTeacher(teacher);
        existingGrade.setSemester(semester);
        existingGrade.setGrade(gradeValue);

        return gradeRepository.save(existingGrade);
    }

    // Delete a grade by ID
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }
}
