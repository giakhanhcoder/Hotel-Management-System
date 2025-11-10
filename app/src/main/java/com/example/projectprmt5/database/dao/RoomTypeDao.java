package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.RoomType;

import java.util.List;

@Dao
public interface RoomTypeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RoomType roomType);

    @Update
    void update(RoomType roomType);

    @Delete
    void delete(RoomType roomType);

    @Query("SELECT * FROM room_types ORDER BY name ASC")
    LiveData<List<RoomType>> getAllRoomTypes();

    @Query("SELECT * FROM room_types LIMIT 1")
    RoomType getAnyRoomType();
}
