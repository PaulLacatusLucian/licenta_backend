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
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        PasswordResetToken resetToken = passwordResetService.validateToken(token);

        if (resetToken == null) {
            return "reset-password-expired";
        }

        if (resetToken.isUsed()) {
            return "reset-password-already-used";
        }

        model.addAttribute("token", token);
        return "reset-password-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                Model model) {
        PasswordResetToken resetToken = passwordResetService.validateToken(token);

        if (resetToken == null) {
            return "reset-password-expired";
        }

        if (resetToken.isUsed()) {
            return "reset-password-already-used";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.forceUpdatePassword(user);
        passwordResetService.markTokenAsUsed(resetToken);

        return "reset-password-success";
    }

}
