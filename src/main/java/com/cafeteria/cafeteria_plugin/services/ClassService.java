package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.ClassRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.cafeteria.cafeteria_plugin.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Zentraler Service für die Klassenverwaltung im Schulverwaltungssystem.
 * <p>
 * Diese Klasse ist verantwortlich für:
 * - Erstellung und Verwaltung von Schulklassen
 * - Validierung von Klassendaten nach Bildungsebene
 * - Zuweisung von Lehrern zu Klassen
 * - Verwaltung von Schülern in Klassen
 * <p>
 * Der Service stellt sicher, dass Klassen korrekt nach ihren
 * Bildungsebenen (Grundschule, Mittelschule, Gymnasium) validiert
 * und verwaltet werden.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Class
 * @see EducationLevel
 * @see Teacher
 * @since 2025-01-01
 */
@Service
public class ClassService {

    /**
     * Repository für Klassenoperationen.
     */
    @Autowired
    private ClassRepository classRepository;

    /**
     * Repository für Lehreroperationen.
     */
    @Autowired
    private TeacherRepository teacherRepository;

    /**
     * Repository für Schüleroperationen.
     */
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Neue Klasse hinzufügen (nach Bildungsniveau validiert).
     * <p>
     * Diese Methode validiert die Klasse basierend auf ihrer Bildungsebene
     * und speichert sie in der Datenbank.
     *
     * @param studentClass Die zu erstellende Klasse
     * @return Die gespeicherte Klasse mit generierter ID
     * @throws IllegalArgumentException Falls Validierung fehlschlägt
     */
    public Class addClass(Class studentClass) {
        validateClassByEducationLevel(studentClass);
        return classRepository.save(studentClass);
    }

    /**
     * Alle Klassen abrufen.
     *
     * @return Liste aller Klassen im System
     */
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    /**
     * Klasse nach ID abrufen.
     *
     * @param id Die ID der gesuchten Klasse
     * @return Optional mit der gefundenen Klasse oder leer falls nicht gefunden
     */
    public Optional<Class> getClassById(Long id) {
        return classRepository.findById(id);
    }

    /**
     * Klasse aktualisieren (mit Validierung je nach Bildungsniveau).
     * <p>
     * Diese Methode führt umfassende Validierungen basierend auf der
     * Bildungsebene durch und stellt sicher, dass die Lehrertypen
     * und Spezialisierungen korrekt zugewiesen sind.
     *
     * @param id      Die ID der zu aktualisierenden Klasse
     * @param updated Die Klasse mit den neuen Daten
     * @return Die aktualisierte Klasse
     * @throws IllegalArgumentException Falls Validierung fehlschlägt
     */
    public Class updateClass(Long id, Class updated) {
        EducationLevel level = updated.getEducationLevel();

        if (level == null) {
            throw new IllegalArgumentException("Bildungsniveau darf nicht null sein.");
        }

        switch (level) {
            case PRIMARY -> {
                if (updated.getClassTeacher() != null && updated.getClassTeacher().getType() != TeacherType.EDUCATOR) {
                    throw new IllegalArgumentException("Eine Grundschulklasse muss einen Erzieher haben.");
                }
                updated.setSpecialization(null);
            }
            case MIDDLE -> {
                if (updated.getClassTeacher() != null && updated.getClassTeacher().getType() != TeacherType.TEACHER) {
                    throw new IllegalArgumentException("Eine Mittelstufenklasse muss einen Lehrer haben.");
                }
                updated.setSpecialization(null);
            }
            case HIGH -> {
                if (updated.getClassTeacher() != null && updated.getClassTeacher().getType() != TeacherType.TEACHER) {
                    throw new IllegalArgumentException("Eine Oberstufenklasse muss einen Lehrer haben.");
                }
                if (updated.getSpecialization() == null || updated.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("Eine Oberstufenklasse muss eine Spezialisierung haben.");
                }
            }
        }

        return classRepository.save(updated);
    }

    /**
     * Klasse nach ID löschen.
     *
     * @param id Die ID der zu löschenden Klasse
     */
    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

    /**
     * Lehrer nach ID suchen.
     *
     * @param teacherId Die ID des gesuchten Lehrers
     * @return Der gefundene Lehrer
     * @throws IllegalArgumentException Falls Lehrer nicht gefunden wird
     */
    public Teacher findTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer mit ID " + teacherId + " wurde nicht gefunden."));
    }

    /**
     * Validierung der Klasse je nach Bildungsniveau.
     * <p>
     * Diese private Methode stellt sicher, dass die Klassenattribute
     * korrekt für die jeweilige Bildungsebene gesetzt sind:
     * - Grundschule (0-4): Keine Spezialisierung
     * - Mittelschule (5-8): Keine Spezialisierung
     * - Gymnasium (9-12): Spezialisierung erforderlich
     *
     * @param studentClass Die zu validierende Klasse
     * @throws IllegalArgumentException Falls Validierung fehlschlägt
     */
    private void validateClassByEducationLevel(Class studentClass) {
        EducationLevel level = studentClass.getEducationLevel();

        switch (level) {
            case PRIMARY -> {
                if (studentClass.getSpecialization() != null && !studentClass.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("Grundschulklassen (0–4) dürfen keine Spezialisierung haben.");
                }
            }
            case MIDDLE -> {
                if (studentClass.getSpecialization() != null && !studentClass.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("Mittelstufenklassen (5–8) dürfen keine Spezialisierung haben.");
                }
            }
            case HIGH -> {
                if (studentClass.getSpecialization() == null || studentClass.getSpecialization().isBlank()) {
                    throw new IllegalArgumentException("Oberstufenklassen (9–12) müssen eine Spezialisierung haben.");
                }
            }
            default -> throw new IllegalArgumentException("Nicht unterstütztes Bildungsniveau.");
        }
    }

    /**
     * Schüler nach Klassen-ID abrufen.
     *
     * @param id Die ID der Klasse
     * @return Liste der Schüler in der Klasse
     * @throws IllegalArgumentException Falls Klasse nicht gefunden wird
     */
    public List<Student> getStudentsByClassId(Long id) {
        if (!classRepository.existsById(id)) {
            throw new IllegalArgumentException("Klasse mit ID " + id + " wurde nicht gefunden.");
        }
        return studentRepository.findByStudentClassId(id);
    }
}