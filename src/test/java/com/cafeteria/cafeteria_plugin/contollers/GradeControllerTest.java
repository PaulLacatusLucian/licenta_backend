package com.cafeteria.cafeteria_plugin.contollers;

import com.cafeteria.cafeteria_plugin.controllers.GradeController;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.services.GradeService;
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

public class GradeControllerTest {

    @Mock
    private GradeService gradeService;

    @Mock
    private SemesterService semesterService;

    @InjectMocks
    private GradeController gradeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddGrade() {
        Long studentId = 1L;
        Long teacherId = 2L;
        Long semesterId = 3L; // Simulated semester ID
        Double gradeValue = 9.5;

        Grade grade = new Grade();
        grade.setId(1L);
        grade.setGrade(gradeValue);

        when(gradeService.addGrade(studentId, teacherId, semesterId, gradeValue)).thenReturn(grade);

        ResponseEntity<Grade> response = gradeController.addGrade(studentId, teacherId, semesterId, gradeValue);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue()); // 201 status
        assertEquals(grade, response.getBody());
    }

    @Test
    public void testAddGradeWithDefaultSemester() {
        Long studentId = 1L;
        Long teacherId = 2L;
        Double gradeValue = 9.5;

        Long currentSemesterId = 3L; // Mock current semester
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setGrade(gradeValue);

        when(semesterService.getCurrentSemester().getId()).thenReturn(currentSemesterId);
        when(gradeService.addGrade(studentId, teacherId, currentSemesterId, gradeValue)).thenReturn(grade);

        ResponseEntity<Grade> response = gradeController.addGrade(studentId, teacherId, null, gradeValue);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue()); // 201 status
        assertEquals(grade, response.getBody());
    }

    @Test
    public void testGetGradeById() {
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setGrade(9.5);

        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));

        ResponseEntity<Grade> response = gradeController.getGradeById(1L);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue()); // 200 status
        assertEquals(grade, response.getBody());
    }

    @Test
    public void testUpdateGrade() {
        Long gradeId = 1L;
        Long studentId = 1L;
        Long teacherId = 2L;
        Long semesterId = 3L;
        Double gradeValue = 10.0;

        Grade updatedGrade = new Grade();
        updatedGrade.setId(gradeId);
        updatedGrade.setGrade(gradeValue);

        when(gradeService.updateGrade(gradeId, studentId, teacherId, semesterId, gradeValue)).thenReturn(updatedGrade);

        ResponseEntity<Grade> response = gradeController.updateGrade(gradeId, studentId, teacherId, semesterId, gradeValue);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue()); // 200 status
        assertEquals(updatedGrade, response.getBody());
    }

    @Test
    public void testUpdateGradeWithDefaultSemester() {
        Long gradeId = 1L;
        Long studentId = 1L;
        Long teacherId = 2L;
        Double gradeValue = 10.0;

        Long currentSemesterId = 3L; // Mock current semester
        Grade updatedGrade = new Grade();
        updatedGrade.setId(gradeId);
        updatedGrade.setGrade(gradeValue);

        when(semesterService.getCurrentSemester().getId()).thenReturn(currentSemesterId);
        when(gradeService.updateGrade(gradeId, studentId, teacherId, currentSemesterId, gradeValue)).thenReturn(updatedGrade);

        ResponseEntity<Grade> response = gradeController.updateGrade(gradeId, studentId, teacherId, null, gradeValue);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue()); // 200 status
        assertEquals(updatedGrade, response.getBody());
    }

    @Test
    public void testDeleteGrade() {
        Long gradeId = 1L;

        doNothing().when(gradeService).deleteGrade(gradeId);

        ResponseEntity<Void> response = gradeController.deleteGrade(gradeId);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue()); // 204 status
    }
}
