package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.InventoryUsage;

import java.util.List;

/**
 * Data Access Object for InventoryUsage entity
 */
@Dao
public interface InventoryUsageDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(InventoryUsage inventoryUsage);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<InventoryUsage> usageRecords);
    
    // Update operations
    @Update
    int update(InventoryUsage inventoryUsage);
    
    // Delete operations
    @Delete
    int delete(InventoryUsage inventoryUsage);
    
    @Query("DELETE FROM inventory_usage WHERE usageId = :usageId")
    int deleteById(int usageId);
    
    // Query operations
    @Query("SELECT * FROM inventory_usage WHERE usageId = :usageId")
    LiveData<InventoryUsage> getUsageById(int usageId);
    
    @Query("SELECT * FROM inventory_usage WHERE usageId = :usageId")
    InventoryUsage getUsageByIdSync(int usageId);
    
    @Query("SELECT * FROM inventory_usage WHERE inventoryId = :inventoryId ORDER BY usageDate DESC")
    LiveData<List<InventoryUsage>> getUsageByInventoryItem(int inventoryId);
    
    @Query("SELECT * FROM inventory_usage WHERE inventoryId = :inventoryId ORDER BY usageDate DESC")
    List<InventoryUsage> getUsageByInventoryItemSync(int inventoryId);
    
    @Query("SELECT * FROM inventory_usage WHERE roomId = :roomId ORDER BY usageDate DESC")
    LiveData<List<InventoryUsage>> getUsageByRoom(int roomId);
    
    @Query("SELECT * FROM inventory_usage WHERE roomId = :roomId ORDER BY usageDate DESC")
    List<InventoryUsage> getUsageByRoomSync(int roomId);
    
    @Query("SELECT * FROM inventory_usage WHERE loggedByUserId = :userId ORDER BY usageDate DESC")
    LiveData<List<InventoryUsage>> getUsageByUser(int userId);
    
    @Query("SELECT * FROM inventory_usage WHERE usageType = :usageType ORDER BY usageDate DESC")
    LiveData<List<InventoryUsage>> getUsageByType(String usageType);
    
    @Query("SELECT * FROM inventory_usage WHERE usageType = :usageType ORDER BY usageDate DESC")
    List<InventoryUsage> getUsageByTypeSync(String usageType);
    
    @Query("SELECT * FROM inventory_usage ORDER BY usageDate DESC")
    LiveData<List<InventoryUsage>> getAllUsage();
    
    @Query("SELECT * FROM inventory_usage ORDER BY usageDate DESC")
    List<InventoryUsage> getAllUsageSync();
    
    @Query("SELECT * FROM inventory_usage WHERE usageDate >= :startDate AND usageDate <= :endDate " +
           "ORDER BY usageDate DESC")
    LiveData<List<InventoryUsage>> getUsageInDateRange(long startDate, long endDate);
    
    @Query("SELECT * FROM inventory_usage WHERE usageDate >= :startDate AND usageDate <= :endDate " +
           "ORDER BY usageDate DESC")
    List<InventoryUsage> getUsageInDateRangeSync(long startDate, long endDate);
    
    @Query("SELECT * FROM inventory_usage WHERE inventoryId = :inventoryId " +
           "AND usageDate >= :startDate AND usageDate <= :endDate")
    List<InventoryUsage> getInventoryUsageInDateRange(int inventoryId, long startDate, long endDate);
    
    @Query("SELECT SUM(quantityUsed) FROM inventory_usage WHERE inventoryId = :inventoryId " +
           "AND usageDate >= :startDate AND usageDate <= :endDate")
    Integer getTotalUsageForItem(int inventoryId, long startDate, long endDate);
    
    @Query("SELECT SUM(quantityUsed) FROM inventory_usage WHERE usageType = :usageType " +
           "AND usageDate >= :startDate AND usageDate <= :endDate")
    Integer getTotalUsageByType(String usageType, long startDate, long endDate);
    
    @Query("SELECT COUNT(*) FROM inventory_usage WHERE usageDate >= :startDate AND usageDate <= :endDate")
    int getUsageCountInDateRange(long startDate, long endDate);
}


