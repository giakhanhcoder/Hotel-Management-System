package com.example.projectprmt5.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.repository.RoomRepository;

import java.util.List;
import java.util.concurrent.Future;

public class RoomViewModel extends AndroidViewModel {

    private final RoomRepository repository;
    private final LiveData<List<Room>> allRooms;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository(application);
        allRooms = repository.getAllRooms();
    }

    public LiveData<List<Room>> getAllRooms() {
        return allRooms;
    }

    public LiveData<Room> getRoomById(int roomId) {
        return repository.getRoomById(roomId);
    }

    public Future<Room> getRoomByIdSync(int roomId) {
        return repository.getRoomByIdSync(roomId);
    }

    public void insert(Room room) {
        repository.insert(room);
    }

    public void update(Room room) {
        repository.update(room);
    }

    public void delete(Room room) {
        repository.delete(room);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public Room getRoomByNumberSync(String roomNumber) {
        return repository.getRoomByNumberSync(roomNumber);
    }
}
