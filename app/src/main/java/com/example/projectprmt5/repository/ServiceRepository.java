package com.example.projectprmt5.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.ServiceDao;
import com.example.projectprmt5.database.entities.BookingServiceCrossRef;
import com.example.projectprmt5.database.entities.Service;
import com.example.projectprmt5.database.relations.BookingWithServices;

import java.util.List;

public class ServiceRepository {

    private ServiceDao serviceDao;
    private LiveData<List<Service>> allServices;

    public ServiceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        serviceDao = db.serviceDao();
        allServices = serviceDao.getAllServices();
    }

    public LiveData<List<Service>> getAllServices() {
        return allServices;
    }

    public LiveData<BookingWithServices> getBookingWithServices(int bookingId) {
        return serviceDao.getBookingWithServices(bookingId);
    }

    public void addServiceToBooking(int bookingId, int serviceId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            serviceDao.insertBookingServiceCrossRef(new BookingServiceCrossRef(bookingId, serviceId));
        });
    }

    public void insert(Service service) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            serviceDao.insert(service);
        });
    }

    public void update(Service service) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            serviceDao.update(service);
        });
    }

    public void delete(Service service) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            serviceDao.delete(service);
        });
    }
}
