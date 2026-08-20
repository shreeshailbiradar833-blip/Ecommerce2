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

        if (userRepo.existsByUsername(
                user.getUsername()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body("Username already exists");
        }


        if (userRepo.existsByEmail(
                user.getEmail()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body("Email already exists");
        }


        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );


        user.setRole("USER");


        Users savedUser =
                userRepo.save(user);


        savedUser.setPassword(null);


        return ResponseEntity.ok(savedUser);
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


            if (authentication.isAuthenticated()) {

                String token =
                        jwtService.generateToken(
                                user.getUsername()
                        );


                return ResponseEntity.ok(
                        token
                );
            }


            return ResponseEntity
                    .status(401)
                    .body("Invalid credentials");

        } catch (Exception e) {

            return ResponseEntity
                    .status(401)
                    .body("Invalid username or password");
        }
    }
}