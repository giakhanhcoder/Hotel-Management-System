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

/**
 * Repository for the Booking entity.
 * Handles data operations for bookings and coordinates with other DAOs like RoomDao.
 */
public class BookingRepository {

    private final BookingDao bookingDao;
    private final RoomDao roomDao;

    public BookingRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        bookingDao = database.bookingDao();
        roomDao = database.roomDao();
    }

    /**
     * Inserts a new booking and updates the corresponding room's status to RESERVED.
     */
    public Future<Long> insert(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.RESERVED);
            return bookingDao.insert(booking);
        });
    }

    /**
     * Updates an existing booking.
     */
    public Future<Integer> update(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            booking.setLastUpdatedAt(new Date());
            return bookingDao.update(booking);
        });
    }

    /**
     * Updates the status of a specific booking and adjusts the room status accordingly.
     */
    public Future<Integer> updateBookingStatus(int bookingId, String newStatus) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Booking booking = bookingDao.getBookingByIdSync(bookingId);
            if (booking != null) {
                // If booking is cancelled or checked out, make the room available again.
                if (newStatus.equals(Booking.BookingStatus.CANCELLED) || newStatus.equals(Booking.BookingStatus.CHECKED_OUT)) {
                    roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
                }
            }
            return bookingDao.updateBookingStatus(bookingId, newStatus);
        });
    }

    /**
     * Handles the check-in process for a booking.
     */
    public Future<Integer> checkIn(int bookingId, int receptionistId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Booking booking = bookingDao.getBookingByIdSync(bookingId);
            if (booking != null) {
                // Mark the room as OCCUPIED
                roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.OCCUPIED);
            }
            return bookingDao.checkIn(bookingId, receptionistId, new Date().getTime());
        });
    }

    /**
     * Handles the check-out process for a booking.
     */
    public Future<Integer> checkOut(int bookingId, int receptionistId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Booking booking = bookingDao.getBookingByIdSync(bookingId);
            if (booking != null) {
                // Make the room AVAILABLE again
                roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
            }
            return bookingDao.checkOut(bookingId, receptionistId, new Date().getTime());
        });
    }

    /**
     * Deletes a booking and makes the room available.
     */
    public Future<Integer> delete(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
            return bookingDao.delete(booking);
        });
    }

    // --- Query Methods ---

    public LiveData<Booking> getBookingById(int bookingId) {
        return bookingDao.getBookingById(bookingId);
    }

    public LiveData<List<Booking>> getBookingsByGuest(int guestId) {
        return bookingDao.getBookingsByGuest(guestId);
    }
    
    public LiveData<List<Booking>> getAllBookings() {
        return bookingDao.getAllBookings();
    }

}
