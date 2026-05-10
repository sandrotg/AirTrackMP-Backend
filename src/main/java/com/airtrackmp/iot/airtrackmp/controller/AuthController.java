package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.*;
import com.airtrackmp.iot.airtrackmp.entity.User;
import com.airtrackmp.iot.airtrackmp.repository.UserRepository;
import com.airtrackmp.iot.airtrackmp.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@RequestBody AuthRequest request) {

        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        boolean validPassword = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!validPassword) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse(token)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register (@RequestBody RegisterRequest request) {

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
        userRepo.save(user);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}