package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.InventoryDao;
import com.example.projectprmt5.database.entities.Inventory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Repository class for Inventory entity
 */
public class InventoryRepository {
    
    private final InventoryDao inventoryDao;
    
    public InventoryRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        inventoryDao = database.inventoryDao();
    }
    
    // Insert operations
    public Future<Long> insert(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.insert(inventory));
    }
    
    public Future<List<Long>> insertAll(List<Inventory> inventoryItems) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.insertAll(inventoryItems));
    }
    
    // Update operations
    public Future<Integer> update(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            inventory.setLastUpdatedAt(new Date());
            return inventoryDao.update(inventory);
        });
    }
    
    public Future<Integer> addStock(int inventoryId, int quantity) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.addStock(inventoryId, quantity, new Date().getTime())
        );
    }
    
    public Future<Integer> reduceStock(int inventoryId, int quantity) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.reduceStock(inventoryId, quantity)
        );
    }
    
    public Future<Integer> updateStock(int inventoryId, int quantity) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.updateStock(inventoryId, quantity)
        );
    }
    
    public Future<Integer> updateInventoryStatus(int inventoryId, boolean isActive) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.updateInventoryStatus(inventoryId, isActive)
        );
    }
    
    // Delete operations
    public Future<Integer> delete(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.delete(inventory));
    }
    
    public Future<Integer> deleteById(int inventoryId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.deleteById(inventoryId));
    }
    
    // Query operations
    public LiveData<Inventory> getInventoryById(int inventoryId) {
        return inventoryDao.getInventoryById(inventoryId);
    }
    
    public Future<Inventory> getInventoryByIdSync(int inventoryId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.getInventoryByIdSync(inventoryId)
        );
    }
    
    public Future<Inventory> getInventoryByCode(String itemCode) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.getInventoryByCode(itemCode)
        );
    }
    
    public LiveData<List<Inventory>> getAllActiveInventory() {
        return inventoryDao.getAllActiveInventory();
    }
    
    public Future<List<Inventory>> getAllActiveInventorySync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.getAllActiveInventorySync()
        );
    }
    
    public LiveData<List<Inventory>> getAllInventory() {
        return inventoryDao.getAllInventory();
    }
    
    public Future<List<Inventory>> getAllInventorySync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.getAllInventorySync()
        );
    }
    
    public LiveData<List<Inventory>> getInventoryByCategory(String category) {
        return inventoryDao.getInventoryByCategory(category);
    }
    
    public Future<List<Inventory>> getInventoryByCategorySync(String category) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.getInventoryByCategorySync(category)
        );
    }
    
    public LiveData<List<Inventory>> getLowStockItems() {
        return inventoryDao.getLowStockItems();
    }
    
    public Future<List<Inventory>> getLowStockItemsSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.getLowStockItemsSync()
        );
    }
    
    public Future<Integer> getLowStockCount() {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryDao.getLowStockCount()
        );
    }
    
    public LiveData<Integer> getLowStockCountLive() {
        return inventoryDao.getLowStockCountLive();
    }
    
    public LiveData<List<Inventory>> searchInventory(String searchQuery) {
        return inventoryDao.searchInventory(searchQuery);
    }
    
    public Future<Double> getTotalInventoryValue() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Double value = inventoryDao.getTotalInventoryValue();
            return value != null ? value : 0.0;
        });
    }
}












