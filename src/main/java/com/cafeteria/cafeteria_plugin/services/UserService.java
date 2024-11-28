package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
}

