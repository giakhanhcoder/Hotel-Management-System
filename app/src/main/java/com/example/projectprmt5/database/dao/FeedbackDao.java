package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.Feedback;

import java.util.List;

/**
 * Data Access Object for Feedback entity
 */
@Dao
public interface FeedbackDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Feedback feedback);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Feedback> feedbackList);
    
    // Update operations
    @Update
    int update(Feedback feedback);
    
    // Delete operations
    @Delete
    int delete(Feedback feedback);
    
    @Query("DELETE FROM feedback WHERE feedbackId = :feedbackId")
    int deleteById(int feedbackId);
    
    // Query operations
    @Query("SELECT * FROM feedback WHERE feedbackId = :feedbackId")
    LiveData<Feedback> getFeedbackById(int feedbackId);
    
    @Query("SELECT * FROM feedback WHERE feedbackId = :feedbackId")
    Feedback getFeedbackByIdSync(int feedbackId);
    
    @Query("SELECT * FROM feedback WHERE bookingId = :bookingId LIMIT 1")
    Feedback getFeedbackByBooking(int bookingId);
    
    @Query("SELECT * FROM feedback WHERE bookingId = :bookingId LIMIT 1")
    LiveData<Feedback> getFeedbackByBookingLive(int bookingId);
    
    @Query("SELECT * FROM feedback WHERE guestId = :guestId ORDER BY submittedAt DESC")
    LiveData<List<Feedback>> getFeedbackByGuest(int guestId);
    
    @Query("SELECT * FROM feedback WHERE guestId = :guestId ORDER BY submittedAt DESC")
    List<Feedback> getFeedbackByGuestSync(int guestId);
    
    @Query("SELECT * FROM feedback ORDER BY submittedAt DESC")
    LiveData<List<Feedback>> getAllFeedback();
    
    @Query("SELECT * FROM feedback ORDER BY submittedAt DESC")
    List<Feedback> getAllFeedbackSync();
    
    @Query("SELECT * FROM feedback WHERE rating >= :minRating ORDER BY submittedAt DESC")
    LiveData<List<Feedback>> getFeedbackByMinRating(float minRating);
    
    @Query("SELECT * FROM feedback WHERE rating >= :minRating AND rating <= :maxRating " +
           "ORDER BY submittedAt DESC")
    LiveData<List<Feedback>> getFeedbackByRatingRange(float minRating, float maxRating);
    
    @Query("SELECT * FROM feedback WHERE submittedAt >= :startDate AND submittedAt <= :endDate " +
           "ORDER BY submittedAt DESC")
    LiveData<List<Feedback>> getFeedbackInDateRange(long startDate, long endDate);
    
    @Query("SELECT * FROM feedback WHERE submittedAt >= :startDate AND submittedAt <= :endDate " +
           "ORDER BY submittedAt DESC")
    List<Feedback> getFeedbackInDateRangeSync(long startDate, long endDate);
    
    @Query("SELECT AVG(rating) FROM feedback")
    LiveData<Float> getAverageRating();
    
    @Query("SELECT AVG(rating) FROM feedback")
    Float getAverageRatingSync();
    
    @Query("SELECT AVG(cleanlinessRating) FROM feedback WHERE cleanlinessRating > 0")
    Float getAverageCleanlinessRating();
    
    @Query("SELECT AVG(serviceRating) FROM feedback WHERE serviceRating > 0")
    Float getAverageServiceRating();
    
    @Query("SELECT AVG(amenitiesRating) FROM feedback WHERE amenitiesRating > 0")
    Float getAverageAmenitiesRating();
    
    @Query("SELECT AVG(valueForMoneyRating) FROM feedback WHERE valueForMoneyRating > 0")
    Float getAverageValueForMoneyRating();
    
    @Query("SELECT COUNT(*) FROM feedback")
    int getTotalFeedbackCount();
    
    @Query("SELECT COUNT(*) FROM feedback")
    LiveData<Integer> getTotalFeedbackCountLive();
    
    @Query("SELECT COUNT(*) FROM feedback WHERE rating >= :minRating")
    int getFeedbackCountByMinRating(float minRating);
    
    @Query("SELECT * FROM feedback ORDER BY rating DESC LIMIT :limit")
    List<Feedback> getTopRatedFeedback(int limit);
}


