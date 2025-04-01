package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.repositories.ClassRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
import com.cafeteria.cafeteria_plugin.models.EducationLevel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;

    public ClassService(ClassRepository classRepository, TeacherRepository teacherRepository) {
        this.classRepository = classRepository;
        this.teacherRepository = teacherRepository;
    }

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

    public Class updateClass(Long id, Class updatedClass) {
        return classRepository.findById(id)
                .map(existingClass -> {
                    existingClass.setName(updatedClass.getName());
                    existingClass.setClassTeacher(updatedClass.getClassTeacher());
                    existingClass.setSpecialization(updatedClass.getSpecialization());
                    existingClass.setEducationLevel(updatedClass.getEducationLevel());

                    validateClassByEducationLevel(existingClass);
                    return classRepository.save(existingClass);
                }).orElseThrow(() -> new IllegalArgumentException("Class not found"));
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

}
