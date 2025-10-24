package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.FeedbackDao;
import com.example.projectprmt5.database.entities.Feedback;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Repository class for Feedback entity
 */
public class FeedbackRepository {
    
    private final FeedbackDao feedbackDao;
    
    public FeedbackRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        feedbackDao = database.feedbackDao();
    }
    
    // Insert operations
    public Future<Long> insert(Feedback feedback) {
        return AppDatabase.databaseWriteExecutor.submit(() -> feedbackDao.insert(feedback));
    }
    
    public Future<List<Long>> insertAll(List<Feedback> feedbackList) {
        return AppDatabase.databaseWriteExecutor.submit(() -> feedbackDao.insertAll(feedbackList));
    }
    
    // Update operations
    public Future<Integer> update(Feedback feedback) {
        return AppDatabase.databaseWriteExecutor.submit(() -> feedbackDao.update(feedback));
    }
    
    // Delete operations
    public Future<Integer> delete(Feedback feedback) {
        return AppDatabase.databaseWriteExecutor.submit(() -> feedbackDao.delete(feedback));
    }
    
    public Future<Integer> deleteById(int feedbackId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> feedbackDao.deleteById(feedbackId));
    }
    
    // Query operations
    public LiveData<Feedback> getFeedbackById(int feedbackId) {
        return feedbackDao.getFeedbackById(feedbackId);
    }
    
    public Future<Feedback> getFeedbackByIdSync(int feedbackId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getFeedbackByIdSync(feedbackId)
        );
    }
    
    public Future<Feedback> getFeedbackByBooking(int bookingId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getFeedbackByBooking(bookingId)
        );
    }
    
    public LiveData<Feedback> getFeedbackByBookingLive(int bookingId) {
        return feedbackDao.getFeedbackByBookingLive(bookingId);
    }
    
    public LiveData<List<Feedback>> getFeedbackByGuest(int guestId) {
        return feedbackDao.getFeedbackByGuest(guestId);
    }
    
    public Future<List<Feedback>> getFeedbackByGuestSync(int guestId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getFeedbackByGuestSync(guestId)
        );
    }
    
    public LiveData<List<Feedback>> getAllFeedback() {
        return feedbackDao.getAllFeedback();
    }
    
    public Future<List<Feedback>> getAllFeedbackSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getAllFeedbackSync()
        );
    }
    
    public LiveData<List<Feedback>> getFeedbackByMinRating(float minRating) {
        return feedbackDao.getFeedbackByMinRating(minRating);
    }
    
    public LiveData<List<Feedback>> getFeedbackByRatingRange(float minRating, float maxRating) {
        return feedbackDao.getFeedbackByRatingRange(minRating, maxRating);
    }
    
    public LiveData<List<Feedback>> getFeedbackInDateRange(Date startDate, Date endDate) {
        return feedbackDao.getFeedbackInDateRange(startDate.getTime(), endDate.getTime());
    }
    
    public Future<List<Feedback>> getFeedbackInDateRangeSync(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getFeedbackInDateRangeSync(startDate.getTime(), endDate.getTime())
        );
    }
    
    public LiveData<Float> getAverageRating() {
        return feedbackDao.getAverageRating();
    }
    
    public Future<Float> getAverageRatingSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Float rating = feedbackDao.getAverageRatingSync();
            return rating != null ? rating : 0.0f;
        });
    }
    
    public Future<Float> getAverageCleanlinessRating() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Float rating = feedbackDao.getAverageCleanlinessRating();
            return rating != null ? rating : 0.0f;
        });
    }
    
    public Future<Float> getAverageServiceRating() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Float rating = feedbackDao.getAverageServiceRating();
            return rating != null ? rating : 0.0f;
        });
    }
    
    public Future<Float> getAverageAmenitiesRating() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Float rating = feedbackDao.getAverageAmenitiesRating();
            return rating != null ? rating : 0.0f;
        });
    }
    
    public Future<Float> getAverageValueForMoneyRating() {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Float rating = feedbackDao.getAverageValueForMoneyRating();
            return rating != null ? rating : 0.0f;
        });
    }
    
    public Future<Integer> getTotalFeedbackCount() {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getTotalFeedbackCount()
        );
    }
    
    public LiveData<Integer> getTotalFeedbackCountLive() {
        return feedbackDao.getTotalFeedbackCountLive();
    }
    
    public Future<Integer> getFeedbackCountByMinRating(float minRating) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getFeedbackCountByMinRating(minRating)
        );
    }
    
    public Future<List<Feedback>> getTopRatedFeedback(int limit) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            feedbackDao.getTopRatedFeedback(limit)
        );
    }
}


