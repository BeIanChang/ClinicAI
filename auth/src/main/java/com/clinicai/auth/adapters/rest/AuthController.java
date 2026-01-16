package com.clinicai.auth.adapters.rest;

import com.clinicai.auth.app.JwtTokenProvider;
import com.clinicai.auth.domain.model.User;
import com.clinicai.auth.domain.model.UserRole;
import com.clinicai.auth.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    
    public AuthController(AuthService authService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or username already exists")
    })
    public ResponseEntity<Map<String, Object>> register(
            @Parameter(description = "Registration details", required = true)
            @RequestBody RegisterRequest request) {
        
        try {
            User user = authService.registerUser(
                request.getUsername(), 
                request.getPassword(), 
                request.getEmail(), 
                request.getRole()
            );
            
            String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole().name()
            ));
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Map<String, Object>> login(
            @Parameter(description = "Login credentials", required = true)
            @RequestBody LoginRequest request) {
        
        return authService.authenticateUser(request.getUsername(), request.getPassword())
            .map(user -> {
                String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "role", user.getRole().name()
                ));
                
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validates the provided JWT token")
    public ResponseEntity<Map<String, Object>> validateToken(
            @Parameter(description = "JWT token to validate", required = true)
            @RequestBody Map<String, String> request) {
        
        String token = request.get("token");
        if (tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            String role = tokenProvider.getRoleFromToken(token);
            
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username,
                "role", role
            ));
        } else {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
    
    // DTOs
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private UserRole role;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}