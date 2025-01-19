package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.ClassSessionRepository;
import com.cafeteria.cafeteria_plugin.repositories.ScheduleRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final ClassSessionRepository classSessionRepository;


    public TeacherService(TeacherRepository teacherRepository, ScheduleRepository scheduleRepository, StudentRepository studentRepository, ClassSessionRepository classSessionRepository) {
        this.teacherRepository = teacherRepository;
        this.scheduleRepository = scheduleRepository;
        this.studentRepository = studentRepository;
        this.classSessionRepository = classSessionRepository;
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

        // Șterge profesorul
        teacherRepository.delete(teacher);
    }

    // Obține elevii pentru profesor
    public List<Student> getStudentsForTeacher(Long teacherId) {
        // Găsește toate orele predate de profesor
        List<Schedule> schedules = scheduleRepository.findByTeacherId(teacherId);

        // Extrage ID-urile claselor din orar
        List<Long> classIds = schedules.stream()
                .map(schedule -> schedule.getStudentClass().getId())
                .distinct()
                .collect(Collectors.toList());

        // Găsește toți studenții care aparțin claselor respective
        return studentRepository.findAll().stream()
                .filter(student -> classIds.contains(student.getStudentClass().getId()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Schedule> getWeeklyScheduleForTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));

        return scheduleRepository.findByTeacherId(teacherId);
    }

    public List<ClassSession> getSessionsForTeacher(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new IllegalArgumentException("Teacher not found");
        }
        return classSessionRepository.findByTeacherId(teacherId);
    }
}
