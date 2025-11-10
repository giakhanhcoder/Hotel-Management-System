package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.InventoryDao;
import com.example.projectprmt5.database.dao.InventoryUsageDao;
import com.example.projectprmt5.database.entities.InventoryUsage;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Repository class for InventoryUsage entity
 */
public class InventoryUsageRepository {
    
    private final InventoryUsageDao inventoryUsageDao;
    private final InventoryDao inventoryDao;
    
    public InventoryUsageRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        inventoryUsageDao = database.inventoryUsageDao();
        inventoryDao = database.inventoryDao();
    }
    
    // Insert operations
    public Future<Long> insert(InventoryUsage inventoryUsage) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Reduce inventory stock
            inventoryDao.reduceStock(
                inventoryUsage.getInventoryId(), 
                inventoryUsage.getQuantityUsed()
            );
            return inventoryUsageDao.insert(inventoryUsage);
        });
    }
    
    public Future<List<Long>> insertAll(List<InventoryUsage> usageRecords) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.insertAll(usageRecords)
        );
    }
    
    // Update operations
    public Future<Integer> update(InventoryUsage inventoryUsage) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.update(inventoryUsage)
        );
    }
    
    // Delete operations
    public Future<Integer> delete(InventoryUsage inventoryUsage) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Add stock back
            inventoryDao.addStock(
                inventoryUsage.getInventoryId(), 
                inventoryUsage.getQuantityUsed(),
                new Date().getTime()
            );
            return inventoryUsageDao.delete(inventoryUsage);
        });
    }
    
    public Future<Integer> deleteById(int usageId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.deleteById(usageId)
        );
    }
    
    // Query operations
    public LiveData<InventoryUsage> getUsageById(int usageId) {
        return inventoryUsageDao.getUsageById(usageId);
    }
    
    public Future<InventoryUsage> getUsageByIdSync(int usageId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getUsageByIdSync(usageId)
        );
    }
    
    public LiveData<List<InventoryUsage>> getUsageByInventoryItem(int inventoryId) {
        return inventoryUsageDao.getUsageByInventoryItem(inventoryId);
    }
    
    public Future<List<InventoryUsage>> getUsageByInventoryItemSync(int inventoryId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getUsageByInventoryItemSync(inventoryId)
        );
    }
    
    public LiveData<List<InventoryUsage>> getUsageByRoom(int roomId) {
        return inventoryUsageDao.getUsageByRoom(roomId);
    }
    
    public Future<List<InventoryUsage>> getUsageByRoomSync(int roomId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getUsageByRoomSync(roomId)
        );
    }
    
    public LiveData<List<InventoryUsage>> getUsageByUser(int userId) {
        return inventoryUsageDao.getUsageByUser(userId);
    }
    
    public LiveData<List<InventoryUsage>> getUsageByType(String usageType) {
        return inventoryUsageDao.getUsageByType(usageType);
    }
    
    public Future<List<InventoryUsage>> getUsageByTypeSync(String usageType) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getUsageByTypeSync(usageType)
        );
    }
    
    public LiveData<List<InventoryUsage>> getAllUsage() {
        return inventoryUsageDao.getAllUsage();
    }
    
    public Future<List<InventoryUsage>> getAllUsageSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getAllUsageSync()
        );
    }
    
    public LiveData<List<InventoryUsage>> getUsageInDateRange(Date startDate, Date endDate) {
        return inventoryUsageDao.getUsageInDateRange(startDate.getTime(), endDate.getTime());
    }
    
    public Future<List<InventoryUsage>> getUsageInDateRangeSync(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getUsageInDateRangeSync(startDate.getTime(), endDate.getTime())
        );
    }
    
    public Future<List<InventoryUsage>> getInventoryUsageInDateRange(int inventoryId, 
                                                                     Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getInventoryUsageInDateRange(
                inventoryId, startDate.getTime(), endDate.getTime()
            )
        );
    }
    
    public Future<Integer> getTotalUsageForItem(int inventoryId, Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Integer total = inventoryUsageDao.getTotalUsageForItem(
                inventoryId, startDate.getTime(), endDate.getTime()
            );
            return total != null ? total : 0;
        });
    }
    
    public Future<Integer> getTotalUsageByType(String usageType, Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Integer total = inventoryUsageDao.getTotalUsageByType(
                usageType, startDate.getTime(), endDate.getTime()
            );
            return total != null ? total : 0;
        });
    }
    
    public Future<Integer> getUsageCountInDateRange(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            inventoryUsageDao.getUsageCountInDateRange(startDate.getTime(), endDate.getTime())
        );
    }
}












