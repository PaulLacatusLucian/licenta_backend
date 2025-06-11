package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.mappers.ScheduleMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.mappers.TeacherMapper;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.User.UserType;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import com.cafeteria.cafeteria_plugin.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für alle lehrerbezogenen Operationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Verwaltung von Lehrern bereit
 * und ermöglicht sowohl administrative Operationen als auch lehrerspezifische
 * Self-Service-Funktionen.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für Lehrer (Admin)
 * - Self-Service für angemeldete Lehrer
 * - Schülerverwaltung (Lehrer sehen ihre Schüler)
 * - Stundenplanverwaltung
 * - Klassensitzungsübersicht
 * - Elternkommunikation
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle
 * - JWT-Token-Validierung
 * - Automatische Passwort-Generierung bei Erstellung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see TeacherService
 * @see Teacher
 * @see TeacherDTO
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Erstellt einen neuen Lehrer im System.
     * <p>
     * Nur Administratoren können neue Lehrer erstellen.
     * Die Methode generiert automatisch Benutzername und Passwort
     * basierend auf dem Namen des Lehrers.
     * <p>
     * Generierungslogik:
     * - Benutzername: [name_in_kleinbuchstaben].prof
     * - Passwort: [name_ohne_punkte]123!
     *
     * @param teacher Lehrerobjekt mit grundlegenden Informationen
     * @return ResponseEntity mit dem erstellten Lehrer
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Teacher> addTeacher(@RequestBody Teacher teacher) {
        String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", ".");
        teacher.setUsername(baseUsername + ".prof");
        teacher.setPassword(passwordEncoder.encode(baseUsername.replace(".", "_") + "123!"));

        teacher.setUserType(UserType.TEACHER);

        return ResponseEntity.ok(teacherService.addTeacher(teacher));
    }

    /**
     * Ruft alle Lehrer im System ab.
     * <p>
     * Zugänglich für Administratoren und andere Lehrer.
     * Gibt eine vollständige Liste aller registrierten Lehrer zurück.
     *
     * @return Liste aller Lehrer im System
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    /**
     * Ruft einen spezifischen Lehrer anhand seiner ID ab.
     * <p>
     * Zugänglich für Administratoren und andere Lehrer.
     *
     * @param id Eindeutige ID des Lehrers
     * @return ResponseEntity mit den Lehrerdaten
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    /**
     * Aktualisiert die Daten eines existierenden Lehrers.
     * <p>
     * Nur Administratoren können Lehrerdaten ändern.
     * Führt Validierungen durch, um Konsistenz mit zugewiesenen Klassen sicherzustellen.
     *
     * @param id      ID des zu aktualisierenden Lehrers
     * @param teacher Lehrer-Objekt mit neuen Daten
     * @return ResponseEntity mit dem aktualisierten Lehrer
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }

    /**
     * Löscht einen Lehrer vollständig aus dem System.
     * <p>
     * Nur Administratoren können Lehrer löschen.
     * Führt eine sichere Löschung mit Bereinigung aller Referenzen durch.
     *
     * @param id ID des zu löschenden Lehrers
     * @return ResponseEntity mit No-Content-Status bei Erfolg oder Bad-Request bei Fehlern
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        try {
            teacherService.deleteTeacher(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Ruft die Daten des aktuell angemeldeten Lehrers ab.
     * <p>
     * Self-Service-Endpunkt für Lehrer, um ihre eigenen Daten einzusehen.
     * Verwendet JWT-Token zur Identifikation des Lehrers.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit den Lehrerdaten als DTO
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeacherDTO> getCurrentTeacher(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);

        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(teacherMapper.toDto(teacher));
    }

    /**
     * Ruft alle Schüler ab, die vom aktuellen Lehrer unterrichtet werden.
     * <p>
     * Self-Service-Endpunkt für Lehrer zur Einsicht ihrer Schüler.
     * Analysiert die Stundenpläne und sammelt alle Schüler aus den
     * Klassen, in denen der Lehrer unterrichtet.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der Schüler als DTOs
     */
    @GetMapping("/me/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentDTO>> getMyStudents(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<Student> students = teacherService.getStudentsForTeacher(teacher.getId());
        List<StudentDTO> studentDTOs = students.stream()
                .map(studentMapper::toDTO)
                .toList();

        return ResponseEntity.ok(studentDTOs);
    }

    /**
     * Ruft den wöchentlichen Stundenplan des aktuellen Lehrers ab.
     * <p>
     * Self-Service-Endpunkt für Lehrer zur Einsicht ihres Stundenplans.
     * Zeigt alle Unterrichtsstunden, die der Lehrer in der Woche hat.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit dem Stundenplan als Liste von DTOs
     */
    @GetMapping("/me/weekly-schedule")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ScheduleDTO>> getMySchedule(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<Schedule> schedule = teacherService.getWeeklyScheduleForTeacher(teacher.getId());
        List<ScheduleDTO> scheduleDTOs = schedule.stream()
                .map(scheduleMapper::toDto)
                .toList();
        return ResponseEntity.ok(scheduleDTOs);
    }

    /**
     * Ruft alle Klassensitzungen des aktuellen Lehrers ab.
     * <p>
     * Self-Service-Endpunkt für Lehrer zur Einsicht ihrer gehaltenen
     * oder geplanten Klassensitzungen. Nützlich für Noten- und
     * Anwesenheitsverwaltung.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der Klassensitzungen
     */
    @GetMapping("/me/sessions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ClassSession>> getMySessions(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<ClassSession> sessions = teacherService.getSessionsForTeacher(teacher.getId());
        return ResponseEntity.ok(sessions);
    }

    /**
     * Ruft die Email-Adressen aller Eltern der eigenen Klasse ab.
     * <p>
     * Self-Service-Endpunkt für Klassenlehrer zur Kommunikation mit Eltern.
     * Gibt alle verfügbaren Email-Adressen (Mutter und Vater) der Schüler
     * aus der Klasse zurück, für die der Lehrer Klassenlehrer ist.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der Eltern-Email-Adressen
     */
    @GetMapping("/my-class/parent-emails")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<String>> getParentEmailsForOwnClass(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Teacher teacher = teacherService.findByUsername(username);

        if (teacher.getClassAsTeacher() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<String> emails = parentService.getParentEmailsByClassId(teacher.getClassAsTeacher().getId());
        return ResponseEntity.ok(emails);
    }
}