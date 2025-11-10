package com.example.projectprmt5.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.entities.RoomType;
import com.example.projectprmt5.repository.RoomTypeRepository;

import java.util.List;

public class RoomTypeViewModel extends AndroidViewModel {

    private RoomTypeRepository repository;
    private LiveData<List<RoomType>> allRoomTypes;

    public RoomTypeViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomTypeRepository(application);
        allRoomTypes = repository.getAllRoomTypes();
    }

    public LiveData<List<RoomType>> getAllRoomTypes() {
        return allRoomTypes;
    }

    public void insert(RoomType roomType) {
        repository.insert(roomType);
    }

    public void update(RoomType roomType) {
        repository.update(roomType);
    }

    public void delete(RoomType roomType) {
        repository.delete(roomType);
    }
}
