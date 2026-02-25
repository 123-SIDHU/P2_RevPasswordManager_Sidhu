package com.rev.app.dto;

public class CredentialDTO {
    private int id;
    private String accountName;
    private String username;
    private String url;
    private String notes;
    private String category;
    private boolean isFavorite;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
