package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.Booking;

import java.util.Date;
import java.util.List;

@Dao
public interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Booking booking);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Booking> bookings);

    @Update
    int update(Booking booking);

    @Delete
    int delete(Booking booking);
    
    @Query("DELETE FROM bookings WHERE bookingId = :bookingId")
    int deleteById(int bookingId);

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    LiveData<Booking> getBookingById(int bookingId);
    
    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    Booking getBookingByIdSync(int bookingId);
    
    @Query("SELECT * FROM bookings WHERE bookingCode = :bookingCode LIMIT 1")
    Booking getBookingByCode(String bookingCode);
    
    @Query("SELECT * FROM bookings WHERE bookingCode = :bookingCode LIMIT 1")
    LiveData<Booking> getBookingByCodeLive(String bookingCode);
    
    @Query("SELECT * FROM bookings WHERE guestId = :guestId ORDER BY bookingDate DESC")
    LiveData<List<Booking>> getBookingsByGuest(int guestId);
    
    @Query("SELECT * FROM bookings WHERE guestId = :guestId ORDER BY bookingDate DESC")
    List<Booking> getBookingsByGuestSync(int guestId);
    
    @Query("SELECT * FROM bookings WHERE roomId = :roomId ORDER BY checkInDate DESC")
    LiveData<List<Booking>> getBookingsByRoom(int roomId);
    
    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY bookingDate DESC")
    LiveData<List<Booking>> getBookingsByStatus(String status);
    
    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY bookingDate DESC")
    List<Booking> getBookingsByStatusSync(String status);
    
    @Query("SELECT * FROM bookings ORDER BY bookingDate DESC")
    LiveData<List<Booking>> getAllBookings();
    
    @Query("SELECT * FROM bookings ORDER BY bookingDate DESC")
    List<Booking> getAllBookingsSync();
    
    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    int updateBookingStatus(int bookingId, String status);
    
    @Query("UPDATE bookings SET checkedInByUserId = :receptionistId, actualCheckInTime = :checkInTime, " +
           "status = 'CHECKED_IN' WHERE bookingId = :bookingId")
    int checkIn(int bookingId, int receptionistId, long checkInTime);
    
    @Query("UPDATE bookings SET checkedOutByUserId = :receptionistId, actualCheckOutTime = :checkOutTime, " +
           "status = 'CHECKED_OUT' WHERE bookingId = :bookingId")
    int checkOut(int bookingId, int receptionistId, long checkOutTime);
    
    @Query("SELECT * FROM bookings WHERE checkInDate <= :date AND checkOutDate >= :date " +
           "AND status NOT IN ('CANCELLED', 'CHECKED_OUT')")
    List<Booking> getBookingsForDate(long date);

    @Query("SELECT * FROM bookings WHERE roomId = :roomId " +
           "AND status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
           "AND (checkInDate < :checkOutDate AND :checkInDate < checkOutDate)")
    List<Booking> checkRoomAvailability(int roomId, long checkInDate, long checkOutDate);
    
    @Query("SELECT * FROM bookings WHERE checkInDate >= :startDate AND checkInDate <= :endDate " +
           "ORDER BY checkInDate ASC")
    LiveData<List<Booking>> getBookingsInDateRange(long startDate, long endDate);
    
    @Query("SELECT * FROM bookings WHERE checkInDate >= :startDate AND checkInDate <= :endDate " +
           "ORDER BY checkInDate ASC")
    List<Booking> getBookingsInDateRangeSync(long startDate, long endDate);
    
    @Query("SELECT COUNT(*) FROM bookings WHERE status = :status")
    int getBookingCountByStatus(String status);
    
    @Query("SELECT COUNT(*) FROM bookings WHERE checkInDate >= :startDate AND checkInDate <= :endDate")
    int getBookingCountInDateRange(long startDate, long endDate);
    
    @Query("SELECT SUM(totalAmount) FROM bookings WHERE status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') " +
           "AND bookingDate >= :startDate AND bookingDate <= :endDate")
    Double getTotalRevenueInDateRange(long startDate, long endDate);
}



