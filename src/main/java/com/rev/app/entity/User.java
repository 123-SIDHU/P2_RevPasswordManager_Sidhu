package com.rev.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "MASTER_PASSWORD_HASH", nullable = false)
    private String masterPasswordHash;

    private String name;

    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "TWO_FACTOR_ENABLED")
    private boolean twoFactorEnabled;

    public User() {}

    public User(String username, String masterPasswordHash, String name, String email, String phoneNumber) {
        this.username = username;
        this.masterPasswordHash = masterPasswordHash;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.twoFactorEnabled = false;
    }

    public User(int id, String username, String masterPasswordHash, String name, String email, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.masterPasswordHash = masterPasswordHash;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.twoFactorEnabled = false;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getMasterPasswordHash() { return masterPasswordHash; }
    public void setMasterPasswordHash(String hash) { this.masterPasswordHash = hash; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
}
