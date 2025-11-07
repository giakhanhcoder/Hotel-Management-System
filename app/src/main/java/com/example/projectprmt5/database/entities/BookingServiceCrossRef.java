package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "booking_service_cross_ref",
        primaryKeys = {"bookingId", "serviceId"},
        foreignKeys = {
                @ForeignKey(entity = Booking.class,
                        parentColumns = "bookingId",
                        childColumns = "bookingId"),
                @ForeignKey(entity = Service.class,
                        parentColumns = "serviceId",
                        childColumns = "serviceId")
        })
public class BookingServiceCrossRef {
    private int bookingId;
    private int serviceId;

    public BookingServiceCrossRef(int bookingId, int serviceId) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getServiceId() {
        return serviceId;
    }
}
