package com.lengdev.pms_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    @Column(unique=true)
    private String email;
    private String verificationToken;
    private boolean isVerified;

    @Column(name="reset_token")
    private String resetToken; 

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        TENANT, OWNER, ADMIN, VENDOR
    }
}
