package com.ycyw.userservice.service;

import com.ycyw.userservice.dto.CreateUserRequest;
import com.ycyw.userservice.dto.LoginRequest;
import com.ycyw.userservice.model.User;
import com.ycyw.userservice.model.UserRole;
import com.ycyw.userservice.repository.JpaUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final JpaUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Map<String, String> tokenStore = Collections.synchronizedMap(new HashMap<>());

    public UserService(JpaUserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        ensureDefaultUsers();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public User createUser(CreateUserRequest request) {
        var existing = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        var user = new User(request.getUsername(), request.getPassword(), request.getRole());
        return userRepository.save(user);
    }

    public boolean deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public Optional<String> login(LoginRequest request) {
        var userOpt = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        var user = userOpt.get();
        if (!user.isActive() || !passwordMatchesAndMaybeMigrate(user, request.getPassword())) {
            return Optional.empty();
        }
        var token = generateToken(user.getId());
        tokenStore.put(token, user.getId());
        System.out.println("DEBUG: User " + user.getUsername() + " (ID: " + user.getId() + ") logged in with token: " + token);
        return Optional.of(token);
    }

    public boolean isUserOnline(String userId) {
        boolean online = tokenStore.values().contains(userId);
        // System.out.println("DEBUG: Checking online status for ID " + userId + ": " + online);
        return online;
    }

    public List<String> getOnlineUserIds() {
        return List.copyOf(tokenStore.values());
    }

    public void logout(String token) {
        String userId = tokenStore.remove(token);
        System.out.println("DEBUG: Logout for token " + token + ". Removed User ID: " + userId);
    }

    public Optional<String> whoAmI(String token) {
        return Optional.ofNullable(tokenStore.get(token));
    }

    private String generateToken(String userId) {
        return "token-" + userId + "/" + Instant.now().toEpochMilli();
    }

    private void ensureDefaultUsers() {
        if (userRepository.count() == 0) {
            var agent = new User("agent", "agent", UserRole.AGENT);
            var client = new User("client", "client", UserRole.CLIENT);
            userRepository.save(agent);
            userRepository.save(client);
        }
    }

    /**
     * Compat rétro: si password_hash existe => BCrypt.
     * Sinon => on accepte l'ancien champ password (en clair) et on migre vers password_hash.
     */
    private boolean passwordMatchesAndMaybeMigrate(User user, String rawPassword) {
        var hash = user.getPasswordHash();
        if (hash != null && !hash.isBlank()) {
            return passwordEncoder.matches(rawPassword, hash);
        }

        // legacy: plain-text
        if (user.getPassword() == null || !user.getPassword().equals(rawPassword)) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        return true;
    }
}
