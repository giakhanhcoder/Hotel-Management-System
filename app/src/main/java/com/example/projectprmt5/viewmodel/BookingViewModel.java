package com.example.projectprmt5.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.repository.BookingRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public class BookingViewModel extends AndroidViewModel {

    private final BookingRepository bookingRepository;
    private final LiveData<List<Booking>> allBookings;

    public BookingViewModel(@NonNull Application application) {
        super(application);
        bookingRepository = new BookingRepository(application);
        allBookings = bookingRepository.getAllBookings();
    }

    public LiveData<List<Booking>> getAllBookings() {
        return allBookings;
    }

    public LiveData<Booking> getBookingById(int id) {
        return bookingRepository.getBookingById(id);
    }

    public Future<Booking> getBookingByIdSync(int id) { // Added this method
        return bookingRepository.getBookingByIdSync(id);
    }

    public Future<Long> insert(Booking booking) {
        return bookingRepository.insert(booking);
    }

    public Future<Integer> update(Booking booking) {
        return bookingRepository.update(booking);
    }

    public Future<Integer> delete(Booking booking) {
        return bookingRepository.delete(booking);
    }

    public Future<Integer> updateBookingStatus(int bookingId, String status) {
        return bookingRepository.updateBookingStatus(bookingId, status);
    }

    public Future<Boolean> isRoomAvailable(int roomId, Date checkIn, Date checkOut) {
        return bookingRepository.isRoomAvailable(roomId, checkIn, checkOut);
    }

    public Future<Room> getRoomByNumber(String roomNumber) {
        return bookingRepository.getRoomByNumber(roomNumber);
    }

    public Future<Integer> checkIn(int bookingId) {
        return bookingRepository.checkIn(bookingId, 0);
    }

    public Future<Integer> checkOut(int bookingId) {
        return bookingRepository.checkOut(bookingId, 0);
    }
}
