package com.ycyw.userservice.controller;

import com.ycyw.userservice.dto.CreateUserRequest;
import com.ycyw.userservice.dto.LoginRequest;
import com.ycyw.userservice.dto.UserResponse;
import com.ycyw.userservice.model.User;
import com.ycyw.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Tag(name = "User Service", description = "Endpoints pour gérer les utilisateurs et l'authentification")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Liste des utilisateurs")
    public List<UserResponse> listUsers() {
        return userService.getAllUsers().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère un utilisateur par son ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") String id) {
        return userService.getUserById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @Operation(summary = "Crée un nouvel utilisateur")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un utilisateur")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        return userService.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion (retourne un token)")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request)
                .map(token -> ResponseEntity.ok().body(new LoginResponse(token)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion")
    public ResponseEntity<Void> logout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        userService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Récupère l'utilisateur connecté à partir du token")
    public ResponseEntity<UserResponse> whoAmI(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return userService.whoAmI(token)
                .flatMap(userService::getUserById)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private UserResponse toResponse(User user) {
        boolean online = userService.isUserOnline(user.getId());
        if (online) {
            System.out.println("DEBUG: User " + user.getUsername() + " is currently ONLINE");
        }
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), online);
    }

    private static class LoginResponse {
        public final String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }
}
