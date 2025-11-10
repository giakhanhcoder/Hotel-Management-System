package com.example.projectprmt5.database.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.BookingServiceCrossRef;
import com.example.projectprmt5.database.entities.Service;

import java.util.List;

public class BookingWithServices {
    @Embedded
    public Booking booking;

    @Relation(
            parentColumn = "bookingId",
            entityColumn = "serviceId",
            associateBy = @Junction(BookingServiceCrossRef.class)
    )
    public List<Service> services;
}
