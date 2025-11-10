package com.example.projectprmt5.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.repository.PaymentRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public class PaymentViewModel extends AndroidViewModel {

    private final PaymentRepository repository;
    private final LiveData<List<Payment>> allPayments;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        repository = new PaymentRepository(application);
        allPayments = repository.getAllPayments();
    }

    // --- LiveData getters for observing data in UI ---

    public LiveData<List<Payment>> getAllPayments() {
        return allPayments;
    }

    public LiveData<Payment> getPaymentById(int paymentId) {
        return repository.getPaymentById(paymentId);
    }

    public Future<Payment> getPaymentByIdSync(int paymentId) { // Added this method
        return repository.getPaymentByIdSync(paymentId);
    }

    public LiveData<List<Payment>> getPaymentsByBooking(int bookingId) {
        return repository.getPaymentsByBooking(bookingId);
    }
    
    public LiveData<Payment> getPaymentByTransactionIdLive(String transactionId) {
        return repository.getPaymentByTransactionIdLive(transactionId);
    }

    public LiveData<List<Payment>> getPaymentsByStatus(String status) {
        return repository.getPaymentsByStatus(status);
    }
    
    public LiveData<List<Payment>> getPaymentsByMethod(String method) {
        return repository.getPaymentsByMethod(method);
    }

    // --- Asynchronous operations that don't return LiveData ---

    public Future<Long> insert(Payment payment) {
        return repository.insert(payment);
    }

    public Future<Integer> update(Payment payment) {
        return repository.update(payment);
    }

    public Future<Integer> delete(Payment payment) {
        return repository.delete(payment);
    }

    public Future<Integer> updatePaymentStatus(int paymentId, String status) { // Added this method
        return repository.updatePaymentStatus(paymentId, status);
    }

    public Future<Integer> updatePaymentStatusByBookingId(int bookingId, String status) {
        return repository.updatePaymentStatusByBookingId(bookingId, status);
    }

    public Future<Integer> updatePaymentWithVNPAYResponse(int paymentId, String status, String responseCode, String transactionNo) {
        return repository.updatePaymentWithVNPAYResponse(paymentId, status, responseCode, transactionNo);
    }

    public Future<Integer> processRefund(int paymentId, double refundAmount, String refundTransactionId, String refundReason) {
        return repository.processRefund(paymentId, refundAmount, refundTransactionId, refundReason);
    }
    
    public Future<Payment> getLatestPaymentForBooking(int bookingId) {
        return repository.getLatestPaymentForBooking(bookingId);
    }

    // --- You can add other methods from the repository that you need ---
    
}
