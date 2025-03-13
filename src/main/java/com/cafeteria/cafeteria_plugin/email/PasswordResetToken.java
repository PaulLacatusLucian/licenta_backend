package com.cafeteria.cafeteria_plugin.email;

import com.cafeteria.cafeteria_plugin.models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    private LocalDateTime expiryDate;

    private boolean used = false;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}