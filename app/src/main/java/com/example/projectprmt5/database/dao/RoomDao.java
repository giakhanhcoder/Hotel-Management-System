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

@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Room room);

    @Update
    void update(Room room);

    @Delete
    void delete(Room room);

    @Query("DELETE FROM rooms")
    void deleteAll();

    @Query("SELECT * FROM rooms ORDER BY roomNumber ASC")
    LiveData<List<Room>> getAllRooms();
    
    @Query("SELECT * FROM rooms WHERE id = :roomId")
    LiveData<Room> getRoomById(int roomId);

    @Query("SELECT * FROM rooms WHERE id = :roomId")
    Room getRoomByIdSync(int roomId);

    @Query("UPDATE rooms SET status = :status WHERE id = :roomId")
    void updateRoomStatus(int roomId, String status);

    @Query("SELECT * FROM rooms WHERE roomNumber = :roomNumber LIMIT 1")
    Room getRoomByNumberSync(String roomNumber);

    @Query("SELECT * FROM rooms LIMIT 1")
    Room getAnyRoom();
}











