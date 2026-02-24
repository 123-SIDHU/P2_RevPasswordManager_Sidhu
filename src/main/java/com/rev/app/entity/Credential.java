package com.rev.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CREDENTIALS")
public class Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USER_ID", nullable = false)
    private int userId;

    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    private String username;

    @Column(name = "ENCRYPTED_PASSWORD")
    private String encryptedPassword;

    private String url;

    private String notes;

    private String category;

    @Column(name = "IS_FAVORITE")
    private boolean isFavorite = false;

    @Column(name = "CREATED_AT", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private java.time.LocalDateTime updatedAt;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    public Credential() {}

    public Credential(int userId, String accountName, String username, String encryptedPassword, String url,
                      String notes, String category, boolean isFavorite) {
        this.userId = userId;
        this.accountName = accountName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.url = url;
        this.notes = notes;
        this.category = category;
        this.isFavorite = isFavorite;
    }

    public Credential(int id, int userId, String accountName, String username, String encryptedPassword, String url,
                      String notes, String category, boolean isFavorite) {
        this.id = id;
        this.userId = userId;
        this.accountName = accountName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.url = url;
        this.notes = notes;
        this.category = category;
        this.isFavorite = isFavorite;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
