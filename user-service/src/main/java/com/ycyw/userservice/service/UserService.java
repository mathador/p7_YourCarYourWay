package com.ycyw.userservice.service;

import com.ycyw.userservice.dto.CreateUserRequest;
import com.ycyw.userservice.dto.LoginRequest;
import com.ycyw.userservice.model.User;
import com.ycyw.userservice.model.UserRole;
import com.ycyw.userservice.repository.JpaUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final JpaUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Token -> UserID (Integer)
    private final Map<String, Integer> tokenStore = new ConcurrentHashMap<>();

    public UserService(JpaUserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        ensureDefaultUsers();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
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
        var user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getRole());
        return userRepository.save(user);
    }

    public boolean deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public String login(LoginRequest request) {
        var userOpt = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (userOpt.isEmpty()) {
            return null;
        }
        var user = userOpt.get();
        if (!user.isActive() || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return null;
        }
        var token = generateToken(user.getId());
        tokenStore.put(token, user.getId());
        return token;
    }

    public boolean isUserOnline(Integer userId) {
        return tokenStore.values().contains(userId);
    }

    public List<Integer> getOnlineUserIds() {
        return tokenStore.values().stream().toList();
    }

    public Optional<User> getUserByToken(String token) {
        Integer userId = tokenStore.get(token);
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    public void logout(String token) {
        tokenStore.remove(token);
    }

    private String generateToken(Integer userId) {
        return "token-" + userId + "/" + Instant.now().toEpochMilli();
    }

    private void ensureDefaultUsers() {
        if (userRepository.count() == 0) {
            var agent = new User("agent", passwordEncoder.encode("agent"), UserRole.AGENT);
            var client = new User("client", passwordEncoder.encode("client"), UserRole.CLIENT);
            userRepository.save(agent);
            userRepository.save(client);
        }
    }
}
