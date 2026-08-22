package com.Shree.ecom_web.controller;

import com.Shree.ecom_web.model.Users;
import com.Shree.ecom_web.repository.UserRepo;
import com.Shree.ecom_web.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;


    // ==========================================
    // REGISTER
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Users user
    ) {

        try {

            // Check username
            if (userRepo.existsByUsername(
                    user.getUsername()
            )) {

                return ResponseEntity
                        .badRequest()
                        .body("Username already exists");
            }


            // Check email
            if (userRepo.existsByEmail(
                    user.getEmail()
            )) {

                return ResponseEntity
                        .badRequest()
                        .body("Email already exists");
            }


            // Encode password
            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );


            // Set default role
            user.setRole("USER");


            // Save user
            Users savedUser =
                    userRepo.save(user);


            // Don't send password
            savedUser.setPassword(null);


            return ResponseEntity.ok(
                    savedUser
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Registration failed: "
                                    + e.getMessage()
                    );
        }
    }


    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Users user
    ) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    user.getPassword()
                            )
                    );

            if (!authentication.isAuthenticated()) {
                return ResponseEntity
                        .status(401)
                        .body("Invalid username or password");
            }

            // Generate JWT
            String token =
                    jwtService.generateToken(
                            user.getUsername()
                    );

            // Get the actual user from database
            Users loggedUser =
                    userRepo.findByUsername(
                            user.getUsername()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "User not found"
                            )
                    );

            // Return token + user ID + username
            Map<String, Object> response =
                    new HashMap<>();

            response.put("token", token);
            response.put("userId", loggedUser.getId());
            response.put("username", loggedUser.getUsername());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(401)
                    .body("Invalid username or password");
        }
    }
}