package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.services.CatalogService;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST-Controller für alle klassenbezogenen Operationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Verwaltung von Schulklassen bereit
 * und ermöglicht die Erstellung verschiedener Klassentypen entsprechend der
 * deutschen Bildungsstruktur (Grundschule, Mittelschule, Gymnasium).
 * <p>
 * Hauptfunktionen:
 * - Erstellung von Klassen nach Bildungsebenen
 * - CRUD-Operationen für Klassen
 * - Lehrerzuordnung und -verwaltung
 * - Schülerverwaltung in Klassen
 * - Automatische Katalogerstellung für neue Klassen
 * - Bildungsebenen-spezifische Validierung
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle
 * - Administratoren haben vollständige CRUD-Rechte
 * - Lehrer können Klassen einsehen
 * - Authentifizierte Benutzer können einzelne Klassen abrufen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see ClassService
 * @see Class
 * @see EducationLevel
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/classes")
public class ClassController {

    /**
     * Service für Klassenoperationen.
     */
    @Autowired
    private ClassService classService;

    /**
     * Service für Elternoperationen.
     */
    @Autowired
    private ParentService parentService;

    /**
     * Service für Katalogoperationen.
     */
    @Autowired
    private CatalogService catalogService;

    /**
     * Erstellt eine neue Grundschulklasse (Klassen 0-4).
     * <p>
     * Nur Administratoren können Grundschulklassen erstellen.
     * Grundschulklassen benötigen einen Erzieher (EDUCATOR) als Klassenlehrer
     * und haben keine Spezialisierung. Ein Katalog wird automatisch erstellt.
     *
     * @param studentClass Klassendaten für die Erstellung
     * @param teacherId    Optional: ID des zugeordneten Erziehers
     * @return ResponseEntity mit der erstellten Klasse oder Fehler bei ungültigen Daten
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-primary")
    public ResponseEntity<Class> createPrimaryClass(@RequestBody Class studentClass, @RequestParam(required = false) Long teacherId) {
        studentClass.setEducationLevel(EducationLevel.PRIMARY);
        studentClass.setSpecialization(null);

        if (teacherId != null) {
            Teacher teacher = classService.findTeacherById(teacherId);
            if (teacher.getType() != TeacherType.EDUCATOR) {
                return ResponseEntity.badRequest().body(null);
            }
            studentClass.setClassTeacher(teacher);
        }

        Class savedClass = classService.addClass(studentClass);
        catalogService.createCatalogForClass(savedClass);

        return ResponseEntity.ok(savedClass);

    }

    /**
     * Erstellt eine neue Mittelschulklasse (Klassen 5-8).
     * <p>
     * Nur Administratoren können Mittelschulklassen erstellen.
     * Mittelschulklassen benötigen einen regulären Lehrer (TEACHER) als Klassenlehrer
     * und haben keine Spezialisierung. Ein Katalog wird automatisch erstellt.
     *
     * @param studentClass Klassendaten für die Erstellung
     * @param teacherId    ID des zugeordneten Lehrers (erforderlich)
     * @return ResponseEntity mit der erstellten Klasse oder Fehler bei ungültigen Daten
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-middle")
    public ResponseEntity<Class> createMiddleClass(@RequestBody Class studentClass, @RequestParam Long teacherId) {
        studentClass.setEducationLevel(EducationLevel.MIDDLE);
        studentClass.setSpecialization(null);

        Teacher teacher = classService.findTeacherById(teacherId);
        if (teacher.getType() != TeacherType.TEACHER) {
            return ResponseEntity.badRequest().body(null);
        }

        studentClass.setClassTeacher(teacher);

        Class savedClass = classService.addClass(studentClass);
        catalogService.createCatalogForClass(savedClass);

        return ResponseEntity.ok(savedClass);
    }

    /**
     * Erstellt eine neue Gymnasiumklasse (Klassen 9-12).
     * <p>
     * Nur Administratoren können Gymnasiumklassen erstellen.
     * Gymnasiumklassen benötigen einen regulären Lehrer (TEACHER) als Klassenlehrer
     * und müssen eine Spezialisierung haben. Ein Katalog wird automatisch erstellt.
     *
     * @param studentClass Klassendaten für die Erstellung (mit Spezialisierung)
     * @param teacherId    ID des zugeordneten Lehrers (erforderlich)
     * @return ResponseEntity mit der erstellten Klasse oder Fehler bei fehlender Spezialisierung
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-high")
    public ResponseEntity<Class> createHighClass(@RequestBody Class studentClass, @RequestParam Long teacherId) {
        if (studentClass.getSpecialization() == null || studentClass.getSpecialization().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Teacher teacher = classService.findTeacherById(teacherId);
        if (teacher.getType() != TeacherType.TEACHER) {
            return ResponseEntity.badRequest().body(null);
        }

        studentClass.setEducationLevel(EducationLevel.HIGH);
        studentClass.setClassTeacher(teacher);

        Class savedClass = classService.addClass(studentClass);
        catalogService.createCatalogForClass(savedClass);

        return ResponseEntity.ok(savedClass);
    }

    /**
     * Ruft alle Klassen im System ab.
     * <p>
     * Zugänglich für Administratoren und Lehrer.
     * Gibt eine vollständige Liste aller registrierten Klassen zurück.
     *
     * @return Liste aller Klassen im System
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public List<Class> getAllClasses() {
        return classService.getAllClasses();
    }

    /**
     * Ruft eine spezifische Klasse anhand ihrer ID ab.
     * <p>
     * Zugänglich für alle authentifizierten Benutzer.
     * Ermöglicht die Einsicht in Klassendetails für verschiedene Benutzerrollen.
     *
     * @param id Eindeutige ID der Klasse
     * @return ResponseEntity mit den Klassendaten oder 404 falls nicht gefunden
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable Long id) {
        Optional<Class> studentClass = classService.getClassById(id);
        return studentClass.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Aktualisiert die Daten einer existierenden Klasse.
     * <p>
     * Nur Administratoren können Klassendaten aktualisieren.
     * Ermöglicht die Änderung von Name, Spezialisierung, Bildungsebene
     * und Lehrerzuordnung unter Beachtung der Bildungsebenen-Regeln.
     *
     * @param id           ID der zu aktualisierenden Klasse
     * @param studentClass Klassen-Objekt mit neuen Daten
     * @param teacherId    Optional: ID des neuen Klassenlehrers
     * @return ResponseEntity mit der aktualisierten Klasse oder 404 falls nicht gefunden
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Class> updateClass(@PathVariable Long id,
                                             @RequestBody Class studentClass,
                                             @RequestParam(required = false) Long teacherId) {

        Optional<Class> existingClassOpt = classService.getClassById(id);
        if (existingClassOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Class existingClass = existingClassOpt.get();

        existingClass.setName(studentClass.getName());
        existingClass.setSpecialization(studentClass.getSpecialization());
        existingClass.setEducationLevel(studentClass.getEducationLevel());

        if (teacherId != null) {
            Teacher teacher = classService.findTeacherById(teacherId);
            existingClass.setClassTeacher(teacher);
        }

        return ResponseEntity.ok(classService.updateClass(id, existingClass));
    }

    /**
     * Löscht eine Klasse vollständig aus dem System.
     * <p>
     * Nur Administratoren können Klassen löschen.
     * Führt eine sichere Löschung mit Bereinigung aller Referenzen durch.
     *
     * @param id ID der zu löschenden Klasse
     * @return ResponseEntity mit No-Content-Status bei Erfolg
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ruft alle Schüler einer bestimmten Klasse ab.
     * <p>
     * Zugänglich für Administratoren und Lehrer.
     * Gibt eine Liste aller in der angegebenen Klasse eingeschriebenen Schüler zurück.
     *
     * @param id ID der Klasse
     * @return ResponseEntity mit Liste der Klassenschüler oder 404 falls Klasse nicht gefunden
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}/students")
    public ResponseEntity<?> getStudentsByClassId(@PathVariable Long id) {
        Optional<Class> classOpt = classService.getClassById(id);

        if (classOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Student> students = classService.getStudentsByClassId(id);
        return ResponseEntity.ok(students);
    }
}