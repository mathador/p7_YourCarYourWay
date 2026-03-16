package com.ycyw.userservice.controller;

import com.ycyw.userservice.dto.CreateUserRequest;
import com.ycyw.userservice.dto.LoginRequest;
import com.ycyw.userservice.dto.UserResponse;
import com.ycyw.userservice.model.User;
import com.ycyw.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(u -> ResponseEntity.ok(toResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @RequestHeader(value = "X-Auth-Token", required = false) String xToken,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String token = xToken;
        if (token == null && authHeader != null) {
            token = authHeader.replace("Bearer ", "");
        }
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return userService.getUserByToken(token)
                .map(u -> ResponseEntity.ok(toResponse(u)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest request) {
        String token = userService.login(request);
        if (token != null) {
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "X-Auth-Token", required = false) String xToken,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String token = xToken;
        if (token == null && authHeader != null) {
            token = authHeader.replace("Bearer ", "");
        }
        if (token != null) {
            userService.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/online")
    public List<UserResponse> getOnlineUsers() {
        List<Integer> onlineIds = userService.getOnlineUserIds();
        return userService.getAllUsers().stream()
                .filter(u -> onlineIds.contains(u.getId()))
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}/online")
    public Map<String, Boolean> isOnline(@PathVariable Integer id) {
        return Map.of("online", userService.isUserOnline(id));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRole().name(),
                u.isActive(),
                u.isPshProfile()
        );
    }
}
