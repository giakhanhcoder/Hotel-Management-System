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

@Dao
public interface PaymentDao {

    // --- Insert operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Payment payment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Payment> payments);

    // --- Update operations ---
    @Update
    int update(Payment payment);

    @Query("UPDATE payments SET status = :status WHERE paymentId = :paymentId")
    int updatePaymentStatus(int paymentId, String status);

    @Query("UPDATE payments SET status = :status WHERE bookingId = :bookingId")
    int updatePaymentStatusByBookingId(int bookingId, String status);

    @Query("UPDATE payments SET status = :status, completedAt = :completionTime, vnpayResponseCode = :responseCode, vnpayTransactionNo = :transactionNo WHERE paymentId = :paymentId")
    int updatePaymentWithVNPAYResponse(int paymentId, String status, long completionTime, String responseCode, String transactionNo);

    @Query("UPDATE payments SET isRefunded = 1, refundAmount = :refundAmount, refundDate = :refundDate, refundTransactionId = :refundTransactionId, refundReason = :refundReason, status = 'REFUNDED' WHERE paymentId = :paymentId")
    int processRefund(int paymentId, double refundAmount, long refundDate, String refundTransactionId, String refundReason);

    // --- Delete operations ---
    @Delete
    int delete(Payment payment);

    @Query("DELETE FROM payments WHERE paymentId = :paymentId")
    int deleteById(int paymentId);

    // --- Query operations (LiveData for UI) ---
    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    LiveData<Payment> getPaymentById(int paymentId);

    @Query("SELECT * FROM payments WHERE transactionId = :transactionId")
    LiveData<Payment> getPaymentByTransactionIdLive(String transactionId);

    @Query("SELECT * FROM payments WHERE bookingId = :bookingId")
    LiveData<List<Payment>> getPaymentsByBooking(int bookingId);

    @Query("SELECT * FROM payments WHERE status = :status")
    LiveData<List<Payment>> getPaymentsByStatus(String status);
    
    @Query("SELECT * FROM payments WHERE paymentMethod = :method")
    LiveData<List<Payment>> getPaymentsByMethod(String method);

    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    LiveData<List<Payment>> getAllPayments();

    @Query("SELECT * FROM payments WHERE paymentDate BETWEEN :startDate AND :endDate")
    LiveData<List<Payment>> getPaymentsInDateRange(long startDate, long endDate);

    // --- Synchronous Query operations (for background tasks) ---
    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    Payment getPaymentByIdSync(int paymentId);

    @Query("SELECT * FROM payments WHERE transactionId = :transactionId")
    Payment getPaymentByTransactionId(String transactionId);
    
    @Query("SELECT * FROM payments WHERE bookingId = :bookingId")
    List<Payment> getPaymentsByBookingSync(int bookingId);

    @Query("SELECT * FROM payments WHERE status = :status")
    List<Payment> getPaymentsByStatusSync(String status);

    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    List<Payment> getAllPaymentsSync();

    @Query("SELECT * FROM payments WHERE paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> getPaymentsInDateRangeSync(long startDate, long endDate);

    // --- Aggregate queries ---
    @Query("SELECT SUM(amount) FROM payments WHERE status = 'SUCCESS' AND paymentDate BETWEEN :startDate AND :endDate")
    Double getTotalSuccessfulPayments(long startDate, long endDate);

    @Query("SELECT SUM(refundAmount) FROM payments WHERE isRefunded = 1 AND refundDate BETWEEN :startDate AND :endDate")
    Double getTotalRefunds(long startDate, long endDate);

    @Query("SELECT COUNT(*) FROM payments WHERE status = :status")
    int getPaymentCountByStatus(String status);
}
