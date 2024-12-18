package com.cafeteria.cafeteria_plugin.contollers;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import com.cafeteria.cafeteria_plugin.controllers.AbsenceController;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AbsenceControllerTest {

    @Mock
    private AbsenceService absenceService;

    @InjectMocks
    private AbsenceController absenceController;

    private Absence absence;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        absence = new Absence();
        absence.setId(1L);
        absence.setSubject("Math");
        absence.setCount(3);
    }

    @Test
    public void testAddAbsence() {
        when(absenceService.addAbsence(any(Absence.class))).thenReturn(absence);

        ResponseEntity<Absence> response = absenceController.addAbsence(absence);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(absence, response.getBody());
    }

    @Test
    public void testGetAbsenceById() {
        when(absenceService.getAbsenceById(1L)).thenReturn(Optional.of(absence));

        ResponseEntity<Absence> response = absenceController.getAbsenceById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(absence, response.getBody());
    }

    @Test
    public void testUpdateAbsence() {
        Absence updatedAbsence = new Absence();
        updatedAbsence.setCount(5);
        when(absenceService.updateAbsence(1L, updatedAbsence)).thenReturn(updatedAbsence);

        ResponseEntity<Absence> response = absenceController.updateAbsence(1L, updatedAbsence);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedAbsence, response.getBody());
    }

    @Test
    public void testDeleteAbsence() {
        doNothing().when(absenceService).deleteAbsence(1L);

        ResponseEntity<Void> response = absenceController.deleteAbsence(1L);
        assertEquals(204, response.getStatusCodeValue());
    }
}
