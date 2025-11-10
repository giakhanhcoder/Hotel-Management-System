package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.RoomTypeDao;
import com.example.projectprmt5.database.entities.RoomType;

import java.util.List;

public class RoomTypeRepository {

    private RoomTypeDao roomTypeDao;
    private LiveData<List<RoomType>> allRoomTypes;

    public RoomTypeRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        roomTypeDao = database.roomTypeDao();
        allRoomTypes = roomTypeDao.getAllRoomTypes();
    }

    public LiveData<List<RoomType>> getAllRoomTypes() {
        return allRoomTypes;
    }

    public void insert(RoomType roomType) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            roomTypeDao.insert(roomType);
        });
    }

    public void update(RoomType roomType) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            roomTypeDao.update(roomType);
        });
    }

    public void delete(RoomType roomType) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            roomTypeDao.delete(roomType);
        });
    }
}
