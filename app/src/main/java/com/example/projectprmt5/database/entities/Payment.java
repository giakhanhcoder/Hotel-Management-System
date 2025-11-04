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
 * Payment entity for tracking transactions
 */
@Entity(tableName = "payments",
        foreignKeys = {
            @ForeignKey(entity = Booking.class,
                       parentColumns = "bookingId",
                       childColumns = "bookingId",
                       onDelete = ForeignKey.CASCADE)
        },
        indices = {
            @Index(value = "bookingId"),
            @Index(value = "transactionId", unique = true)
        })
@TypeConverters(DateConverter.class)
public class Payment {
    
    @PrimaryKey(autoGenerate = true)
    private int paymentId;
    
    private int bookingId;
    private String transactionId; // From VNPAY
    private double amount;
    private String currency; // "VND"
    private String paymentMethod; // "VNPAY", "CASH", "CARD", "VIETQR"
    private String status; // "PENDING", "SUCCESS", "FAILED", "REFUNDED"
    private String vnpayUrl; // Payment gateway URL
    private String vnpayResponseCode;
    private String vnpayTransactionNo;
    private Date paymentDate;
    private Date completedAt;
    private String receiptPath; // Path to receipt file
    private String notes;
    
    // Refund information
    private boolean isRefunded;
    private double refundAmount;
    private Date refundDate;
    private String refundTransactionId;
    private String refundReason;
    
    // Constructors
    public Payment() {
        this.paymentDate = new Date();
        this.status = PaymentStatus.PENDING;
        this.currency = "VND";
        this.isRefunded = false;
    }
    
    @Ignore
    public Payment(int bookingId, double amount, String paymentMethod) {
        this();
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = generateTransactionId();
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
    
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getVnpayUrl() {
        return vnpayUrl;
    }
    
    public void setVnpayUrl(String vnpayUrl) {
        this.vnpayUrl = vnpayUrl;
    }
    
    public String getVnpayResponseCode() {
        return vnpayResponseCode;
    }
    
    public void setVnpayResponseCode(String vnpayResponseCode) {
        this.vnpayResponseCode = vnpayResponseCode;
    }
    
    public String getVnpayTransactionNo() {
        return vnpayTransactionNo;
    }
    
    public void setVnpayTransactionNo(String vnpayTransactionNo) {
        this.vnpayTransactionNo = vnpayTransactionNo;
    }
    
    public Date getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public Date getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getReceiptPath() {
        return receiptPath;
    }
    
    public void setReceiptPath(String receiptPath) {
        this.receiptPath = receiptPath;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isRefunded() {
        return isRefunded;
    }
    
    public void setRefunded(boolean refunded) {
        isRefunded = refunded;
    }
    
    public double getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public Date getRefundDate() {
        return refundDate;
    }
    
    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }
    
    public String getRefundTransactionId() {
        return refundTransactionId;
    }
    
    public void setRefundTransactionId(String refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }
    
    public String getRefundReason() {
        return refundReason;
    }
    
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
    
    // Constants
    public static class PaymentStatus {
        public static final String PENDING = "PENDING";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
        public static final String REFUNDED = "REFUNDED";
    }
    
    public static class PaymentMethod {
        public static final String VNPAY = "VNPAY";
        public static final String VIETQR = "VIETQR";
        public static final String CASH = "CASH";
        public static final String CARD = "CARD";
    }
}

