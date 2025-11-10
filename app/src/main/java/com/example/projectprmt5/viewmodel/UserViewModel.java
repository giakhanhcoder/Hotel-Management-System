package com.example.projectprmt5.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.UserRepository;

import java.util.List;
import java.util.concurrent.Future;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public Future<User> getUserByIdSync(int userId) {
        return repository.getUserByIdSync(userId);
    }

    public LiveData<User> getUserById(int userId) {
        return repository.getUserById(userId);
    }

    public LiveData<List<User>> getAllUsers() {
        return repository.getAllUsers();
    }

    public void insert(User user) {
        repository.insert(user);
    }
}
