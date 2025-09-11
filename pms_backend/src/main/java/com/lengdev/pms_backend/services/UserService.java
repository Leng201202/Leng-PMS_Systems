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

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    public Users register(Users user) {
        Users existingUser = userRepo.findByEmail(user.getEmail());
        
       try {
         if(existingUser != null){
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
         }else{
            if(user.getEmail()==null || user.getPassword()==null){
            throw new RuntimeException("Email and Password are required");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmail(user.getEmail());
            user.setRole(user.getRole() != null ? user.getRole() : Users.Role.TENANT);

            return userRepo.save(user);
         }
         
       } catch (Exception e) {
        e.printStackTrace();

        throw new RuntimeException("Registration failed: " + e.getMessage());
       }

    }
    public String verify(Users user){
        Authentication auth=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(auth.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());
        }
        return "Error in Login";
    }
}
