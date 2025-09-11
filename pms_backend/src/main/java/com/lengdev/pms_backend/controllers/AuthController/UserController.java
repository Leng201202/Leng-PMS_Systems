package com.lengdev.pms_backend.controllers.AuthController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lengdev.pms_backend.models.Users;
import com.lengdev.pms_backend.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users user){
        userService.register(user);
        
        return new ResponseEntity<>("Account Created. Please check your email to verify your account.", HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users user, HttpServletResponse response) {
        String token = userService.verify(user);
        
        // Create a cookie with the JWT token
        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setHttpOnly(true); // Prevent access via JavaScript
        jwtCookie.setSecure(false); // Set to true in production with HTTPS
        jwtCookie.setPath("/"); // Available for all paths
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
        
        // Add the cookie to the response
        response.addCookie(jwtCookie);
        
        return new ResponseEntity<>("Login successful", HttpStatus.OK);
    }

    @GetMapping("/register/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = userService.verifyEmail(token);
        if (verified) {
            return new ResponseEntity<>("Email verified successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired token.", HttpStatus.BAD_REQUEST);
        }
    }
}
