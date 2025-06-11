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

/**
 * Umfassender Service für die Lehrerverwaltung im Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see Teacher
 * @see TeacherType
 * @see EducationLevel
 * @see Schedule
 * @see ClassSession
 * @since 2024-12-18
 */
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

    /**
     * Fügt einen neuen Lehrer zum System hinzu.
     *
     * Diese Methode speichert einen neuen Lehrer nach erfolgreicher
     * Validierung der Eingabedaten. Geschäftsregeln werden durch
     * andere Komponenten (z.B. Controller) durchgesetzt.
     *
     * @param teacher Der zu speichernde Lehrer
     * @return Der gespeicherte Lehrer mit generierter ID
     */
    @Transactional
    public Teacher addTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    /**
     * Ruft alle Lehrer im System ab.
     *
     * @return Liste aller registrierten Lehrer (EDUCATOR und TEACHER)
     */
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    /**
     * Ruft einen spezifischen Lehrer anhand seiner ID ab.
     *
     * @param id Eindeutige ID des Lehrers
     * @return Der gefundene Lehrer
     * @throws IllegalArgumentException Falls der Lehrer nicht existiert
     */
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer mit ID " + id + " wurde nicht gefunden"));
    }

    /**
     * Aktualisiert die Daten eines existierenden Lehrers.
     *
     * Diese Methode führt umfassende Validierungen durch, um sicherzustellen,
     * dass Änderungen am Lehrertyp nicht zu Inkonsistenzen mit bereits
     * zugewiesenen Klassen führen.
     *
     * Validierungsregeln:
     * - EDUCATOR darf nicht zu TEACHER geändert werden, wenn er PRIMARY-Klassen hat
     * - TEACHER kann zu EDUCATOR geändert werden, muss aber PRIMARY-Klassen übernehmen
     *
     * @param id ID des zu aktualisierenden Lehrers
     * @param updatedTeacher Lehrer-Objekt mit neuen Daten
     * @return Der aktualisierte Lehrer
     * @throws IllegalArgumentException Falls der Lehrer nicht gefunden wird
     * @throws IllegalStateException Falls die Aktualisierung Geschäftsregeln verletzt
     */
    public Teacher updateTeacher(Long id, Teacher updatedTeacher) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer wurde nicht gefunden"));

        // Validierung: Überprüfung zugewiesener Klassen bei Typänderung
        Optional<Class> assignedClassOpt = classRepository.findByClassTeacherId(existingTeacher.getId());
        if (assignedClassOpt.isPresent()) {
            Class assignedClass = assignedClassOpt.get();
            if (assignedClass.getEducationLevel() == EducationLevel.PRIMARY &&
                    !updatedTeacher.getType().equals(TeacherType.EDUCATOR)) {
                throw new IllegalStateException(
                        "Lehrer ist einer Grundschulklasse zugewiesen und kann nicht zu TEACHER geändert werden.");
            }
        }

        // Aktualisierung der Lehrerdaten
        existingTeacher.setName(updatedTeacher.getName());
        existingTeacher.setSubject(updatedTeacher.getSubject());
        existingTeacher.setType(updatedTeacher.getType());

        return teacherRepository.save(existingTeacher);
    }

    /**
     * Löscht einen Lehrer vollständig aus dem System.
     *
     * Diese Methode führt eine sichere Löschung durch, die alle
     * Referenzen und abhängigen Daten bereinigt:
     * - Passwort-Reset-Token
     * - Lehrer-Datensatz
     * - Basis-Benutzer-Datensatz
     *
     * Hinweis: Klassenzuordnungen müssen vor der Löschung manuell
     * aufgelöst werden, um Datenintegrität zu gewährleisten.
     *
     * @param id ID des zu löschenden Lehrers
     * @throws IllegalArgumentException Falls der Lehrer nicht gefunden wird
     */
    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer wurde nicht gefunden"));

        tokenRepository.deleteAllByUser_Id(id);
        teacherRepository.deleteById(id);
        userRepository.deleteById(id);
    }

    /**
     * Ermittelt alle Schüler, die von einem bestimmten Lehrer unterrichtet werden.
     *
     * Diese Methode analysiert die Stundenpläne des Lehrers und sammelt
     * alle Schüler aus den Klassen, in denen der Lehrer unterrichtet.
     *
     * Logik:
     * 1. Ermittlung aller Klassen-IDs aus den Stundenplänen des Lehrers
     * 2. Sammlung aller Schüler aus diesen Klassen
     * 3. Entfernung von Duplikaten
     *
     * @param teacherId ID des Lehrers
     * @return Liste aller Schüler, die von diesem Lehrer unterrichtet werden
     */
    public List<Student> getStudentsForTeacher(Long teacherId) {
        // Klassen-IDs ermitteln, in denen der Lehrer unterrichtet
        List<Long> classIds = scheduleRepository.findByTeacherId(teacherId).stream()
                .map(schedule -> schedule.getStudentClass().getId())
                .distinct()
                .collect(Collectors.toList());

        // Schüler direkt aus der Datenbank abrufen
        return studentRepository.findByStudentClassIdIn(classIds);
    }

    /**
     * Ruft den wöchentlichen Stundenplan für einen Lehrer ab.
     *
     * @param teacherId ID des Lehrers
     * @return Liste aller Stundenplan-Einträge für diesen Lehrer
     */
    public List<Schedule> getWeeklyScheduleForTeacher(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId);
    }

    /**
     * Ruft alle Klassensitzungen ab, die ein Lehrer leitet.
     *
     * @param teacherId ID des Lehrers
     * @return Liste aller Klassensitzungen des Lehrers
     */
    public List<ClassSession> getSessionsForTeacher(Long teacherId) {
        return classSessionRepository.findByTeacherId(teacherId);
    }

    /**
     * Ermittelt alle verfügbaren Lehrer ohne Klassenleitungsaufgaben.
     *
     * Diese Methode ist nützlich für die Zuweisung neuer Klassen,
     * da sie nur Lehrer zurückgibt, die noch keine Klassenleitung haben.
     *
     * @return Liste der Lehrer ohne zugewiesene Klassenleitung
     */
    public List<Teacher> findAvailableTeachers() {
        return teacherRepository.findAll().stream()
                .filter(teacher -> teacher.getClassAsTeacher() == null)
                .toList();
    }

    /**
     * Sucht den Erzieher einer bestimmten Grundschulklasse.
     *
     * Diese Methode ist spezifisch für PRIMARY-Klassen, da dort
     * typischerweise ein Erzieher als Hauptlehrer fungiert.
     *
     * @param classId ID der Grundschulklasse
     * @return Der zugewiesene Erzieher oder null falls nicht gefunden
     */
    public Teacher getEducatorByClassId(Long classId) {
        return teacherRepository.findEducatorByClassId(classId);
    }

    /**
     * Sucht einen Lehrer anhand seines Benutzernamens.
     *
     * Diese Methode wird hauptsächlich für Authentifizierung
     * und session-basierte Operationen verwendet.
     *
     * @param username Benutzername des Lehrers
     * @return Der gefundene Lehrer oder null
     */
    public Teacher findByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }
}