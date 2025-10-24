package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.projectprmt5.database.converters.DateConverter;

import java.util.Date;

/**
 * InventoryUsage entity for tracking inventory consumption
 */
@Entity(tableName = "inventory_usage",
        foreignKeys = {
            @ForeignKey(entity = Inventory.class,
                       parentColumns = "inventoryId",
                       childColumns = "inventoryId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Room.class,
                       parentColumns = "roomId",
                       childColumns = "roomId",
                       onDelete = ForeignKey.SET_NULL),
            @ForeignKey(entity = User.class,
                       parentColumns = "userId",
                       childColumns = "loggedByUserId",
                       onDelete = ForeignKey.SET_NULL)
        },
        indices = {
            @Index(value = "inventoryId"),
            @Index(value = "roomId"),
            @Index(value = "loggedByUserId"),
            @Index(value = "usageDate")
        })
@TypeConverters(DateConverter.class)
public class InventoryUsage {
    
    @PrimaryKey(autoGenerate = true)
    private int usageId;
    
    private int inventoryId;
    private Integer roomId; // Can be null for general usage
    private int loggedByUserId; // Receptionist who logged the usage
    private int quantityUsed;
    private String usageType; // "ROOM_SERVICE", "CLEANING", "MAINTENANCE", "RESTOCKING", "WASTAGE"
    private String notes;
    private Date usageDate;
    
    // Constructors
    public InventoryUsage() {
        this.usageDate = new Date();
    }
    
    @Ignore
    public InventoryUsage(int inventoryId, int quantityUsed, int loggedByUserId, String usageType) {
        this();
        this.inventoryId = inventoryId;
        this.quantityUsed = quantityUsed;
        this.loggedByUserId = loggedByUserId;
        this.usageType = usageType;
    }
    
    // Getters and Setters
    public int getUsageId() {
        return usageId;
    }
    
    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }
    
    public int getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    public Integer getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
    
    public int getLoggedByUserId() {
        return loggedByUserId;
    }
    
    public void setLoggedByUserId(int loggedByUserId) {
        this.loggedByUserId = loggedByUserId;
    }
    
    public int getQuantityUsed() {
        return quantityUsed;
    }
    
    public void setQuantityUsed(int quantityUsed) {
        this.quantityUsed = quantityUsed;
    }
    
    public String getUsageType() {
        return usageType;
    }
    
    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Date getUsageDate() {
        return usageDate;
    }
    
    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }
    
    // Constants
    public static class UsageType {
        public static final String ROOM_SERVICE = "ROOM_SERVICE";
        public static final String CLEANING = "CLEANING";
        public static final String MAINTENANCE = "MAINTENANCE";
        public static final String RESTOCKING = "RESTOCKING";
        public static final String WASTAGE = "WASTAGE";
    }
}

