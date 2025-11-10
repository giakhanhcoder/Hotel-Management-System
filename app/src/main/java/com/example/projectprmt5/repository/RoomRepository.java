package com.example.projectprmt5.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.RoomDao;
import com.example.projectprmt5.database.entities.Room;

import java.util.List;
import java.util.concurrent.Future;

public class RoomRepository {

    private final RoomDao roomDao;
    private final LiveData<List<Room>> allRooms;

    public RoomRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        roomDao = db.roomDao();
        allRooms = roomDao.getAllRooms();
    }

    public LiveData<List<Room>> getAllRooms() {
        return allRooms;
    }

    public LiveData<Room> getRoomById(int roomId) {
        return roomDao.getRoomById(roomId);
    }

    public Future<Room> getRoomByIdSync(int roomId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getRoomByIdSync(roomId));
    }

    public void insert(Room room) {
        AppDatabase.databaseWriteExecutor.execute(() -> roomDao.insert(room));
    }

    public void update(Room room) {
        AppDatabase.databaseWriteExecutor.execute(() -> roomDao.update(room));
    }

    public void delete(Room room) {
        AppDatabase.databaseWriteExecutor.execute(() -> roomDao.delete(room));
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(roomDao::deleteAll);
    }

    // This is a synchronous call and should be executed off the main thread.
    public Room getRoomByNumberSync(String roomNumber) {
        return roomDao.getRoomByNumberSync(roomNumber);
    }
}








