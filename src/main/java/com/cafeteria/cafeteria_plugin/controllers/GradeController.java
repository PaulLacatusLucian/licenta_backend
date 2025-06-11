package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.mappers.GradeMapper;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.ClassSessionService;
import com.cafeteria.cafeteria_plugin.services.GradeService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST-Controller für die Verwaltung von Schulnoten.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die umfassende Verwaltung des
 * Notensystems bereit und ermöglicht die Erstellung, Bearbeitung, Abfrage
 * und Löschung von Noten für Schüler. Sie unterstützt sowohl die Lehrerperspektive
 * für die Notenvergabe als auch die Schülerperspektive für die Einsicht eigener Noten.
 * <p>
 * Hauptfunktionen:
 * - Notenvergabe für spezifische Unterrichtsstunden
 * - CRUD-Operationen für Notenverwaltung
 * - Notenabruf nach Schüler und Unterrichtsstunde
 * - Schülerzugriff auf eigene Noten
 * - Flexible Notenaktualisierung mit partiellen Updates
 * - Notenbeschreibungen und Kommentare
 * - Validierung der Notenwerte und Zuordnungen
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle für verschiedene Benutzertypen
 * - Lehrer und Administratoren haben vollständige Notenverwaltungsrechte
 * - Schüler können nur ihre eigenen Noten einsehen
 * - JWT-Authentifizierung für sichere Datenzugriffe
 * - Validierung der Berechtigung für Notenänderungen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see GradeService
 * @see Grade
 * @see ClassSession
 * @see Student
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/grades")
public class GradeController {

    /**
     * Service für Notenverwaltungsoperationen.
     */
    @Autowired
    private GradeService gradeService;

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
     * Mapper für Transformation von Grade-Entitäten in DTOs.
     */
    @Autowired
    private GradeMapper gradeMapper;

    /**
     * Hilfsprogramm für JWT-Token-Verwaltung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Fügt eine Note zu einer spezifischen Unterrichtsstunde hinzu.
     * <p>
     * Nur Lehrer und Administratoren können Noten vergeben.
     * Die Note wird einem Schüler für eine bestimmte Unterrichtsstunde
     * zugeordnet und kann optional eine beschreibende Erklärung enthalten.
     * Validiert die Existenz der Unterrichtsstunde und des Schülers.
     *
     * @param sessionId   ID der Unterrichtsstunde, zu der die Note hinzugefügt wird
     * @param studentId   ID des Schülers, der die Note erhält
     * @param gradeValue  Numerischer Wert der Note
     * @param description Optional: Beschreibende Erklärung oder Kommentar zur Note
     * @return ResponseEntity mit der erstellten Note oder Fehler bei ungültigen Daten
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<GradeDTO> addGradeToSession(@PathVariable Long sessionId,
                                                      @RequestParam Long studentId,
                                                      @RequestParam Double gradeValue,
                                                      @RequestParam(required = false) String description) {
        ClassSession session = classSessionService.getSessionById(sessionId);
        Student student = studentService.getStudentById(studentId);

        Grade grade = new Grade();
        grade.setClassSession(session);
        grade.setStudent(student);
        grade.setGrade(gradeValue);
        grade.setDescription(description);

        Grade saved = gradeService.addGrade(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeMapper.toDto(saved));
    }

    /**
     * Ruft alle Noten im System ab.
     * <p>
     * Zugänglich für Lehrer und Administratoren.
     * Gibt eine vollständige Liste aller im System registrierten Noten zurück,
     * einschließlich aller zugehörigen Schüler- und Unterrichtsstunden-Informationen.
     * Nützlich für Systemübersichten und administrative Berichte.
     *
     * @return ResponseEntity mit der Liste aller Noten im System
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        List<GradeDTO> dtos = gradeService.getAllGrades()
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Aktualisiert eine existierende Note vollständig.
     * <p>
     * Nur Lehrer und Administratoren können Noten aktualisieren.
     * Ermöglicht die vollständige Änderung aller Notenattribute einschließlich
     * Schülerzuordnung, Unterrichtsstunde, Notenwert und Beschreibung.
     * Führt eine komplette Überschreibung der bestehenden Notendaten durch.
     *
     * @param id          ID der zu aktualisierenden Note
     * @param studentId   Neue ID des Schülers für die Note
     * @param sessionId   Neue ID der Unterrichtsstunde für die Note
     * @param gradeValue  Neuer numerischer Wert der Note
     * @param description Neue Beschreibung oder Kommentar zur Note
     * @return ResponseEntity mit der aktualisierten Note oder Fehler bei ungültigen Daten
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable Long id,
                                                @RequestParam Long studentId,
                                                @RequestParam Long sessionId,
                                                @RequestParam Double gradeValue,
                                                @RequestParam(required = false) String description) {
        ClassSession session = classSessionService.getSessionById(sessionId);
        Student student = studentService.getStudentById(studentId);

        Grade updated = new Grade();
        updated.setId(id);
        updated.setStudent(student);
        updated.setClassSession(session);
        updated.setGrade(gradeValue);
        updated.setDescription(description);

        Grade saved = gradeService.updateGrade(id, updated);
        return ResponseEntity.ok(gradeMapper.toDto(saved));
    }

    /**
     * Löscht eine Note vollständig aus dem System.
     * <p>
     * Nur Lehrer und Administratoren können Noten löschen.
     * Führt eine sichere Löschung durch und entfernt alle Referenzen
     * zur Note aus dem System. Die Operation ist irreversibel.
     *
     * @param id ID der zu löschenden Note
     * @return ResponseEntity mit No-Content-Status bei erfolgreichem Löschen
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ruft alle Noten für einen spezifischen Schüler ab.
     * <p>
     * Zugänglich für Lehrer und Administratoren.
     * Filtert alle Noten nach der Schüler-ID und gibt eine vollständige
     * Übersicht über die akademische Leistung des angegebenen Schülers zurück.
     * Nützlich für Leistungsanalysen und Elterngespräche.
     *
     * @param studentId ID des Schülers, dessen Noten abgerufen werden sollen
     * @return ResponseEntity mit der Liste aller Noten des Schülers
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDTO>> getGradesByStudent(@PathVariable Long studentId) {
        List<GradeDTO> grades = gradeService.getGradesByStudent(studentId);
        return ResponseEntity.ok(grades);
    }

    /**
     * Ruft die eigenen Noten für den aktuell angemeldeten Schüler ab.
     * <p>
     * Nur für Schüler zugänglich mit STUDENT-Rolle.
     * Ermöglicht es Schülern, ihre eigenen Noten einzusehen, ohne Zugriff
     * auf Noten anderer Schüler zu haben. Der Schüler wird über den
     * JWT-Token identifiziert und validiert.
     *
     * @param token JWT-Authentifizierungs-Token des angemeldeten Schülers
     * @return ResponseEntity mit der Liste der eigenen Noten oder 404 falls Schüler nicht gefunden
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<List<GradeDTO>> getGradesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<GradeDTO> grades = gradeService.getGradesByStudent(student.getId());
        return ResponseEntity.ok(grades);
    }

    /**
     * Aktualisiert eine Note mit partiellen Daten (flexibles Update).
     * <p>
     * Nur Lehrer und Administratoren können Noten aktualisieren.
     * Ermöglicht die selektive Aktualisierung von Notenattributen ohne
     * vollständige Überschreibung. Unterstützt verschiedene Datentypen
     * für Notenwerte und robuste Fehlerbehandlung bei ungültigen Eingaben.
     * <p>
     * Unterstützte Felder:
     * - "grade": Numerischer Notenwert (als Number oder String)
     * - "description": Textuelle Beschreibung der Note
     * <p>
     * Datentyp-Flexibilität:
     * - Akzeptiert Notenwerte als Number oder String
     * - Automatische Konvertierung und Validierung
     * - Fehlerbehandlung bei ungültigen Formaten
     *
     * @param id         ID der zu aktualisierenden Note
     * @param updateData Map mit den zu aktualisierenden Feldern und Werten
     * @return ResponseEntity mit der aktualisierten Note oder Fehler bei ungültigen Daten
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}/simple")
    public ResponseEntity<GradeDTO> updateGradeSimple(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updateData) {

        try {
            // Erhalte Note als Optional<Grade>
            Optional<Grade> gradeOptional = gradeService.findById(id);

            // Überprüfe ob Note existiert
            if (gradeOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Extrahiere Note aus Optional
            Grade existingGrade = gradeOptional.get();

            // Aktualisiere nur die Werte, die gesendet wurden
            if (updateData.containsKey("grade")) {
                double gradeValue;

                // Behandlung für verschiedene Datentypen, die vom Frontend kommen können
                Object gradeObj = updateData.get("grade");
                if (gradeObj instanceof Number) {
                    gradeValue = ((Number) gradeObj).doubleValue();
                } else if (gradeObj instanceof String) {
                    try {
                        gradeValue = Double.parseDouble((String) gradeObj);
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest()
                                .body(null); // Oder ein Fehler-DTO
                    }
                } else {
                    return ResponseEntity.badRequest()
                            .body(null); // Oder ein Fehler-DTO
                }

                existingGrade.setGrade(gradeValue);
            }

            if (updateData.containsKey("description")) {
                existingGrade.setDescription((String) updateData.get("description"));
            }

            // Speichere aktualisierte Note ohne Änderung anderer Beziehungen
            Grade updated = gradeService.updateGrade(id, existingGrade);
            return ResponseEntity.ok(gradeMapper.toDto(updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Oder ein Fehler-DTO
        }
    }
}