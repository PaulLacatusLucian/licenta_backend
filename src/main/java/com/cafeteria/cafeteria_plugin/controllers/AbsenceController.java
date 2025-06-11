package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.AddAbsenceRequestDTO;
import com.cafeteria.cafeteria_plugin.mappers.AbsenceMapper;
import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST-Controller für alle abwesenheitsbezogenen Operationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Verwaltung von Schülerabwesenheiten bereit
 * und ermöglicht sowohl administrative Operationen als auch schüler- und lehrerspezifische
 * Funktionen zur Abwesenheitsverwaltung.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für Abwesenheiten (Lehrer/Admin)
 * - Abwesenheitserfassung in Unterrichtsstunden
 * - Entschuldigungsverwaltung für Abwesenheiten
 * - Self-Service für Schüler zur Einsicht eigener Abwesenheiten
 * - Klassenspezifische Abwesenheitsübersicht für Klassenlehrer
 * - Integration mit dem Katalog-System
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle
 * - JWT-Token-Validierung
 * - Kontextabhängige Datenfilterung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see AbsenceService
 * @see Absence
 * @see AbsenceDTO
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/absences")
public class AbsenceController {

    /**
     * Service für Abwesenheitsoperationen.
     */
    @Autowired
    private AbsenceService absenceService;

    /**
     * Service für Unterrichtsstunden-Operationen.
     */
    @Autowired
    private ClassSessionService classSessionService;

    /**
     * Service für Schüleroperationen.
     */
    @Autowired
    private StudentService studentService;

    /**
     * Mapper für Abwesenheits-DTOs.
     */
    @Autowired
    private AbsenceMapper absenceMapper;

    /**
     * Service für Katalogoperationen.
     */
    @Autowired
    private CatalogService catalogService;

    /**
     * Service für Lehreroperationen.
     */
    @Autowired
    private TeacherService teacherService;

    /**
     * Utility für JWT-Token-Verarbeitung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Fügt eine Abwesenheit zu einer bestimmten Unterrichtsstunde hinzu.
     * <p>
     * Nur Lehrer und Administratoren können Abwesenheiten erfassen.
     * Die Abwesenheit wird automatisch im Katalog vermerkt.
     *
     * @param sessionId ID der Unterrichtsstunde
     * @param request   DTO mit Schüler-ID und weiteren Abwesenheitsdaten
     * @return ResponseEntity mit der erstellten Abwesenheit als DTO
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<AbsenceDTO> addAbsenceToSession(@PathVariable Long sessionId, @RequestBody AddAbsenceRequestDTO request) {
        ClassSession session = classSessionService.getSessionById(sessionId);
        Student student = studentService.getStudentById(request.getStudentId());

        Absence absence = new Absence();
        absence.setStudent(student);
        absence.setClassSession(session);

        Absence saved = absenceService.addAbsence(absence);
        return ResponseEntity.status(HttpStatus.CREATED).body(absenceMapper.toDto(saved));
    }

    /**
     * Ruft alle Abwesenheiten im System ab.
     * <p>
     * Nur für Lehrer und Administratoren zugänglich.
     * Gibt eine vollständige Liste aller erfassten Abwesenheiten zurück.
     *
     * @return ResponseEntity mit Liste aller Abwesenheiten als DTOs
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AbsenceDTO>> getAllAbsences() {
        List<AbsenceDTO> dtos = absenceService.getAllAbsences()
                .stream().map(absenceMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft eine spezifische Abwesenheit anhand ihrer ID ab.
     * <p>
     * Nur für Lehrer und Administratoren zugänglich.
     *
     * @param id Eindeutige ID der Abwesenheit
     * @return ResponseEntity mit der Abwesenheit als DTO oder 404 falls nicht gefunden
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AbsenceDTO> getAbsenceById(@PathVariable Long id) {
        Optional<Absence> absence = absenceService.getAbsenceById(id);
        return absence.map(a -> ResponseEntity.ok(absenceMapper.toDto(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Aktualisiert eine bestehende Abwesenheit.
     * <p>
     * Ermöglicht die Änderung von Abwesenheitsdaten, insbesondere
     * des Entschuldigungsstatus und der Zuordnung zu Stunden/Schülern.
     *
     * @param id      ID der zu aktualisierenden Abwesenheit
     * @param request DTO mit neuen Abwesenheitsdaten
     * @return ResponseEntity mit der aktualisierten Abwesenheit als DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<AbsenceDTO> updateAbsence(@PathVariable Long id, @RequestBody AddAbsenceRequestDTO request) {
        ClassSession session = classSessionService.getSessionById(request.getClassSessionId());
        Student student = studentService.getStudentById(request.getStudentId());

        Absence updated = new Absence();
        updated.setId(id);
        updated.setStudent(student);
        updated.setClassSession(session);

        updated.setJustified(request.getJustified());

        Absence saved = absenceService.updateAbsence(id, updated);
        return ResponseEntity.ok(absenceMapper.toDto(saved));
    }

    /**
     * Löscht eine Abwesenheit vollständig aus dem System.
     * <p>
     * Nur Lehrer und Administratoren können Abwesenheiten löschen.
     * Führt eine sichere Löschung mit Bereinigung aller Referenzen durch.
     *
     * @param id ID der zu löschenden Abwesenheit
     * @return ResponseEntity mit No-Content-Status bei Erfolg
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ruft alle Abwesenheiten des aktuell angemeldeten Schülers ab.
     * <p>
     * Self-Service-Endpunkt für Schüler zur Einsicht ihrer eigenen Abwesenheiten.
     * Verwendet JWT-Token zur Identifikation des Schülers.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der Schülerabwesenheiten als DTOs
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<List<AbsenceDTO>> getAbsencesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        List<Absence> absences = absenceService.getAbsencesForStudent(student.getId());
        List<AbsenceDTO> dtoList = absences.stream().map(absenceMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Ruft alle Abwesenheiten eines bestimmten Schülers ab.
     * <p>
     * Nur für Lehrer und Administratoren zugänglich.
     * Ermöglicht die Einsicht in die Abwesenheitshistorie eines spezifischen Schülers.
     *
     * @param studentId ID des Schülers
     * @return ResponseEntity mit Liste der Schülerabwesenheiten als DTOs
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AbsenceDTO>> getAbsencesForStudent(@PathVariable Long studentId) {
        List<Absence> absences = absenceService.getAbsencesForStudent(studentId);
        List<AbsenceDTO> dtoList = absences.stream().map(absenceMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Entschuldigt eine bestehende Abwesenheit.
     * <p>
     * Nur Lehrer und Administratoren können Abwesenheiten entschuldigen.
     * Die Entschuldigung wird sowohl in der Abwesenheit als auch im Katalog vermerkt.
     *
     * @param id ID der zu entschuldigenden Abwesenheit
     * @return ResponseEntity mit der aktualisierten Abwesenheit als DTO
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}/justify")
    public ResponseEntity<AbsenceDTO> justifyAbsence(@PathVariable Long id) {
        try {
            Optional<Absence> absenceOpt = absenceService.getAbsenceById(id);
            if (absenceOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Absence absence = absenceOpt.get();
            Absence justifiedAbsence = absenceService.justifyAbsence(absence);

            catalogService.updateAbsenceJustification(id, true);

            return ResponseEntity.ok(absenceMapper.toDto(justifiedAbsence));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ruft alle unentschuldigten Abwesenheiten für die Klasse des angemeldeten Lehrers ab.
     * <p>
     * Nur für Klassenlehrer zugänglich. Der Endpunkt überprüft, ob der angemeldete
     * Lehrer als Klassenlehrer einer Klasse zugeordnet ist und gibt dann alle
     * unentschuldigten Abwesenheiten dieser Klasse zurück.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der unentschuldigten Abwesenheiten als DTOs
     *         oder 403 Forbidden falls der Lehrer kein Klassenlehrer ist
     */
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/class/unjustified")
    public ResponseEntity<List<AbsenceDTO>> getUnjustifiedAbsencesForClass(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Teacher teacher = teacherService.findByUsername(username);

        // Verifică dacă profesorul este diriginte
        if (teacher.getClassAsTeacher() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Absence> absences = absenceService.getUnjustifiedAbsencesForClass(teacher.getClassAsTeacher().getId());
        List<AbsenceDTO> dtoList = absences.stream().map(absenceMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}