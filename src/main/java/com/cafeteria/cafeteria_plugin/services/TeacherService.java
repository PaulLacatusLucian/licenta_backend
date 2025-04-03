package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.email.PasswordResetTokenRepository;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;


    @Transactional
    public Teacher addTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul cu ID-ul " + id + " nu a fost găsit"));
    }

    public Teacher updateTeacher(Long id, Teacher updatedTeacher) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu a fost găsit"));

        Optional<Class> assignedClassOpt = classRepository.findByClassTeacherId(existingTeacher.getId());
        if (assignedClassOpt.isPresent()) {
            Class assignedClass = assignedClassOpt.get();
            if (assignedClass.getEducationLevel() == EducationLevel.PRIMARY &&
                    !updatedTeacher.getType().equals(TeacherType.EDUCATOR)) {
                throw new IllegalStateException("Profesorul este asignat unei clase primare și nu poate fi transformat în TEACHER.");
            }
        }


        existingTeacher.setName(updatedTeacher.getName());
        existingTeacher.setSubject(updatedTeacher.getSubject());
        existingTeacher.setType(updatedTeacher.getType());

        return teacherRepository.save(existingTeacher);
    }



    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu a fost găsit"));
        tokenRepository.deleteAllByUser_Id(id);
        teacherRepository.deleteById(id);
        userRepository.deleteById(id);
    }

    public List<Student> getStudentsForTeacher(Long teacherId) {
        // Găsește ID-urile claselor în care profesorul predă
        List<Long> classIds = scheduleRepository.findByTeacherId(teacherId).stream()
                .map(schedule -> schedule.getStudentClass().getId())
                .distinct()
                .collect(Collectors.toList());

        // Returnează studenții direct din baza de date
        return studentRepository.findByStudentClassIdIn(classIds);
    }

    public List<Schedule> getWeeklyScheduleForTeacher(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId);
    }

    public List<ClassSession> getSessionsForTeacher(Long teacherId) {
        return classSessionRepository.findByTeacherId(teacherId);
    }

    public List<Teacher> findAvailableTeachers() {
        return teacherRepository.findAll().stream()
                .filter(teacher -> teacher.getClassAsTeacher() == null)
                .toList();
    }

    public Teacher getEducatorByClassId(Long classId) {
        return teacherRepository.findEducatorByClassId(classId);
    }


}
