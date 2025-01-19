//package com.cafeteria.cafeteria_plugin.contollers;
//
//import com.cafeteria.cafeteria_plugin.models.Grade;
//import com.cafeteria.cafeteria_plugin.services.GradeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class GradeControllerTest {
//
//    @Mock
//    private GradeService gradeService;
//
//    @InjectMocks
//    private GradeController gradeController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testAddGrade() {
//        Long studentId = 1L;
//        Long teacherId = 2L;
//        Double gradeValue = 9.5;
//
//        Grade grade = new Grade();
//        grade.setId(1L);
//        grade.setGrade(gradeValue);
//
//        when(gradeService.addGrade(studentId, teacherId, gradeValue)).thenReturn(grade);
//
//        ResponseEntity<Grade> response = gradeController.addGrade(studentId, teacherId, gradeValue);
//
//        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue()); // 201 status
//        assertEquals(grade, response.getBody());
//    }
//
//    @Test
//    public void testGetGradeById() {
//        Grade grade = new Grade();
//        grade.setId(1L);
//        grade.setGrade(9.5);
//
//        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));
//
//        ResponseEntity<Grade> response = gradeController.getGradeById(1L);
//        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue()); // 200 status
//        assertEquals(grade, response.getBody());
//    }
//
//    @Test
//    public void testUpdateGrade() {
//        Long gradeId = 1L;
//        Long studentId = 1L;
//        Long teacherId = 2L;
//        Double gradeValue = 10.0;
//
//        Grade updatedGrade = new Grade();
//        updatedGrade.setId(gradeId);
//        updatedGrade.setGrade(gradeValue);
//
//        when(gradeService.updateGrade(gradeId, studentId, teacherId, gradeValue)).thenReturn(updatedGrade);
//
//        ResponseEntity<Grade> response = gradeController.updateGrade(gradeId, studentId, teacherId, gradeValue);
//
//        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue()); // 200 status
//        assertEquals(updatedGrade, response.getBody());
//    }
//
//    @Test
//    public void testDeleteGrade() {
//        Long gradeId = 1L;
//
//        doNothing().when(gradeService).deleteGrade(gradeId);
//
//        ResponseEntity<Void> response = gradeController.deleteGrade(gradeId);
//        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue()); // 204 status
//    }
//}
