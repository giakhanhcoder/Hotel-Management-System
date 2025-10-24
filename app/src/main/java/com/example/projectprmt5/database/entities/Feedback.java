package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.projectprmt5.database.converters.DateConverter;

import java.util.Date;

/**
 * Feedback entity for guest reviews and ratings
 */
@Entity(tableName = "feedback",
        foreignKeys = {
            @ForeignKey(entity = Booking.class,
                       parentColumns = "bookingId",
                       childColumns = "bookingId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = User.class,
                       parentColumns = "userId",
                       childColumns = "guestId",
                       onDelete = ForeignKey.CASCADE)
        },
        indices = {
            @Index(value = "bookingId"),
            @Index(value = "guestId"),
            @Index(value = "rating")
        })
@TypeConverters(DateConverter.class)
public class Feedback {
    
    @PrimaryKey(autoGenerate = true)
    private int feedbackId;
    
    private int bookingId;
    private int guestId;
    private float rating; // 1.0 to 5.0
    private String comment;
    private float cleanlinessRating;
    private float serviceRating;
    private float amenitiesRating;
    private float valueForMoneyRating;
    private boolean isAnonymous;
    private Date submittedAt;
    
    // Constructors
    public Feedback() {
        this.submittedAt = new Date();
        this.isAnonymous = false;
    }
    
    @Ignore
    public Feedback(int bookingId, int guestId, float rating, String comment) {
        this();
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public int getFeedbackId() {
        return feedbackId;
    }
    
    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }
    
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public int getGuestId() {
        return guestId;
    }
    
    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
    
    public float getRating() {
        return rating;
    }
    
    public void setRating(float rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public float getCleanlinessRating() {
        return cleanlinessRating;
    }
    
    public void setCleanlinessRating(float cleanlinessRating) {
        this.cleanlinessRating = cleanlinessRating;
    }
    
    public float getServiceRating() {
        return serviceRating;
    }
    
    public void setServiceRating(float serviceRating) {
        this.serviceRating = serviceRating;
    }
    
    public float getAmenitiesRating() {
        return amenitiesRating;
    }
    
    public void setAmenitiesRating(float amenitiesRating) {
        this.amenitiesRating = amenitiesRating;
    }
    
    public float getValueForMoneyRating() {
        return valueForMoneyRating;
    }
    
    public void setValueForMoneyRating(float valueForMoneyRating) {
        this.valueForMoneyRating = valueForMoneyRating;
    }
    
    public boolean isAnonymous() {
        return isAnonymous;
    }
    
    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }
    
    public Date getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }
}

