package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.BookingServiceCrossRef;
import com.example.projectprmt5.database.entities.Service;
import com.example.projectprmt5.database.relations.BookingWithServices;

import java.util.List;

@Dao
public interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Service service);

    @Update
    void update(Service service);

    @Delete
    void delete(Service service);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookingServiceCrossRef(BookingServiceCrossRef crossRef);

    @Query("SELECT * FROM services ORDER BY name ASC")
    LiveData<List<Service>> getAllServices();

    @Transaction
    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    LiveData<BookingWithServices> getBookingWithServices(int bookingId);
    
    @Query("SELECT * FROM services LIMIT 1")
    Service getAnyService();
}
