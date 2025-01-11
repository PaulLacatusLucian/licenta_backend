package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.repositories.ClassRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;

    public TeacherService(TeacherRepository teacherRepository, ClassRepository classRepository) {
        this.teacherRepository = teacherRepository;
        this.classRepository = classRepository;
    }
    // Adaugă un profesor
    public Teacher addTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    // Obține toți profesorii
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    // Obține un profesor după ID
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + id));
    }

    // Actualizează un profesor
    public Teacher updateTeacher(Long id, Teacher updatedTeacher) {
        return teacherRepository.findById(id)
                .map(existingTeacher -> {
                    existingTeacher.setName(updatedTeacher.getName());
                    existingTeacher.setSubject(updatedTeacher.getSubject());
                    return teacherRepository.save(existingTeacher);
                }).orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
    }

    // Șterge un profesor
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu a fost găsit"));

        // Șterge clasa asociată, dacă există
        if (teacher.getClassAsTeacher() != null) {
            classRepository.delete(teacher.getClassAsTeacher());
        }

        // Șterge profesorul
        teacherRepository.delete(teacher);
    }

}
