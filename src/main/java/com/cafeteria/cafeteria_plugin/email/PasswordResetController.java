package com.cafeteria.cafeteria_plugin.email;

import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("username") String username, Model model) {
        model.addAttribute("username", username);
        return "reset-password-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("username") String username,
                                @RequestParam("newPassword") String newPassword,
                                Model model) {
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Utilizatorul nu a fost găsit!");
            return "reset-password-form";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.forceUpdatePassword(user);
        model.addAttribute("success", "Parola a fost schimbată cu succes!");
        return "reset-password-form";
    }
}
