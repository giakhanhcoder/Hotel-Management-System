package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.projectprmt5.database.converters.DateConverter;

import java.util.Date;

/**
 * Inventory entity for tracking hotel items and supplies
 */
@Entity(tableName = "inventory")
@TypeConverters(DateConverter.class)
public class Inventory {
    
    @PrimaryKey(autoGenerate = true)
    private int inventoryId;
    
    private String itemName;
    private String itemCode; // Unique item code/barcode
    private String category; // "AMENITY", "CLEANING", "LINEN", "FOOD", "BEVERAGE", "OTHER"
    private String description;
    private int currentQuantity;
    private int minimumQuantity; // Threshold for reorder alert
    private String unit; // "piece", "bottle", "kg", "liter"
    private double unitPrice;
    private String supplierName;
    private String supplierContact;
    private boolean isActive;
    private Date createdAt;
    private Date lastUpdatedAt;
    private Date lastRestockedDate;
    
    // Constructors
    public Inventory() {
        this.createdAt = new Date();
        this.lastUpdatedAt = new Date();
        this.isActive = true;
        this.currentQuantity = 0;
    }
    
    @Ignore
    public Inventory(String itemName, String itemCode, String category, 
                     int currentQuantity, int minimumQuantity) {
        this();
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.category = category;
        this.currentQuantity = currentQuantity;
        this.minimumQuantity = minimumQuantity;
    }
    
    // Check if item needs restocking
    public boolean needsRestocking() {
        return currentQuantity <= minimumQuantity;
    }
    
    // Getters and Setters
    public int getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCurrentQuantity() {
        return currentQuantity;
    }
    
    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }
    
    public int getMinimumQuantity() {
        return minimumQuantity;
    }
    
    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getSupplierContact() {
        return supplierContact;
    }
    
    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
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
    
    public Date getLastRestockedDate() {
        return lastRestockedDate;
    }
    
    public void setLastRestockedDate(Date lastRestockedDate) {
        this.lastRestockedDate = lastRestockedDate;
    }
    
    // Constants
    public static class Category {
        public static final String AMENITY = "AMENITY";
        public static final String CLEANING = "CLEANING";
        public static final String LINEN = "LINEN";
        public static final String FOOD = "FOOD";
        public static final String BEVERAGE = "BEVERAGE";
        public static final String OTHER = "OTHER";
    }
}

