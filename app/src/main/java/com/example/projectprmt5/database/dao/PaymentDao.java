package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.Payment;

import java.util.List;

/**
 * Data Access Object for Payment entity
 */
@Dao
public interface PaymentDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Payment payment);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Payment> payments);
    
    // Update operations
    @Update
    int update(Payment payment);
    
    // Delete operations
    @Delete
    int delete(Payment payment);
    
    @Query("DELETE FROM payments WHERE paymentId = :paymentId")
    int deleteById(int paymentId);
    
    // Query operations
    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    LiveData<Payment> getPaymentById(int paymentId);
    
    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    Payment getPaymentByIdSync(int paymentId);
    
    @Query("SELECT * FROM payments WHERE transactionId = :transactionId LIMIT 1")
    Payment getPaymentByTransactionId(String transactionId);
    
    @Query("SELECT * FROM payments WHERE transactionId = :transactionId LIMIT 1")
    LiveData<Payment> getPaymentByTransactionIdLive(String transactionId);
    
    @Query("SELECT * FROM payments WHERE bookingId = :bookingId ORDER BY paymentDate DESC")
    LiveData<List<Payment>> getPaymentsByBooking(int bookingId);
    
    @Query("SELECT * FROM payments WHERE bookingId = :bookingId ORDER BY paymentDate DESC")
    List<Payment> getPaymentsByBookingSync(int bookingId);
    
    @Query("SELECT * FROM payments WHERE status = :status ORDER BY paymentDate DESC")
    LiveData<List<Payment>> getPaymentsByStatus(String status);
    
    @Query("SELECT * FROM payments WHERE status = :status ORDER BY paymentDate DESC")
    List<Payment> getPaymentsByStatusSync(String status);
    
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    LiveData<List<Payment>> getAllPayments();
    
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    List<Payment> getAllPaymentsSync();
    
    @Query("UPDATE payments SET status = :status WHERE paymentId = :paymentId")
    int updatePaymentStatus(int paymentId, String status);
    
    @Query("UPDATE payments SET status = :status, completedAt = :completedAt, " +
           "vnpayResponseCode = :responseCode, vnpayTransactionNo = :transactionNo " +
           "WHERE paymentId = :paymentId")
    int updatePaymentWithVNPAYResponse(int paymentId, String status, long completedAt, 
                                       String responseCode, String transactionNo);
    
    @Query("UPDATE payments SET isRefunded = 1, refundAmount = :refundAmount, " +
           "refundDate = :refundDate, refundTransactionId = :refundTransactionId, " +
           "refundReason = :refundReason, status = 'REFUNDED' " +
           "WHERE paymentId = :paymentId")
    int processRefund(int paymentId, double refundAmount, long refundDate, 
                      String refundTransactionId, String refundReason);
    
    @Query("SELECT * FROM payments WHERE paymentDate >= :startDate AND paymentDate <= :endDate " +
           "ORDER BY paymentDate DESC")
    LiveData<List<Payment>> getPaymentsInDateRange(long startDate, long endDate);
    
    @Query("SELECT * FROM payments WHERE paymentDate >= :startDate AND paymentDate <= :endDate " +
           "ORDER BY paymentDate DESC")
    List<Payment> getPaymentsInDateRangeSync(long startDate, long endDate);
    
    @Query("SELECT SUM(amount) FROM payments WHERE status = 'SUCCESS' " +
           "AND paymentDate >= :startDate AND paymentDate <= :endDate")
    Double getTotalSuccessfulPayments(long startDate, long endDate);
    
    @Query("SELECT SUM(refundAmount) FROM payments WHERE isRefunded = 1 " +
           "AND refundDate >= :startDate AND refundDate <= :endDate")
    Double getTotalRefunds(long startDate, long endDate);
    
    @Query("SELECT COUNT(*) FROM payments WHERE status = :status")
    int getPaymentCountByStatus(String status);
    
    @Query("SELECT * FROM payments WHERE paymentMethod = :method ORDER BY paymentDate DESC")
    LiveData<List<Payment>> getPaymentsByMethod(String method);
}


