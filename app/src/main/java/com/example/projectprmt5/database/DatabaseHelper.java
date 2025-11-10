package com.example.projectprmt5.database;

import android.content.Context;

import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Inventory;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;

import java.util.Calendar;
import java.util.Date;

/**
 * Helper class with utility methods for database operations
 */
public class DatabaseHelper {
    
    /**
     * Initialize the database with sample data
     * Call this once when the app is first installed
     */
    public static void initializeDatabase(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        // Database will be auto-initialized with callback
    }
    
    /**
     * Calculate date range for queries
     */
    public static class DateRange {
        public Date startDate;
        public Date endDate;
        
        public DateRange(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        /**
         * Get date range for today
         */
        public static DateRange getToday() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startDate = calendar.getTime();
            
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endDate = calendar.getTime();
            
            return new DateRange(startDate, endDate);
        }
        
        /**
         * Get date range for this week
         */
        public static DateRange getThisWeek() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date startDate = calendar.getTime();
            
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endDate = calendar.getTime();
            
            return new DateRange(startDate, endDate);
        }
        
        /**
         * Get date range for this month
         */
        public static DateRange getThisMonth() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date startDate = calendar.getTime();
            
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endDate = calendar.getTime();
            
            return new DateRange(startDate, endDate);
        }
        
        /**
         * Get custom date range
         */
        public static DateRange getCustomRange(int daysBack) {
            Calendar calendar = Calendar.getInstance();
            Date endDate = calendar.getTime();
            
            calendar.add(Calendar.DAY_OF_YEAR, -daysBack);
            Date startDate = calendar.getTime();
            
            return new DateRange(startDate, endDate);
        }
    }
    
    /**
     * Validation helpers
     */
    public static class Validator {
        
        public static boolean isValidEmail(String email) {
            return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }
        
        public static boolean isValidPhoneNumber(String phone) {
            return phone != null && phone.matches("^\\+?[0-9]{10,15}$");
        }
        
        public static boolean isValidPassword(String password) {
            return password != null && password.length() >= 6;
        }
        
        public static boolean isValidBookingDates(Date checkIn, Date checkOut) {
            if (checkIn == null || checkOut == null) return false;
            return checkOut.after(checkIn);
        }
        
        public static boolean isValidRoomPrice(double price) {
            return price > 0;
        }
        
        public static boolean isValidRating(float rating) {
            return rating >= 1.0f && rating <= 5.0f;
        }
    }
    
    /**
     * Query result helpers
     */
    public static class QueryResult<T> {
        public boolean success;
        public T data;
        public String errorMessage;
        
        public static <T> QueryResult<T> success(T data) {
            QueryResult<T> result = new QueryResult<>();
            result.success = true;
            result.data = data;
            return result;
        }
        
        public static <T> QueryResult<T> error(String message) {
            QueryResult<T> result = new QueryResult<>();
            result.success = false;
            result.errorMessage = message;
            return result;
        }
    }
    
    /**
     * Calculate occupancy rate
     */
    public static double calculateOccupancyRate(int occupiedRooms, int totalRooms) {
        if (totalRooms == 0) return 0.0;
        return (occupiedRooms * 100.0) / totalRooms;
    }
    
    /**
     * Calculate booking duration in nights
     */
    public static int calculateNights(Date checkIn, Date checkOut) {
        long diff = checkOut.getTime() - checkIn.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }
    
    /**
     * Calculate total booking amount
     */
    public static double calculateBookingAmount(double pricePerNight, Date checkIn, Date checkOut) {
        int nights = calculateNights(checkIn, checkOut);
        return pricePerNight * nights;
    }
    
    /**
     * Format currency (VND)
     */
    public static String formatCurrency(double amount) {
        return String.format("%,.0f VND", amount);
    }
    
    /**
     * Generate booking code
     */
    public static String generateBookingCode(int guestId) {
        return String.format("BK%d%d", guestId, System.currentTimeMillis() % 100000);
    }
    
    /**
     * Generate transaction ID
     */
    public static String generateTransactionId(int bookingId) {
        return String.format("TXN%d%d", bookingId, System.currentTimeMillis() % 100000);
    }
}












