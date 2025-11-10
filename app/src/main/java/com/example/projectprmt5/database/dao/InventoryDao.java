package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.Inventory;

import java.util.List;

/**
 * Data Access Object for Inventory entity
 */
@Dao
public interface InventoryDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Inventory inventory);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Inventory> inventoryItems);
    
    // Update operations
    @Update
    int update(Inventory inventory);
    
    // Delete operations
    @Delete
    int delete(Inventory inventory);
    
    @Query("DELETE FROM inventory WHERE inventoryId = :inventoryId")
    int deleteById(int inventoryId);
    
    // Query operations
    @Query("SELECT * FROM inventory WHERE inventoryId = :inventoryId")
    LiveData<Inventory> getInventoryById(int inventoryId);
    
    @Query("SELECT * FROM inventory WHERE inventoryId = :inventoryId")
    Inventory getInventoryByIdSync(int inventoryId);
    
    @Query("SELECT * FROM inventory WHERE itemCode = :itemCode LIMIT 1")
    Inventory getInventoryByCode(String itemCode);
    
    @Query("SELECT * FROM inventory WHERE isActive = 1")
    LiveData<List<Inventory>> getAllActiveInventory();
    
    @Query("SELECT * FROM inventory WHERE isActive = 1")
    List<Inventory> getAllActiveInventorySync();
    
    @Query("SELECT * FROM inventory")
    LiveData<List<Inventory>> getAllInventory();
    
    @Query("SELECT * FROM inventory")
    List<Inventory> getAllInventorySync();
    
    @Query("SELECT * FROM inventory WHERE category = :category AND isActive = 1")
    LiveData<List<Inventory>> getInventoryByCategory(String category);
    
    @Query("SELECT * FROM inventory WHERE category = :category AND isActive = 1")
    List<Inventory> getInventoryByCategorySync(String category);
    
    @Query("SELECT * FROM inventory WHERE currentQuantity <= minimumQuantity AND isActive = 1")
    LiveData<List<Inventory>> getLowStockItems();
    
    @Query("SELECT * FROM inventory WHERE currentQuantity <= minimumQuantity AND isActive = 1")
    List<Inventory> getLowStockItemsSync();
    
    @Query("UPDATE inventory SET currentQuantity = currentQuantity + :quantity, " +
           "lastRestockedDate = :restockedDate WHERE inventoryId = :inventoryId")
    int addStock(int inventoryId, int quantity, long restockedDate);
    
    @Query("UPDATE inventory SET currentQuantity = currentQuantity - :quantity " +
           "WHERE inventoryId = :inventoryId AND currentQuantity >= :quantity")
    int reduceStock(int inventoryId, int quantity);
    
    @Query("UPDATE inventory SET currentQuantity = :quantity WHERE inventoryId = :inventoryId")
    int updateStock(int inventoryId, int quantity);
    
    @Query("UPDATE inventory SET isActive = :isActive WHERE inventoryId = :inventoryId")
    int updateInventoryStatus(int inventoryId, boolean isActive);
    
    @Query("SELECT COUNT(*) FROM inventory WHERE currentQuantity <= minimumQuantity AND isActive = 1")
    int getLowStockCount();
    
    @Query("SELECT COUNT(*) FROM inventory WHERE currentQuantity <= minimumQuantity AND isActive = 1")
    LiveData<Integer> getLowStockCountLive();
    
    @Query("SELECT * FROM inventory WHERE itemName LIKE '%' || :searchQuery || '%' " +
           "OR itemCode LIKE '%' || :searchQuery || '%' AND isActive = 1")
    LiveData<List<Inventory>> searchInventory(String searchQuery);
    
    @Query("SELECT SUM(currentQuantity * unitPrice) FROM inventory WHERE isActive = 1")
    Double getTotalInventoryValue();
}











