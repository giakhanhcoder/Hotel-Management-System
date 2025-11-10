package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.PaymentDao;
import com.example.projectprmt5.database.entities.Payment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Repository class for Payment entity
 */
public class PaymentRepository {
    
    private final PaymentDao paymentDao;
    
    public PaymentRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        paymentDao = database.paymentDao();
    }
    
    // Insert operations
    public Future<Long> insert(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.insert(payment));
    }
    
    public Future<List<Long>> insertAll(List<Payment> payments) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.insertAll(payments));
    }
    
    // Update operations
    public Future<Integer> update(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.update(payment));
    }
    
    public Future<Integer> updatePaymentStatus(int paymentId, String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.updatePaymentStatus(paymentId, status)
        );
    }

    public Future<Integer> updatePaymentStatusByBookingId(int bookingId, String status) {
        return AppDatabase.databaseWriteExecutor.submit(() ->
                paymentDao.updatePaymentStatusByBookingId(bookingId, status)
        );
    }
    
    public Future<Integer> updatePaymentWithVNPAYResponse(int paymentId, String status, 
                                                          String responseCode, String transactionNo) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.updatePaymentWithVNPAYResponse(
                paymentId, status, new Date().getTime(), responseCode, transactionNo
            )
        );
    }
    
    public Future<Integer> processRefund(int paymentId, double refundAmount, 
                                        String refundTransactionId, String refundReason) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.processRefund(
                paymentId, refundAmount, new Date().getTime(), 
                refundTransactionId, refundReason
            )
        );
    }
    
    // Delete operations
    public Future<Integer> delete(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.delete(payment));
    }
    
    public Future<Integer> deleteById(int paymentId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.deleteById(paymentId));
    }
    
    // Query operations
    public LiveData<Payment> getPaymentById(int paymentId) {
        return paymentDao.getPaymentById(paymentId);
    }
    
    public Future<Payment> getPaymentByIdSync(int paymentId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.getPaymentByIdSync(paymentId));
    }
    
    public Future<Payment> getPaymentByTransactionId(String transactionId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.getPaymentByTransactionId(transactionId)
        );
    }
    
    public LiveData<Payment> getPaymentByTransactionIdLive(String transactionId) {
        return paymentDao.getPaymentByTransactionIdLive(transactionId);
    }
    
    public LiveData<List<Payment>> getPaymentsByBooking(int bookingId) {
        return paymentDao.getPaymentsByBooking(bookingId);
    }
    
    public Future<List<Payment>> getPaymentsByBookingSync(int bookingId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.getPaymentsByBookingSync(bookingId)
        );
    }

    public Future<Payment> getLatestPaymentForBooking(int bookingId) {
        return AppDatabase.databaseWriteExecutor.submit(() ->
            paymentDao.getLatestPaymentForBooking(bookingId)
        );
    }

    public LiveData<List<Payment>> getPaymentsByStatus(String status) {
        return paymentDao.getPaymentsByStatus(status);
    }
    
    public Future<List<Payment>> getPaymentsByStatusSync(String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.getPaymentsByStatusSync(status)
        );
    }
    
    public LiveData<List<Payment>> getAllPayments() {
        return paymentDao.getAllPayments();
    }
    
    public Future<List<Payment>> getAllPaymentsSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.getAllPaymentsSync());
    }
    
    public LiveData<List<Payment>> getPaymentsInDateRange(Date startDate, Date endDate) {
        return paymentDao.getPaymentsInDateRange(startDate.getTime(), endDate.getTime());
    }
    
    public Future<List<Payment>> getPaymentsInDateRangeSync(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.getPaymentsInDateRangeSync(startDate.getTime(), endDate.getTime())
        );
    }
    
    public Future<Double> getTotalSuccessfulPayments(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Double total = paymentDao.getTotalSuccessfulPayments(startDate.getTime(), endDate.getTime());
            return total != null ? total : 0.0;
        });
    }
    
    public Future<Double> getTotalRefunds(Date startDate, Date endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            Double total = paymentDao.getTotalRefunds(startDate.getTime(), endDate.getTime());
            return total != null ? total : 0.0;
        });
    }
    
    public Future<Integer> getPaymentCountByStatus(String status) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            paymentDao.getPaymentCountByStatus(status)
        );
    }
    
    public LiveData<List<Payment>> getPaymentsByMethod(String method) {
        return paymentDao.getPaymentsByMethod(method);
    }
}











