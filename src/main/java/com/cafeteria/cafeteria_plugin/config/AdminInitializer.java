package com.cafeteria.cafeteria_plugin.config;

import com.cafeteria.cafeteria_plugin.models.Admin;
import com.cafeteria.cafeteria_plugin.models.User.UserType;
import com.cafeteria.cafeteria_plugin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String defaultUsername = "admin_user.admin";
        String defaultEmail = "admin@cafeteria.com";

        boolean adminExists = userRepository.existsByUsername(defaultUsername);

        if (!adminExists) {
            Admin admin = new Admin();
            admin.setUsername(defaultUsername);
            admin.setEmail(defaultEmail);
            admin.setPassword(passwordEncoder.encode("admin123!"));
            admin.setUserType(UserType.ADMIN);

            adminRepository.save(admin);
            System.out.println("✅ Admin default creat: " + defaultUsername + " / admin123!");
        } else {
            System.out.println("✅ Admin default deja există.");
        }
    }
}
