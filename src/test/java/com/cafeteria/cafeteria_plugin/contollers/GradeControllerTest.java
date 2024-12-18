package com.cafeteria.cafeteria_plugin.contollers;

import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.services.GradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cafeteria.cafeteria_plugin.controllers.GradeController;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GradeControllerTest {

    @Mock
    private GradeService gradeService;

    @InjectMocks
    private GradeController gradeController;

    private Grade grade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        grade = new Grade();
        grade.setId(1L);
        grade.setSubject("Math");
        grade.setGrade(9.5);
        grade.setTeacher("Mr. Popescu");
    }

    @Test
    public void testAddGrade() {
        when(gradeService.addGrade(any(Grade.class))).thenReturn(grade);

        ResponseEntity<Grade> response = gradeController.addGrade(grade);

        // Expecting a 201 Created status code for a successful creation
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue()); // 201 status
        assertEquals(grade, response.getBody());
    }

    @Test
    public void testGetGradeById() {
        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));

        ResponseEntity<Grade> response = gradeController.getGradeById(1L);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue()); // 200 status
        assertEquals(grade, response.getBody());
    }

    @Test
    public void testUpdateGrade() {
        // Prepare the updated grade
        Grade updatedGrade = new Grade();
        updatedGrade.setId(1L);  // Ensure the updated grade has the correct ID
        updatedGrade.setGrade(10.0);  // Set the updated grade value

        // Mock the gradeService to return the existing grade when getGradeById is called
        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));  // Grade with ID 1L exists

        // Mock the gradeService to return the updated grade when updateGrade is called
        when(gradeService.updateGrade(1L, updatedGrade)).thenReturn(updatedGrade);

        // Call the controller method
        ResponseEntity<Grade> response = gradeController.updateGrade(1L, updatedGrade);

        // Assert the correct response
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());  // 200 status
        assertEquals(updatedGrade, response.getBody());  // The body should contain the updated grade
    }


    @Test
    public void testDeleteGrade() {
        doNothing().when(gradeService).deleteGrade(1L);

        ResponseEntity<Void> response = gradeController.deleteGrade(1L);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue()); // 204 status
    }
}
