package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Semester;
import com.cafeteria.cafeteria_plugin.services.SemesterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SemesterControllerTest {

    @Mock
    private SemesterService semesterService;

    @InjectMocks
    private SemesterController semesterController;

    private Semester semester;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        semester = new Semester();
        semester.setId(1L);
        semester.setCurrentSemester(1);
    }

    @Test
    public void testGetCurrentSemester() {
        when(semesterService.getCurrentSemester()).thenReturn(semester);

        ResponseEntity<Semester> response = semesterController.getCurrentSemester();

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(semester, response.getBody());
    }

    @Test
    public void testIncrementSemester() {
        Semester updatedSemester = new Semester();
        updatedSemester.setId(1L);
        updatedSemester.setCurrentSemester(2);

        when(semesterService.incrementSemester()).thenReturn(updatedSemester);

        ResponseEntity<Semester> response = semesterController.incrementSemester();

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(updatedSemester, response.getBody());
        assertEquals(2, response.getBody().getCurrentSemester());
    }
}
