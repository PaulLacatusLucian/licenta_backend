package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint pentru înregistrare
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            String username = user.getUsername();
            if (username == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username este necesar"));
            }

            // Determină tipul utilizatorului
            if (username.endsWith(".teacher")) {
                user.setUserType("teacher");
                user.setEmployee(true);
            } else if (username.endsWith(".stud")) {
                user.setUserType("student");
                user.setEmployee(false);
            } else if (username.endsWith(".parent")) {
                user.setUserType("parent");
                user.setEmployee(false);
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Format invalid pentru username"));
            }

            // Creează utilizatorul
            User createdUser = userService.createUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User înregistrat cu succes");
            response.put("username", createdUser.getUsername());
            response.put("userType", createdUser.getUserType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Eroare la înregistrare: " + e.getMessage()));
        }
    }

    // Endpoint pentru login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username și parola sunt necesare"));
            }

            User user = userService.findByUsername(username);

            // Validează utilizatorul și parola
            if (user != null && user.getPassword().equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("userType", user.getUserType());
                response.put("isEmployee", user.isEmployee());

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credentiale invalide"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Eroare la autentificare: " + e.getMessage()));
        }
    }
}
