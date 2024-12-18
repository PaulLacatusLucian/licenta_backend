package com.cafeteria.cafeteria_plugin.contollers;

import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import com.cafeteria.cafeteria_plugin.controllers.TeacherController;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("John Doe");
        teacher.setSubject("Math");
    }

    @Test
    public void testAddTeacher() {
        when(teacherService.addTeacher(any(Teacher.class))).thenReturn(teacher);

        ResponseEntity<Teacher> response = teacherController.addTeacher(teacher);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(teacher, response.getBody());
    }

    @Test
    public void testGetTeacherById() {
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));

        ResponseEntity<Teacher> response = teacherController.getTeacherById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(teacher, response.getBody());
    }

    @Test
    public void testUpdateTeacher() {
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setName("Jane Doe");
        updatedTeacher.setSubject("Science");

        when(teacherService.updateTeacher(1L, updatedTeacher)).thenReturn(updatedTeacher);

        ResponseEntity<Teacher> response = teacherController.updateTeacher(1L, updatedTeacher);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedTeacher, response.getBody());
    }

    @Test
    public void testDeleteTeacher() {
        doNothing().when(teacherService).deleteTeacher(1L);

        ResponseEntity<Void> response = teacherController.deleteTeacher(1L);
        assertEquals(204, response.getStatusCodeValue());
    }
}
