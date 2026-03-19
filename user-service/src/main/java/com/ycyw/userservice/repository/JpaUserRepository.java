package com.ycyw.userservice.repository;

import com.ycyw.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameIgnoreCase(String username);
}

