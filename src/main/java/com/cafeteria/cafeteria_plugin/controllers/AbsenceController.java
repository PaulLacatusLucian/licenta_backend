package com.cafeteria.cafeteria_plugin.controllers;
import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import com.cafeteria.cafeteria_plugin.services.ClassSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/absences")
public class AbsenceController {

    private final AbsenceService absenceService;
    private final ClassSessionService classSessionService;

    public AbsenceController(AbsenceService absenceService, ClassSessionService classSessionService) {
        this.absenceService = absenceService;
        this.classSessionService = classSessionService;
    }

    @PostMapping
    public ResponseEntity<Absence> addAbsence(@RequestBody Absence absence) {
        return ResponseEntity.ok(absenceService.addAbsence(absence));
    }

    @GetMapping
    public List<Absence> getAllAbsences() {
        return absenceService.getAllAbsences();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Absence> getAbsenceById(@PathVariable Long id) {
        Optional<Absence> absence = absenceService.getAbsenceById(id);
        return absence.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Absence> updateAbsence(@PathVariable Long id, @RequestBody Absence updatedAbsence) {
        return ResponseEntity.ok(absenceService.updateAbsence(id, updatedAbsence));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/session/{sessionId}")
    public ResponseEntity<Absence> addAbsenceToSession(@PathVariable Long sessionId, @RequestBody Absence absence) {
        // Găsește sesiunea asociată
        ClassSession session = classSessionService.getSessionById(sessionId);

        // Asociază sesiunea cu absența
        absence.setClassSession(session);

        // Salvează absența
        Absence savedAbsence = absenceService.addAbsence(absence);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAbsence);
    }

}
