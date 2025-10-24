package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.projectprmt5.database.converters.DateConverter;

import java.util.Date;

/**
 * User entity for authentication and profile management
 * Supports three roles: Guest, Receptionist, Manager
 */
@Entity(tableName = "users",
        indices = {@Index(value = "email", unique = true)})
@TypeConverters(DateConverter.class)
public class User {
    
    @PrimaryKey(autoGenerate = true)
    private int userId;
    
    private String email;
    private String passwordHash;
    private String fullName;
    private String phoneNumber;
    private String role; // "GUEST", "RECEPTIONIST", "MANAGER"
    private String idPhotoPath; // Path to ID document photo
    private boolean isActive;
    private Date createdAt;
    private Date lastLoginAt;
    
    // Additional fields for guests
    private String address;
    private String preferences; // JSON string for stay preferences
    
    // Constructors
    public User() {
        this.createdAt = new Date();
        this.isActive = true;
    }
    
    @Ignore
    public User(String email, String passwordHash, String fullName, String role) {
        this();
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getIdPhotoPath() {
        return idPhotoPath;
    }
    
    public void setIdPhotoPath(String idPhotoPath) {
        this.idPhotoPath = idPhotoPath;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPreferences() {
        return preferences;
    }
    
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
    
    // Role constants
    public static class Role {
        public static final String GUEST = "GUEST";
        public static final String RECEPTIONIST = "RECEPTIONIST";
        public static final String MANAGER = "MANAGER";
    }
}

