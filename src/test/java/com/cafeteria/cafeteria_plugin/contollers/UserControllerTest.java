//package com.cafeteria.cafeteria_plugin.contollers;
//
//import com.cafeteria.cafeteria_plugin.models.User;
//import com.cafeteria.cafeteria_plugin.services.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//import com.cafeteria.cafeteria_plugin.controllers.UserController;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private User user;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = new User();
//        user.setId(1L);
//        user.setUsername("john_doe");
//        user.setPassword("password123");
//        user.setEmployee(true);
//    }
//
//    @Test
//    public void testRegisterUser() {
//        when(userService.createUser(any(User.class))).thenReturn(user);
//
//        ResponseEntity<Map<String, String>> response = userController.registerUser(user);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("User registered successfully", response.getBody().get("message"));
//        assertEquals(user.getUsername(), response.getBody().get("username"));
//    }
//
//    @Test
//    public void testLoginUser() {
//        when(userService.findByUsername("john_doe")).thenReturn(user);
//
//        Map<String, String> credentials = Map.of("username", "john_doe", "password", "password123");
//        ResponseEntity<Map<String, Object>> response = userController.loginUser(credentials);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(user.getId(), response.getBody().get("id"));
//        assertEquals(user.getUsername(), response.getBody().get("username"));
//    }
//
//    @Test
//    public void testLoginInvalidCredentials() {
//        when(userService.findByUsername("john_doe")).thenReturn(null);
//
//        Map<String, String> credentials = Map.of("username", "john_doe", "password", "wrong_password");
//        ResponseEntity<Map<String, Object>> response = userController.loginUser(credentials);
//
//        assertEquals(401, response.getStatusCodeValue());
//        assertEquals("Invalid credentials", response.getBody().get("message"));
//    }
//}
