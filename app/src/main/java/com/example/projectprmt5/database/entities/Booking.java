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
 * Booking entity representing guest room bookings
 */
@Entity(tableName = "bookings",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                       parentColumns = "userId",
                       childColumns = "guestId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Room.class,
                       parentColumns = "roomId",
                       childColumns = "roomId",
                       onDelete = ForeignKey.RESTRICT)
        },
        indices = {
            @Index(value = "guestId"),
            @Index(value = "roomId"),
            @Index(value = "bookingCode", unique = true)
        })
@TypeConverters(DateConverter.class)
public class Booking {
    
    @PrimaryKey(autoGenerate = true)
    private int bookingId;
    
    private String bookingCode; // Unique booking reference code
    private int guestId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfGuests;
    private String status; // "PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"
    private double totalAmount;
    private String specialRequests;
    private String qrCodePath; // Path to QR code image
    private Date bookingDate;
    private Date lastUpdatedAt;
    
    // Check-in/out tracking
    private int checkedInByUserId; // Receptionist who checked in
    private Date actualCheckInTime;
    private int checkedOutByUserId; // Receptionist who checked out
    private Date actualCheckOutTime;
    
    // Constructors
    public Booking() {
        this.bookingDate = new Date();
        this.lastUpdatedAt = new Date();
        this.status = BookingStatus.PENDING;
    }
    
    @Ignore
    public Booking(int guestId, int roomId, Date checkInDate, Date checkOutDate, 
                   int numberOfGuests, double totalAmount) {
        this();
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalAmount = totalAmount;
        this.bookingCode = generateBookingCode();
    }
    
    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getBookingCode() {
        return bookingCode;
    }
    
    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
    
    public int getGuestId() {
        return guestId;
    }
    
    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
    
    public int getRoomId() {
        return roomId;
    }
    
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    
    public Date getCheckInDate() {
        return checkInDate;
    }
    
    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public Date getCheckOutDate() {
        return checkOutDate;
    }
    
    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    
    public int getNumberOfGuests() {
        return numberOfGuests;
    }
    
    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public String getQrCodePath() {
        return qrCodePath;
    }
    
    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }
    
    public Date getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    
    public int getCheckedInByUserId() {
        return checkedInByUserId;
    }
    
    public void setCheckedInByUserId(int checkedInByUserId) {
        this.checkedInByUserId = checkedInByUserId;
    }
    
    public Date getActualCheckInTime() {
        return actualCheckInTime;
    }
    
    public void setActualCheckInTime(Date actualCheckInTime) {
        this.actualCheckInTime = actualCheckInTime;
    }
    
    public int getCheckedOutByUserId() {
        return checkedOutByUserId;
    }
    
    public void setCheckedOutByUserId(int checkedOutByUserId) {
        this.checkedOutByUserId = checkedOutByUserId;
    }
    
    public Date getActualCheckOutTime() {
        return actualCheckOutTime;
    }
    
    public void setActualCheckOutTime(Date actualCheckOutTime) {
        this.actualCheckOutTime = actualCheckOutTime;
    }
    
    // Constants
    public static class BookingStatus {
        public static final String PENDING = "PENDING";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String CHECKED_IN = "CHECKED_IN";
        public static final String CHECKED_OUT = "CHECKED_OUT";
        public static final String CANCELLED = "CANCELLED";
    }
}

