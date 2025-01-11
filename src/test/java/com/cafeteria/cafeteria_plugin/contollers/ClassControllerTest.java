package com.cafeteria.cafeteria_plugin.contollers;
import com.cafeteria.cafeteria_plugin.controllers.ClassController;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.services.ClassService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClassControllerTest {

    @Mock
    private ClassService classService;

    @InjectMocks
    private ClassController classController;

    private Class studentClass;
    private Teacher teacher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        studentClass = new Class();
        studentClass.setId(1L);
        studentClass.setName("10A");

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("John Doe");
    }

    @Test
    public void testAddClass() {
        when(classService.findTeacherById(1L)).thenReturn(teacher);
        when(classService.addClass(any(Class.class))).thenReturn(studentClass);

        ResponseEntity<Class> response = classController.addClass(studentClass, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(studentClass, response.getBody());
        verify(classService, times(1)).findTeacherById(1L);
        verify(classService, times(1)).addClass(studentClass);
    }

    @Test
    public void testGetClassById() {
        when(classService.getClassById(1L)).thenReturn(Optional.of(studentClass));

        ResponseEntity<Class> response = classController.getClassById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(studentClass, response.getBody());
        verify(classService, times(1)).getClassById(1L);
    }

    @Test
    public void testUpdateClass() {
        Class updatedClass = new Class();
        updatedClass.setName("11A");

        when(classService.findTeacherById(1L)).thenReturn(teacher);
        when(classService.updateClass(1L, updatedClass)).thenReturn(updatedClass);

        ResponseEntity<Class> response = classController.updateClass(1L, updatedClass, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedClass, response.getBody());
        verify(classService, times(1)).findTeacherById(1L);
        verify(classService, times(1)).updateClass(1L, updatedClass);
    }

    @Test
    public void testDeleteClass() {
        doNothing().when(classService).deleteClass(1L);

        ResponseEntity<Void> response = classController.deleteClass(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(classService, times(1)).deleteClass(1L);
    }
}
