package com.ycyw.userservice.service;

import com.ycyw.userservice.dto.CreateUserRequest;
import com.ycyw.userservice.dto.LoginRequest;
import com.ycyw.userservice.model.User;
import com.ycyw.userservice.model.UserRole;
import com.ycyw.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Map<String, String> tokenStore = Collections.synchronizedMap(new HashMap<>());

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        ensureDefaultUsers();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(CreateUserRequest request) {
        var existing = userRepository.findByUsername(request.getUsername());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        var user = new User(request.getUsername(), request.getPassword(), request.getRole());
        return userRepository.save(user);
    }

    public boolean deleteUser(String id) {
        return userRepository.deleteById(id);
    }

    public Optional<String> login(LoginRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        var user = userOpt.get();
        if (!user.isActive() || !user.getPassword().equals(request.getPassword())) {
            return Optional.empty();
        }
        var token = generateToken(user.getId());
        tokenStore.put(token, user.getId());
        return Optional.of(token);
    }

    public void logout(String token) {
        tokenStore.remove(token);
    }

    public Optional<String> whoAmI(String token) {
        return Optional.ofNullable(tokenStore.get(token));
    }

    private String generateToken(String userId) {
        return "token-" + userId + "/" + Instant.now().toEpochMilli();
    }

    private void ensureDefaultUsers() {
        if (userRepository.findAll().isEmpty()) {
            var agent = new User("agent", "agent", UserRole.AGENT);
            var client = new User("client", "client", UserRole.CLIENT);
            userRepository.save(agent);
            userRepository.save(client);
        }
    }
}
