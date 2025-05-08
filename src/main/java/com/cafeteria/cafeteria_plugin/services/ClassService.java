package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.ClassRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassService {

    @Autowired
    private  ClassRepository classRepository;
    @Autowired
    private  TeacherRepository teacherRepository;
    @Autowired
    private  StudentRepository studentRepository;


    public Class addClass(Class studentClass) {
        validateClassByEducationLevel(studentClass);
        return classRepository.save(studentClass);
    }

    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    public Optional<Class> getClassById(Long id) {
        return classRepository.findById(id);
    }

    public Class updateClass(Long id, Class updated) {
        EducationLevel level = updated.getEducationLevel();

        if (level == null) {
            throw new IllegalArgumentException("Education level must not be null.");
        }

        switch (level) {
            case PRIMARY -> {
                if (updated.getClassTeacher() != null && updated.getClassTeacher().getType() != TeacherType.EDUCATOR) {
                    throw new IllegalArgumentException("Primary class must have an educator.");
                }
                updated.setSpecialization(null);
            }
            case MIDDLE -> {
                if (updated.getClassTeacher() != null && updated.getClassTeacher().getType() != TeacherType.TEACHER) {
                    throw new IllegalArgumentException("Middle class must have a regular teacher.");
                }
                updated.setSpecialization(null);
            }
            case HIGH -> {
                if (updated.getClassTeacher() != null && updated.getClassTeacher().getType() != TeacherType.TEACHER) {
                    throw new IllegalArgumentException("High school class must have a regular teacher.");
                }
                if (updated.getSpecialization() == null || updated.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("High school class must have a specialization.");
                }
            }
        }

        return classRepository.save(updated);
    }



    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

    public Teacher findTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));
    }

    private void validateClassByEducationLevel(Class studentClass) {
        EducationLevel level = studentClass.getEducationLevel();

        switch (level) {
            case PRIMARY -> {
                if (studentClass.getSpecialization() != null && !studentClass.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("Primary classes (0-4) should not have specialization.");
                }
            }
            case MIDDLE -> {
                if (studentClass.getSpecialization() != null && !studentClass.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("Middle school classes (5-8) should not have specialization.");
                }
            }
            case HIGH -> {
                if (studentClass.getSpecialization() == null || studentClass.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("High school classes (9-12) must have a specialization.");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported education level.");
        }
    }

    public List<Student> getStudentsByClassId(Long id) {
        if (!classRepository.existsById(id)) {
            throw new IllegalArgumentException("Class not found with ID: " + id);
        }
        return studentRepository.findByStudentClassId(id);
    }
}
