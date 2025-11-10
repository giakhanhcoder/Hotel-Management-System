package com.example.projectprmt5.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.entities.Service;
import com.example.projectprmt5.repository.ServiceRepository;

import java.util.List;

public class ServiceViewModel extends AndroidViewModel {

    private ServiceRepository repository;
    private LiveData<List<Service>> allServices;

    public ServiceViewModel(@NonNull Application application) {
        super(application);
        repository = new ServiceRepository(application);
        allServices = repository.getAllServices();
    }

    public LiveData<List<Service>> getAllServices() {
        return allServices;
    }

    public void insert(Service service) {
        repository.insert(service);
    }

    public void update(Service service) {
        repository.update(service);
    }

    public void delete(Service service) {
        repository.delete(service);
    }
}
