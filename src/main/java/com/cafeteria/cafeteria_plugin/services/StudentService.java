package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private PastStudentRepository pastStudentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Transactional
    public Student saveStudentWithClass(Student studentDetails, Long classId) {
        Class studentSchoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu există"));

        studentDetails.setStudentClass(studentSchoolClass);
        return studentRepository.save(studentDetails);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu ID-ul " + id + " nu există"));
    }

    public List<Absence> getAbsencesByStudentId(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    public List<Class> getUpcomingClasses(Long studentId) {
        return Collections.emptyList();
    }

    public Optional<Student> getStudentByParentId(Long parentId) {
        return studentRepository.findByParentId(parentId);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional
    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setPhoneNumber(updatedStudent.getPhoneNumber());
        existingStudent.setStudentClass(updatedStudent.getStudentClass());

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        gradeRepository.deleteByStudentId(student.getId());
        studentRepository.delete(student);
    }

    @Transactional
    public void advanceYear() {
        List<Class> allSchoolClasses = classRepository.findAll();

        for (Class currentSchoolClass : allSchoolClasses) {
            String className = currentSchoolClass.getName();

            if (className.startsWith("12")) {
                graduateStudents(currentSchoolClass);
            } else {
                moveStudentsToNextClass(currentSchoolClass);
            }
        }
    }

    private void graduateStudents(Class currentSchoolClass) {
        List<Student> studentsInClass = studentRepository.findByStudentClass(currentSchoolClass);

        for (Student student : studentsInClass) {
            PastStudent pastStudent = new PastStudent(
                    null,
                    student.getName(),
                    currentSchoolClass.getSpecialization()
            );

            pastStudentRepository.save(pastStudent);
            studentRepository.delete(student);
        }
    }

    private void moveStudentsToNextClass(Class currentSchoolClass) {
        String className = currentSchoolClass.getName();
        String numericPart = className.replaceAll("^(\\d+).*", "$1");

        try {
            int currentYear = Integer.parseInt(numericPart);
            String newClassName = (currentYear + 1) + className.substring(numericPart.length());

            Class newSchoolClass = classRepository.findByName(newClassName)
                    .orElseGet(() -> createNewClass(newClassName, currentSchoolClass));

            List<Student> studentsInClass = studentRepository.findByStudentClass(currentSchoolClass);

            for (Student student : studentsInClass) {
                student.setStudentClass(newSchoolClass);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Numele clasei nu începe cu un număr valid: " + className);
        }
    }

    private Class createNewClass(String newClassName, Class oldSchoolClass) {
        Class newSchoolClass = new Class();
        newSchoolClass.setName(newClassName);
        newSchoolClass.setSpecialization(oldSchoolClass.getSpecialization());
        newSchoolClass.setClassTeacher(null);
        return classRepository.save(newSchoolClass);
    }
}
