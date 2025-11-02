package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.RoomDao;
import com.example.projectprmt5.database.entities.Room;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Repository class for Room entity
 */
public class RoomRepository {
    
    private final RoomDao roomDao;
    
    public RoomRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        roomDao = database.roomDao();
    }
    
    // Insert operations
    public Future<Long> insert(Room room) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.insert(room));
    }
    
    public Future<List<Long>> insertAll(List<Room> rooms) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.insertAll(rooms));
    }
    
    // Update operations
    public Future<Integer> update(Room room) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            room.setLastUpdatedAt(new Date());
            return roomDao.update(room);
        });
    }
    
    public Future<Integer> updateRoomStatus(int roomId, String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            roomDao.updateRoomStatus(roomId, status)
        );
    }
    
    public Future<Integer> updateRoomActiveStatus(int roomId, boolean isActive) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            roomDao.updateRoomActiveStatus(roomId, isActive)
        );
    }
    
    // Delete operations
    public Future<Integer> delete(Room room) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.delete(room));
    }
    
    public Future<Integer> deleteById(int roomId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.deleteById(roomId));
    }
    
    // Query operations
    public LiveData<Room> getRoomById(int roomId) {
        return roomDao.getRoomById(roomId);
    }
    
    public Future<Room> getRoomByIdSync(int roomId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getRoomByIdSync(roomId));
    }
    
    public Future<Room> getRoomByNumber(String roomNumber) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getRoomByNumber(roomNumber));
    }
    
    public LiveData<List<Room>> getAllActiveRooms() {
        return roomDao.getAllActiveRooms();
    }
    
    public Future<List<Room>> getAllActiveRoomsSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getAllActiveRoomsSync());
    }
    
    public LiveData<List<Room>> getAllRooms() {
        return roomDao.getAllRooms();
    }
    
    public Future<List<Room>> getAllRoomsSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getAllRoomsSync());
    }
    
    public LiveData<List<Room>> getRoomsByStatus(String status) {
        return roomDao.getRoomsByStatus(status);
    }
    
    public Future<List<Room>> getRoomsByStatusSync(String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getRoomsByStatusSync(status));
    }
    
    public LiveData<List<Room>> getRoomsByType(String roomType) {
        return roomDao.getRoomsByType(roomType);
    }
    
    public Future<List<Room>> getRoomsByTypeSync(String roomType) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getRoomsByTypeSync(roomType));
    }
    
    public LiveData<List<Room>> searchAvailableRooms(String roomType, int numberOfGuests) {
        return roomDao.searchAvailableRooms(roomType, numberOfGuests);
    }
    
    public LiveData<List<Room>> searchRoomsWithFilters(int numberOfGuests, double minPrice, double maxPrice) {
        return roomDao.searchRoomsWithFilters(numberOfGuests, minPrice, maxPrice);
    }
    
    public Future<Integer> getRoomCountByStatus(String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            roomDao.getRoomCountByStatus(status)
        );
    }
    
    public LiveData<Integer> getAvailableRoomCount() {
        return roomDao.getAvailableRoomCount();
    }
    
    public LiveData<List<Room>> getRoomsByFloor(int floorNumber) {
        return roomDao.getRoomsByFloor(floorNumber);
    }
}



