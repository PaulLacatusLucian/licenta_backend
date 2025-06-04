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

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    //Neue Klasse hinzufügen (nach Bildungsniveau validiert)
    public Class addClass(Class studentClass) {
        validateClassByEducationLevel(studentClass);
        return classRepository.save(studentClass);
    }

    //Alle Klassen abrufen
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    //Klasse nach ID abrufen
    public Optional<Class> getClassById(Long id) {
        return classRepository.findById(id);
    }

    //Klasse aktualisieren (mit Validierung je nach Bildungsniveau)
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

    //Klasse nach ID löschen
    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

    //Lehrer nach ID suchen
    public Teacher findTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Lehrer mit ID " + teacherId + " wurde nicht gefunden."));
    }

    //Validierung der Klasse je nach Bildungsniveau
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

    //Schüler nach Klassen-ID abrufen
    public List<Student> getStudentsByClassId(Long id) {
        if (!classRepository.existsById(id)) {
            throw new IllegalArgumentException("Klasse mit ID " + id + " wurde nicht gefunden.");
        }
        return studentRepository.findByStudentClassId(id);
    }
}
