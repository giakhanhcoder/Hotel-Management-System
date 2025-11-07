package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.projectprmt5.database.converters.DateConverter;
import com.example.projectprmt5.database.converters.ListConverter;

import java.util.Date;
import java.util.List;

/**
 * Room entity representing hotel rooms
 */
@Entity(tableName = "rooms")
@TypeConverters({DateConverter.class, ListConverter.class})
public class Room {
    
    @PrimaryKey(autoGenerate = true)
    private int roomId;
    
    private String roomNumber;
    private String roomType; // "SINGLE", "DOUBLE", "SUITE", "DELUXE"
    private String status; // "AVAILABLE", "OCCUPIED", "MAINTENANCE", "RESERVED"
    private double pricePerNight;
    private int maxGuests;
    private String description;
    private List<String> amenities; // ["WiFi", "TV", "AC", "MiniBar"]
    private List<String> imagePaths; // Paths to room images
    private int floorNumber;
    private boolean isActive;
    private Date createdAt;
    private Date lastUpdatedAt;
    
    // Constructors
    public Room() {
        this.createdAt = new Date();
        this.lastUpdatedAt = new Date();
        this.status = RoomStatus.AVAILABLE;
        this.isActive = true;
    }
    
    @Ignore
    public Room(String roomNumber, String roomType, double pricePerNight, int maxGuests) {
        this();
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
    }
    
    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }
    
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getPricePerNight() {
        return pricePerNight;
    }
    
    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    
    public int getMaxGuests() {
        return maxGuests;
    }
    
    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getAmenities() {
        return amenities;
    }
    
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
    
    public List<String> getImagePaths() {
        return imagePaths;
    }
    
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
    
    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
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
    
    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    
    // Constants
    public static class RoomType {
        public static final String SINGLE = "SINGLE";
        public static final String DOUBLE = "DOUBLE";
        public static final String SUITE = "SUITE";
        public static final String DELUXE = "DELUXE";
    }
    
    public static class RoomStatus {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String OCCUPIED = "OCCUPIED";
        public static final String MAINTENANCE = "MAINTENANCE";
        public static final String RESERVED = "RESERVED";
    }
}

