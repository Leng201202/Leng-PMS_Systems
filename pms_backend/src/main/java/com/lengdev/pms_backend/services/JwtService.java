package com.lengdev.pms_backend.services;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private String secretKey="";

    public JwtService() {
        try {
            KeyGenerator keyGenerator=KeyGenerator.getInstance("HmacSHA256");
            SecretKey key=keyGenerator.generateKey();
            secretKey=Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate secret key");
        }
    }
    public String generateToken(String email) {
        
        Map<String,Object> claims=new HashMap<>();
        return Jwts
            .builder()
            .claims()
            .add(claims)
            .subject(email)
            .issuedAt(new java.util.Date(System.currentTimeMillis()))
            .expiration(new java.util.Date(System.currentTimeMillis()+1000*60*60*10)) //10 hours
            .and()
            .signWith(getKey())
            .compact();

    }

    private Key getKey() {
       return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    public String extractEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey)getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userdetails) {
        final String email=extractEmail(token);
        return (email.equals(userdetails.getUsername()) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    
}
