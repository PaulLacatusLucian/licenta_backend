package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        System.out.println("Username primit: " + username);
        System.out.println("Password primit: " + password);

        User user = userService.findByUsername(username);
        if (user != null) {
            System.out.println("User găsit: " + user.getUsername());
            System.out.println("Password salvat: " + user.getPassword());

            if (user.getPassword().equals(password)) {
                return ResponseEntity.ok(user);
            } else {
                System.out.println("Parola incorectă");
            }
        } else {
            System.out.println("User inexistent");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
