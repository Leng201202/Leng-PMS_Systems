package com.lengdev.pms_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lengdev.pms_backend.models.Users;
import com.lengdev.pms_backend.repos.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    public Users register(Users user) {
        Users existingUser = userRepo.findByEmail(user.getEmail());

        try {
            if (existingUser != null) {
                throw new RuntimeException("User with email " + user.getEmail() + " already exists");
            } else {
                if (user.getEmail() == null || user.getPassword() == null) {
                    throw new RuntimeException("Email and Password are required");
                }
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setEmail(user.getEmail());
                user.setRole(user.getRole() != null ? user.getRole() : Users.Role.TENANT);
                user.setVerified(false); // Set as not verified initially
                user.setVerificationToken(generateVerificationToken()); // Generate token

                Users savedUser = userRepo.save(user);

                // Send verification email
                emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

                return savedUser;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    private String generateVerificationToken() {
        // Generate a simple token; in production, use a more secure method
        return java.util.UUID.randomUUID().toString();
    }

    public boolean verifyEmail(String token) {
        Users user = userRepo.findByVerificationToken(token);
        if (user != null && !user.isVerified()) {
            user.setVerified(true);
            user.setVerificationToken(null); // Clear token after verification
            userRepo.save(user);
            return true;
        }
        return false;
    }
    public String verify(Users user){
        Authentication auth=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(auth.isAuthenticated()){
            user.setVerificationToken(jwtService.generateToken(user.getUsername()));
            return user.getVerificationToken();
        }
        return "Error in Login";
    }
    
}
