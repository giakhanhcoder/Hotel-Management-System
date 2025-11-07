package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.BookingDao;
import com.example.projectprmt5.database.dao.RoomDao;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Room;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public class BookingRepository {

    private final BookingDao bookingDao;
    private final RoomDao roomDao;

    public BookingRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        bookingDao = database.bookingDao();
        roomDao = database.roomDao();
    }

    public Future<Long> insert(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.insert(booking));
    }

    public Future<Integer> update(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.update(booking));
    }

    public Future<Integer> delete(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.delete(booking));
    }

    public LiveData<List<Booking>> getAllBookings() {
        return bookingDao.getAllBookings();
    }

    public LiveData<Booking> getBookingById(int bookingId) {
        return bookingDao.getBookingById(bookingId);
    }

    public Future<Boolean> isRoomAvailable(int roomId, Date checkIn, Date checkOut) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            List<Booking> bookings = bookingDao.checkRoomAvailability(roomId, checkIn.getTime(), checkOut.getTime());
            return bookings == null || bookings.isEmpty();
        });
    }

    public Future<Room> getRoomByNumber(String roomNumber) {
        return AppDatabase.databaseWriteExecutor.submit(() -> roomDao.getRoomByNumberSync(roomNumber));
    }

    public Future<Integer> checkIn(int bookingId, int receptionistId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.checkIn(bookingId, receptionistId, new Date().getTime()));
    }

    public Future<Integer> checkOut(int bookingId, int receptionistId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.checkOut(bookingId, receptionistId, new Date().getTime()));
    }

    public Future<Integer> updateBookingStatus(int bookingId, String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.updateBookingStatus(bookingId, status));
    }
}
