package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.email.PasswordResetTokenRepository;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import org.springframework.stereotype.Service;

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

    // Neuen Lehrer speichern
    @Transactional
    public Teacher addTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    // Alle Lehrer abrufen
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    // Lehrer anhand seiner ID abrufen
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer mit ID " + id + " wurde nicht gefunden."));
    }

    // Lehrerinformationen aktualisieren
    public Teacher updateTeacher(Long id, Teacher updatedTeacher) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer wurde nicht gefunden."));

        Optional<Class> assignedClassOpt = classRepository.findByClassTeacherId(existingTeacher.getId());

        if (assignedClassOpt.isPresent()) {
            Class assignedClass = assignedClassOpt.get();
            if (assignedClass.getEducationLevel() == EducationLevel.PRIMARY &&
                    !updatedTeacher.getType().equals(TeacherType.EDUCATOR)) {
                throw new IllegalStateException("Der Lehrer ist einer Grundschulklasse zugewiesen und kann nicht in einen TEACHER geändert werden.");
            }
        }

        existingTeacher.setName(updatedTeacher.getName());
        existingTeacher.setSubject(updatedTeacher.getSubject());
        existingTeacher.setType(updatedTeacher.getType());

        return teacherRepository.save(existingTeacher);
    }

    // Lehrer löschen (einschließlich Token und Benutzerkonto)
    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer wurde nicht gefunden."));

        tokenRepository.deleteAllByUser_Id(id);
        teacherRepository.deleteById(id);
        userRepository.deleteById(id);
    }

    // Alle Schüler abrufen, denen der Lehrer unterrichtet
    public List<Student> getStudentsForTeacher(Long teacherId) {
        List<Long> classIds = scheduleRepository.findByTeacherId(teacherId).stream()
                .map(schedule -> schedule.getStudentClass().getId())
                .distinct()
                .collect(Collectors.toList());

        return studentRepository.findByStudentClassIdIn(classIds);
    }

    // Wöchentlicher Stundenplan eines Lehrers
    public List<Schedule> getWeeklyScheduleForTeacher(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId);
    }

    // Unterrichtseinheiten (Sessions) eines Lehrers
    public List<ClassSession> getSessionsForTeacher(Long teacherId) {
        return classSessionRepository.findByTeacherId(teacherId);
    }

    // Lehrer, die keiner Klasse zugewiesen sind
    public List<Teacher> findAvailableTeachers() {
        return teacherRepository.findAll().stream()
                .filter(teacher -> teacher.getClassAsTeacher() == null)
                .toList();
    }

    // Klassenlehrer (Educator) für eine bestimmte Klasse
    public Teacher getEducatorByClassId(Long classId) {
        return teacherRepository.findEducatorByClassId(classId);
    }

    // Lehrer anhand des Benutzernamens finden
    public Teacher findByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }
}
