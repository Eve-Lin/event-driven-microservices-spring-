package com.security.auth.controller;

import com.security.auth.entity.User;
import com.security.auth.repository.UserRepository;
import com.security.auth.config.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepo,
                          PasswordEncoder encoder,
                          JwtUtils jwtUtils) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    public void signup(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("USER");
        System.out.println("The user is: " + user.getEmail()+ " " + user.getPassword() +  " "+ user.getRole());
        userRepo.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User request) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtils.generateToken(user.getEmail(), user.getRole());
    }
}