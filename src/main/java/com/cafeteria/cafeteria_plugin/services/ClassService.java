package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.repositories.ClassRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
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
}
