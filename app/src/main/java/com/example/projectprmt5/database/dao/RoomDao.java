package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.Room;

import java.util.List;

/**
 * Data Access Object for Room entity
 */
@Dao
public interface RoomDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Room room);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Room> rooms);
    
    // Update operations
    @Update
    int update(Room room);
    
    // Delete operations
    @Delete
    int delete(Room room);
    
    @Query("DELETE FROM rooms WHERE roomId = :roomId")
    int deleteById(int roomId);
    
    // Query operations
    @Query("SELECT * FROM rooms WHERE roomId = :roomId")
    LiveData<Room> getRoomById(int roomId);
    
    @Query("SELECT * FROM rooms WHERE roomId = :roomId")
    Room getRoomByIdSync(int roomId);
    
    @Query("SELECT * FROM rooms WHERE roomNumber = :roomNumber LIMIT 1")
    Room getRoomByNumber(String roomNumber);
    
    @Query("SELECT * FROM rooms WHERE isActive = 1")
    LiveData<List<Room>> getAllActiveRooms();
    
    @Query("SELECT * FROM rooms WHERE isActive = 1")
    List<Room> getAllActiveRoomsSync();
    
    @Query("SELECT * FROM rooms")
    LiveData<List<Room>> getAllRooms();
    
    @Query("SELECT * FROM rooms")
    List<Room> getAllRoomsSync();
    
    @Query("SELECT * FROM rooms WHERE status = :status AND isActive = 1")
    LiveData<List<Room>> getRoomsByStatus(String status);
    
    @Query("SELECT * FROM rooms WHERE status = :status AND isActive = 1")
    List<Room> getRoomsByStatusSync(String status);
    
    @Query("SELECT * FROM rooms WHERE roomType = :roomType AND isActive = 1")
    LiveData<List<Room>> getRoomsByType(String roomType);
    
    @Query("SELECT * FROM rooms WHERE roomType = :roomType AND isActive = 1")
    List<Room> getRoomsByTypeSync(String roomType);
    
    @Query("SELECT * FROM rooms WHERE roomType = :roomType AND status = 'AVAILABLE' " +
           "AND maxGuests >= :numberOfGuests AND isActive = 1 " +
           "ORDER BY pricePerNight ASC")
    LiveData<List<Room>> searchAvailableRooms(String roomType, int numberOfGuests);
    
    @Query("SELECT * FROM rooms WHERE status = 'AVAILABLE' " +
           "AND maxGuests >= :numberOfGuests AND isActive = 1 " +
           "AND pricePerNight BETWEEN :minPrice AND :maxPrice " +
           "ORDER BY pricePerNight ASC")
    LiveData<List<Room>> searchRoomsWithFilters(int numberOfGuests, double minPrice, double maxPrice);
    
    @Query("UPDATE rooms SET status = :status WHERE roomId = :roomId")
    int updateRoomStatus(int roomId, String status);
    
    @Query("UPDATE rooms SET isActive = :isActive WHERE roomId = :roomId")
    int updateRoomActiveStatus(int roomId, boolean isActive);
    
    @Query("SELECT COUNT(*) FROM rooms WHERE status = :status AND isActive = 1")
    int getRoomCountByStatus(String status);
    
    @Query("SELECT COUNT(*) FROM rooms WHERE status = 'AVAILABLE' AND isActive = 1")
    LiveData<Integer> getAvailableRoomCount();
    
    @Query("SELECT * FROM rooms WHERE floorNumber = :floorNumber AND isActive = 1")
    LiveData<List<Room>> getRoomsByFloor(int floorNumber);
}



