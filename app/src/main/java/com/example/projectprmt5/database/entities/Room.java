package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "rooms")
public class Room {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String roomNumber;

    private String type; // e.g., SINGLE, DOUBLE

    private double price;

    private String status; // e.g., AVAILABLE, RESERVED, OCCUPIED

    private String imageUrl;

    // --- Constructors ---
    public Room(String roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = RoomStatus.AVAILABLE; // Default status
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // --- Equals and HashCode for DiffUtil ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id &&
                Double.compare(room.price, price) == 0 &&
                Objects.equals(roomNumber, room.roomNumber) &&
                Objects.equals(type, room.type) &&
                Objects.equals(status, room.status) &&
                Objects.equals(imageUrl, room.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber, type, price, status, imageUrl);
    }

    // --- Constants for Status ---
    public static class RoomStatus {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String RESERVED = "RESERVED";
        public static final String OCCUPIED = "OCCUPIED";
    }
}
