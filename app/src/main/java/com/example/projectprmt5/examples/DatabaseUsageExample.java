package com.example.projectprmt5.examples;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.DatabaseHelper;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Inventory;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;
import com.example.projectprmt5.repository.BookingRepository;
import com.example.projectprmt5.repository.InventoryRepository;
import com.example.projectprmt5.repository.PaymentRepository;
import com.example.projectprmt5.repository.RoomRepository;
import com.example.projectprmt5.repository.UserRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Example class demonstrating how to use the Room Database
 * This is for reference only - not meant to be run as-is
 */
public class DatabaseUsageExample extends AppCompatActivity {
    
    // Repositories
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private PaymentRepository paymentRepository;
    private InventoryRepository inventoryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize repositories
        initializeRepositories();
        
        // Example usages
        exampleUserOperations();
        exampleRoomOperations();
        exampleBookingFlow();
        exampleInventoryManagement();
        exampleReports();
    }
    
    private void initializeRepositories() {
        userRepository = new UserRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
        bookingRepository = new BookingRepository(getApplication());
        paymentRepository = new PaymentRepository(getApplication());
        inventoryRepository = new InventoryRepository(getApplication());
    }
    
    // ==================== USER OPERATIONS ====================
    
    private void exampleUserOperations() {
        // 1. User Login
        String email = "guest@example.com";
        String password = "guest123";
        
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Future<User> loginFuture = userRepository.login(email, "HASH_" + password);
                User user = loginFuture.get();
                
                if (user != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Login successful: " + user.getFullName(), 
                                     Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // 2. Register new user
        try {
            Future<Long> registerFuture = userRepository.registerUser(
                "newguest@example.com",
                "password123",
                "Jane Smith",
                User.Role.GUEST,
                "+84905123456"
            );
            
            AppDatabase.databaseWriteExecutor.execute(() -> {
                try {
                    long userId = registerFuture.get();
                    runOnUiThread(() -> {
                        Toast.makeText(this, "User registered with ID: " + userId, 
                                     Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 3. Get all receptionists (using LiveData)
        LiveData<List<User>> receptionistsLiveData = 
            userRepository.getUsersByRole(User.Role.RECEPTIONIST);
        receptionistsLiveData.observe(this, receptionists -> {
            // Update UI with receptionist list
            for (User receptionist : receptionists) {
                System.out.println("Receptionist: " + receptionist.getFullName());
            }
        });
    }
    
    // ==================== ROOM OPERATIONS ====================
    
    private void exampleRoomOperations() {
        // 1. Get all available rooms
        LiveData<List<Room>> availableRoomsLiveData = 
            roomRepository.getRoomsByStatus(Room.RoomStatus.AVAILABLE);
        availableRoomsLiveData.observe(this, rooms -> {
            System.out.println("Available rooms: " + rooms.size());
        });
        
        // 2. Search rooms with filters
        LiveData<List<Room>> searchResults = roomRepository.searchRoomsWithFilters(
            2,          // number of guests
            500000,     // min price
            2000000     // max price
        );
        searchResults.observe(this, rooms -> {
            System.out.println("Search results: " + rooms.size());
        });
        
        // 3. Add new room
        Room newRoom = new Room("601", Room.RoomType.DELUXE, 2000000, 3);
        newRoom.setDescription("Premium room with balcony");
        newRoom.setFloorNumber(6);
        roomRepository.insert(newRoom);
        
        // 4. Update room status
        roomRepository.updateRoomStatus(1, Room.RoomStatus.MAINTENANCE);
    }
    
    // ==================== COMPLETE BOOKING FLOW ====================
    
    private void exampleBookingFlow() {
        // Step 1: Guest searches for available rooms
        int guestId = 3; // Assuming guest is logged in
        int roomId = 1;  // Selected room
        
        // Step 2: Check room availability
        Calendar checkInCal = Calendar.getInstance();
        checkInCal.add(Calendar.DAY_OF_YEAR, 1);
        Date checkInDate = checkInCal.getTime();
        
        Calendar checkOutCal = Calendar.getInstance();
        checkOutCal.add(Calendar.DAY_OF_YEAR, 3);
        Date checkOutDate = checkOutCal.getTime();
        
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Future<Boolean> availabilityFuture = 
                    bookingRepository.checkRoomAvailability(roomId, checkInDate, checkOutDate);
                
                if (availabilityFuture.get()) {
                    // Step 3: Calculate amount
                    Future<Room> roomFuture = roomRepository.getRoomByIdSync(roomId);
                    Room room = roomFuture.get();
                    
                    double totalAmount = DatabaseHelper.calculateBookingAmount(
                        room.getPricePerNight(), 
                        checkInDate, 
                        checkOutDate
                    );
                    
                    // Step 4: Create booking
                    Booking booking = new Booking(
                        guestId,
                        roomId,
                        checkInDate,
                        checkOutDate,
                        2, // number of guests
                        totalAmount
                    );
                    booking.setSpecialRequests("Non-smoking room");
                    
                    Future<Long> bookingFuture = bookingRepository.insert(booking);
                    long bookingId = bookingFuture.get();
                    
                    // Step 5: Create payment
                    Payment payment = new Payment(
                        (int) bookingId,
                        totalAmount,
                        Payment.PaymentMethod.VNPAY
                    );
                    payment.setVnpayUrl("https://vnpay.example.com/...");
                    
                    Future<Long> paymentFuture = paymentRepository.insert(payment);
                    long paymentId = paymentFuture.get();
                    
                    // Step 6: Update booking status to CONFIRMED
                    bookingRepository.updateBookingStatus(
                        (int) bookingId, 
                        Booking.BookingStatus.CONFIRMED
                    );
                    
                    // Step 7: Update payment status (after VNPAY callback)
                    paymentRepository.updatePaymentWithVNPAYResponse(
                        (int) paymentId,
                        Payment.PaymentStatus.SUCCESS,
                        "00", // success code
                        "VNP123456789"
                    );
                    
                    runOnUiThread(() -> {
                        Toast.makeText(this, 
                            "Booking successful! ID: " + bookingId, 
                            Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, 
                            "Room not available for selected dates", 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    // ==================== CHECK-IN/CHECK-OUT FLOW ====================
    
    private void exampleCheckInCheckOut() {
        int bookingId = 1;
        int receptionistId = 2;
        
        // Check-in
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                bookingRepository.checkIn(bookingId, receptionistId).get();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Guest checked in successfully", 
                                 Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Later... Check-out
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                bookingRepository.checkOut(bookingId, receptionistId).get();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Guest checked out successfully", 
                                 Toast.LENGTH_SHORT).show();
                });
                
                // Request feedback
                // (In real app, send notification to guest)
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    // ==================== INVENTORY MANAGEMENT ====================
    
    private void exampleInventoryManagement() {
        // 1. Check low stock items
        LiveData<List<Inventory>> lowStockLiveData = inventoryRepository.getLowStockItems();
        lowStockLiveData.observe(this, items -> {
            if (!items.isEmpty()) {
                System.out.println("Low stock alert! " + items.size() + " items need restocking");
                for (Inventory item : items) {
                    System.out.println("  - " + item.getItemName() + 
                                     ": " + item.getCurrentQuantity() + " " + item.getUnit());
                }
            }
        });
        
        // 2. Restock inventory
        int inventoryId = 1;
        inventoryRepository.addStock(inventoryId, 50);
        
        // 3. Log inventory usage (receptionist logs towel usage for a room)
        // This is handled by InventoryUsageRepository - see example below
    }
    
    // ==================== FEEDBACK ====================
    

    
    // ==================== REPORTS ====================
    
    private void exampleReports() {
        // 1. Daily occupancy report
        DatabaseHelper.DateRange today = DatabaseHelper.DateRange.getToday();
        
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get total rooms
                Future<List<Room>> allRoomsFuture = roomRepository.getAllActiveRoomsSync();
                int totalRooms = allRoomsFuture.get().size();
                
                // Get occupied rooms
                Future<Integer> occupiedCountFuture = 
                    roomRepository.getRoomCountByStatus(Room.RoomStatus.OCCUPIED);
                int occupiedRooms = occupiedCountFuture.get();
                
                // Calculate occupancy rate
                double occupancyRate = DatabaseHelper.calculateOccupancyRate(
                    occupiedRooms, 
                    totalRooms
                );
                
                runOnUiThread(() -> {
                    System.out.println("Occupancy Rate: " + 
                                     String.format("%.1f%%", occupancyRate));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // 2. Monthly revenue report
        DatabaseHelper.DateRange thisMonth = DatabaseHelper.DateRange.getThisMonth();
        
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Future<Double> revenueFuture = bookingRepository.getTotalRevenueInDateRange(
                    thisMonth.startDate, 
                    thisMonth.endDate
                );
                double revenue = revenueFuture.get();
                
                Future<Double> paymentsFuture = paymentRepository.getTotalSuccessfulPayments(
                    thisMonth.startDate, 
                    thisMonth.endDate
                );
                double payments = paymentsFuture.get();
                
                Future<Double> refundsFuture = paymentRepository.getTotalRefunds(
                    thisMonth.startDate, 
                    thisMonth.endDate
                );
                double refunds = refundsFuture.get();
                
                double netRevenue = payments - refunds;
                
                runOnUiThread(() -> {
                    System.out.println("Monthly Revenue Report:");
                    System.out.println("  Gross Revenue: " + 
                                     DatabaseHelper.formatCurrency(revenue));
                    System.out.println("  Total Payments: " + 
                                     DatabaseHelper.formatCurrency(payments));
                    System.out.println("  Total Refunds: " + 
                                     DatabaseHelper.formatCurrency(refunds));
                    System.out.println("  Net Revenue: " + 
                                     DatabaseHelper.formatCurrency(netRevenue));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        

    }
}











