package com.cafeteria.cafeteria_plugin.email;

import com.cafeteria.cafeteria_plugin.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public PasswordResetToken createTokenForUser(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(token);
        return token;
    }

    public PasswordResetToken validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired() && !t.isUsed())
                .orElse(null);
    }

    public void markTokenAsUsed(PasswordResetToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }
}
