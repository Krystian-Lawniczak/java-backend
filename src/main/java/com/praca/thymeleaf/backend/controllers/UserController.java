package com.praca.thymeleaf.backend.controllers;

import com.praca.thymeleaf.backend.models.ChangePasswordRequest;
import com.praca.thymeleaf.backend.models.User;
import com.praca.thymeleaf.backend.services.UserService;
import com.praca.thymeleaf.backend.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody User user) {
        try {
            if (user.getName() == null || user.getPassword() == null || user.getEmail() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Wszystkie pola są wymagane!"));
            }

            if (userService.findByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email jest już zajęty!"));
            }

            user.setId(null);
            User registeredUser = userService.registerUser(user);

            return ResponseEntity.ok(Map.of("message", "Rejestracja udana!", "userId", registeredUser.getId().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Błąd serwera!", "error", e.getMessage()));
        }
    }





    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        String authenticatedEmail = userService.authenticateUser(email, password);
        String token = jwtUtil.generateToken(authenticatedEmail);

        User user = userService.getUserByEmail(authenticatedEmail);

        Map<String, Object> response = Map.of(
                "token", token,
                "user", user
        );

        return ResponseEntity.ok(response);
    }




    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Admin access granted");
    }

    @GetMapping("/user")
    public ResponseEntity<String> userAccess() {
        return ResponseEntity.ok("User access granted");
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
