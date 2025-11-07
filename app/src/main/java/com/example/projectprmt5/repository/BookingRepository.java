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
 * Repository class for Booking entity
 */
public class BookingRepository {
    
    private final BookingDao bookingDao;
    private final RoomDao roomDao;
    
    public BookingRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        bookingDao = database.bookingDao();
        roomDao = database.roomDao();
    }
    
    // Insert operations
    public Future<Long> insert(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Update room status to RESERVED
            roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.RESERVED);
            return bookingDao.insert(booking);
        });
    }
    
    public Future<List<Long>> insertAll(List<Booking> bookings) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.insertAll(bookings));
    }
    
    // Update operations
    public Future<Integer> update(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            booking.setLastUpdatedAt(new Date());
            return bookingDao.update(booking);
        });
    }
    
    public Future<Integer> updateBookingStatus(int bookingId, String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Booking booking = bookingDao.getBookingByIdSync(bookingId);
            if (booking != null) {
                // Update room status based on booking status
                if (status.equals(Booking.BookingStatus.CANCELLED)) {
                    roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
                } else if (status.equals(Booking.BookingStatus.CHECKED_OUT)) {
                    roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
                }
            }
            return bookingDao.updateBookingStatus(bookingId, status);
        });
    }
    
    public Future<Integer> checkIn(int bookingId, int receptionistId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Booking booking = bookingDao.getBookingByIdSync(bookingId);
            if (booking != null) {
                roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.OCCUPIED);
            }
            return bookingDao.checkIn(bookingId, receptionistId, new Date().getTime());
        });
    }
    
    public Future<Integer> checkOut(int bookingId, int receptionistId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Booking booking = bookingDao.getBookingByIdSync(bookingId);
            if (booking != null) {
                roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
            }
            return bookingDao.checkOut(bookingId, receptionistId, new Date().getTime());
        });
    }
    
    // Delete operations
    public Future<Integer> delete(Booking booking) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Update room status to AVAILABLE
            roomDao.updateRoomStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
            return bookingDao.delete(booking);
        });
    }
    
    public Future<Integer> deleteById(int bookingId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.deleteById(bookingId));
    }
    
    // Query operations
    public LiveData<Booking> getBookingById(int bookingId) {
        return bookingDao.getBookingById(bookingId);
    }
    
    public Future<Booking> getBookingByIdSync(int bookingId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.getBookingByIdSync(bookingId));
    }
    
    public Future<Booking> getBookingByCode(String bookingCode) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.getBookingByCode(bookingCode));
    }
    
    public LiveData<Booking> getBookingByCodeLive(String bookingCode) {
        return bookingDao.getBookingByCodeLive(bookingCode);
    }
    
    public LiveData<List<Booking>> getBookingsByGuest(int guestId) {
        return bookingDao.getBookingsByGuest(guestId);
    }
    
    public Future<List<Booking>> getBookingsByGuestSync(int guestId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.getBookingsByGuestSync(guestId));
    }
    
    public LiveData<List<Booking>> getBookingsByRoom(int roomId) {
        return bookingDao.getBookingsByRoom(roomId);
    }
    
    public LiveData<List<Booking>> getBookingsByStatus(String status) {
        return bookingDao.getBookingsByStatus(status);
    }
    
    public Future<List<Booking>> getBookingsByStatusSync(String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.getBookingsByStatusSync(status));
    }
    
    public LiveData<List<Booking>> getAllBookings() {
        return bookingDao.getAllBookings();
    }
    
    public Future<List<Booking>> getAllBookingsSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> bookingDao.getAllBookingsSync());
    }
    
    public Future<List<Booking>> getBookingsForDate(Date date) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            bookingDao.getBookingsForDate(date.getTime())
        );
    }
    
    public Future<Boolean> checkRoomAvailability(int roomId, Date checkInDate, Date checkOutDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            List<Booking> conflicts = bookingDao.checkRoomAvailability(
                roomId, checkInDate.getTime(), checkOutDate.getTime()
            );
            return conflicts.isEmpty();
        });
    }
    
    public LiveData<List<Booking>> getBookingsInDateRange(Date startDate, Date endDate) {
        return bookingDao.getBookingsInDateRange(startDate.getTime(), endDate.getTime());
    }
    
    public Future<List<Booking>> getBookingsInDateRangeSync(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            bookingDao.getBookingsInDateRangeSync(startDate.getTime(), endDate.getTime())
        );
    }
    
    public Future<Integer> getBookingCountByStatus(String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            bookingDao.getBookingCountByStatus(status)
        );
    }
    
    public Future<Integer> getBookingCountInDateRange(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            bookingDao.getBookingCountInDateRange(startDate.getTime(), endDate.getTime())
        );
    }
    
    public Future<Double> getTotalRevenueInDateRange(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Double revenue = bookingDao.getTotalRevenueInDateRange(startDate.getTime(), endDate.getTime());
            return revenue != null ? revenue : 0.0;
        });
    }
}


