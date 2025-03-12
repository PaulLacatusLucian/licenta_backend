package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<Object> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

