package com.cafeteria.cafeteria_plugin.contollers;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import com.cafeteria.cafeteria_plugin.controllers.ClassController;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClassControllerTest {

    @Mock
    private ClassService classService;

    @InjectMocks
    private ClassController classController;

    private Class studentClass;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        studentClass = new Class();
        studentClass.setId(1L);
        studentClass.setName("10A");
    }

    @Test
    public void testAddClass() {
        when(classService.addClass(any(Class.class))).thenReturn(studentClass);

        ResponseEntity<Class> response = classController.addClass(studentClass);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(studentClass, response.getBody());
    }

    @Test
    public void testGetClassById() {
        when(classService.getClassById(1L)).thenReturn(Optional.of(studentClass));

        ResponseEntity<Class> response = classController.getClassById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(studentClass, response.getBody());
    }

    @Test
    public void testUpdateClass() {
        Class updatedClass = new Class();
        updatedClass.setName("11A");
        when(classService.updateClass(1L, updatedClass)).thenReturn(updatedClass);

        ResponseEntity<Class> response = classController.updateClass(1L, updatedClass);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedClass, response.getBody());
    }

    @Test
    public void testDeleteClass() {
        doNothing().when(classService).deleteClass(1L);

        ResponseEntity<Void> response = classController.deleteClass(1L);
        assertEquals(204, response.getStatusCodeValue());
    }
}
