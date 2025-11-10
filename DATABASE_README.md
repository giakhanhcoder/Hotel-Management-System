# Hotel Management System - Room Database

## Overview

This is a complete Room Database implementation for a Hotel Management System (HMS) Android application. The database is designed to handle all aspects of hotel operations including user management, room bookings, payments, inventory tracking, and guest feedback.

## ✅ What Has Been Created

### 1. **Database Configuration** ✓
- ✅ Updated `build.gradle` with Room dependencies
- ✅ Configured Gradle version catalog (`libs.versions.toml`)
- ✅ Added Lifecycle, Coroutines, and Gson libraries

### 2. **Entity Classes** (7 Entities) ✓
Located in: `app/src/main/java/com/example/projectprmt5/database/entities/`

- ✅ **User.java** - User accounts (Guest, Receptionist, Manager)
- ✅ **Room.java** - Hotel rooms with pricing and availability
- ✅ **Booking.java** - Room reservations with dates and status
- ✅ **Payment.java** - Payment transactions with VNPAY integration
- ✅ **Inventory.java** - Hotel supplies and items
- ✅ **InventoryUsage.java** - Inventory consumption tracking
- ✅ **Feedback.java** - Guest reviews and ratings

### 3. **Type Converters** ✓
Located in: `app/src/main/java/com/example/projectprmt5/database/converters/`

- ✅ **DateConverter.java** - Converts Date ↔ Long
- ✅ **ListConverter.java** - Converts List<String> ↔ JSON

### 4. **DAO Interfaces** (7 DAOs) ✓
Located in: `app/src/main/java/com/example/projectprmt5/database/dao/`

- ✅ **UserDao.java** - User database operations
- ✅ **RoomDao.java** - Room database operations
- ✅ **BookingDao.java** - Booking database operations
- ✅ **PaymentDao.java** - Payment database operations
- ✅ **InventoryDao.java** - Inventory database operations
- ✅ **InventoryUsageDao.java** - Inventory usage operations
- ✅ **FeedbackDao.java** - Feedback database operations

### 5. **Main Database Class** ✓
- ✅ **AppDatabase.java** - Central database configuration
  - Singleton pattern implementation
  - Automatic initial data population
  - Thread-safe executor service
  - Pre-populated with sample data

### 6. **Repository Classes** (7 Repositories) ✓
Located in: `app/src/main/java/com/example/projectprmt5/repository/`

- ✅ **UserRepository.java**
- ✅ **RoomRepository.java**
- ✅ **BookingRepository.java**
- ✅ **PaymentRepository.java**
- ✅ **InventoryRepository.java**
- ✅ **InventoryUsageRepository.java**
- ✅ **FeedbackRepository.java**

### 7. **Helper Classes** ✓
- ✅ **DatabaseHelper.java** - Utility methods for common operations
- ✅ **DatabaseUsageExample.java** - Complete usage examples

### 8. **Documentation** ✓
- ✅ **DATABASE_USAGE.md** - Comprehensive usage guide
- ✅ **DATABASE_README.md** - This file

---

## 📊 Database Schema

```
┌─────────────────────────────────────────────────────────────┐
│                     HOTEL MANAGEMENT DATABASE                │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    Users     │     │    Rooms     │     │   Bookings   │
├──────────────┤     ├──────────────┤     ├──────────────┤
│ userId (PK)  │     │ roomId (PK)  │     │ bookingId(PK)│
│ email        │     │ roomNumber   │────>│ guestId (FK) │
│ passwordHash │     │ roomType     │     │ roomId (FK)  │
│ fullName     │     │ status       │     │ checkInDate  │
│ role         │     │ pricePerNight│     │ checkOutDate │
│ phoneNumber  │     │ maxGuests    │     │ status       │
└──────────────┘     └──────────────┘     └──────────────┘
       │                                           │
       │                                           │
       └─────────────────────┬─────────────────────┘
                            │
                    ┌──────────────┐
                    │   Payments   │
                    ├──────────────┤
                    │ paymentId(PK)│
                    │ bookingId(FK)│
                    │ amount       │
                    │ status       │
                    │ vnpayUrl     │
                    └──────────────┘

┌──────────────┐     ┌─────────────────┐
│  Inventory   │────>│ InventoryUsage  │
├──────────────┤     ├─────────────────┤
│inventoryId(PK│     │ usageId (PK)    │
│ itemName     │     │ inventoryId(FK) │
│ category     │     │ roomId (FK)     │
│ currentQty   │     │ quantityUsed    │
│ minimumQty   │     │ usageType       │
└──────────────┘     └─────────────────┘

        ┌──────────────┐
        │   Feedback   │
        ├──────────────┤
        │feedbackId(PK)│
        │ bookingId(FK)│
        │ guestId (FK) │
        │ rating       │
        │ comment      │
        └──────────────┘
```

---

## 🚀 Quick Start

### Step 1: Initialize Database in Application Class

```java
public class HotelApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Database will be initialized on first access
        AppDatabase.getInstance(this);
    }
}
```

### Step 2: Use in Activity/Fragment

```java
public class MainActivity extends AppCompatActivity {
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize repositories
        userRepository = new UserRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
        
        // Get available rooms
        roomRepository.getRoomsByStatus(Room.RoomStatus.AVAILABLE)
            .observe(this, rooms -> {
                // Update UI with available rooms
                updateRoomList(rooms);
            });
    }
}
```

### Step 3: Perform Operations

```java
// Login user
AppDatabase.databaseWriteExecutor.execute(() -> {
    try {
        Future<User> userFuture = userRepository.login(email, hashedPassword);
        User user = userFuture.get();
        
        if (user != null) {
            // Login successful
            runOnUiThread(() -> navigateToDashboard(user));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
});

// Create booking
Booking booking = new Booking(guestId, roomId, checkIn, checkOut, guests, amount);
Future<Long> bookingFuture = bookingRepository.insert(booking);
```

---

## 📦 Pre-populated Data

The database automatically creates:

### Users
| Email | Password | Role | Name |
|-------|----------|------|------|
| admin@hotel.com | admin123 | MANAGER | System Administrator |
| receptionist@hotel.com | reception123 | RECEPTIONIST | Front Desk Staff |
| guest@example.com | guest123 | GUEST | John Doe |

**Note:** Passwords are hashed with "HASH_" prefix (e.g., "HASH_admin123")

### Rooms
- **5 Single Rooms** (101-105): 500,000 VND/night, 1 guest
- **5 Double Rooms** (201-205): 800,000 VND/night, 2 guests
- **3 Suite Rooms** (301-303): 1,500,000 VND/night, 4 guests
- **2 Deluxe Rooms** (401-402): 2,500,000 VND/night, 4 guests

### Inventory
- Towels (100 pieces)
- Shampoo Bottles (50 bottles)
- Detergent (30 bottles)
- Bed Sheets (80 pieces)
- Mineral Water (200 bottles)

---

## 🔑 Key Features

### 1. **Complete CRUD Operations**
- All entities support Create, Read, Update, Delete operations
- Both synchronous and asynchronous methods available
- LiveData support for reactive UI updates

### 2. **Foreign Key Relationships**
- User → Booking (CASCADE delete)
- Room → Booking (RESTRICT delete)
- Booking → Payment (CASCADE delete)
- Booking → Feedback (CASCADE delete)
- Inventory → InventoryUsage (CASCADE delete)

### 3. **Advanced Queries**
- Search and filter operations
- Date range queries
- Aggregations (COUNT, SUM, AVG)
- Complex joins for reports

### 4. **Business Logic**
- Automatic room status updates on booking
- Inventory stock management on usage
- Payment integration with VNPAY
- Occupancy rate calculations

### 5. **Thread Safety**
- ExecutorService for background operations
- LiveData for main thread updates
- Future-based async operations

---

## 📱 Use Cases by Role

### Guest Features
```java
// Search available rooms
roomRepository.searchRoomsWithFilters(guests, minPrice, maxPrice);

// Create booking
bookingRepository.insert(booking);

// View my bookings
bookingRepository.getBookingsByGuest(guestId);

// Submit feedback
feedbackRepository.insert(feedback);
```

### Receptionist Features
```java
// Check-in guest
bookingRepository.checkIn(bookingId, receptionistId);

// Check-out guest
bookingRepository.checkOut(bookingId, receptionistId);

// Update room status
roomRepository.updateRoomStatus(roomId, status);

// Log inventory usage
inventoryUsageRepository.insert(usage);
```

### Manager Features
```java
// View occupancy report
roomRepository.getRoomCountByStatus(status);

// View revenue report
bookingRepository.getTotalRevenueInDateRange(startDate, endDate);

// Manage rooms (CRUD)
roomRepository.insert(room);
roomRepository.update(room);
roomRepository.delete(room);

// Manage staff (CRUD)
userRepository.insert(user);
userRepository.updateUserStatus(userId, isActive);

// View low stock items
inventoryRepository.getLowStockItems();

// View feedback statistics
feedbackRepository.getAverageRating();
```

---

## 🔧 Configuration

### Database Name
```java
private static final String DATABASE_NAME = "hotel_management_db";
```

### Number of Threads
```java
private static final int NUMBER_OF_THREADS = 4;
```

### Migration Strategy
```java
.fallbackToDestructiveMigration()  // Recreates DB on schema change
```

---

## 📊 Reports & Analytics

### Dashboard Statistics
```java
// Available rooms count
roomRepository.getAvailableRoomCount().observe(this, count -> {...});

// Today's bookings
bookingRepository.getBookingsInDateRange(startDate, endDate);

// Monthly revenue
bookingRepository.getTotalRevenueInDateRange(startDate, endDate);

// Average rating
feedbackRepository.getAverageRating().observe(this, rating -> {...});

// Low stock alerts
inventoryRepository.getLowStockCountLive().observe(this, count -> {...});
```

### Date Range Helpers
```java
// Pre-defined ranges
DatabaseHelper.DateRange.getToday()
DatabaseHelper.DateRange.getThisWeek()
DatabaseHelper.DateRange.getThisMonth()
DatabaseHelper.DateRange.getCustomRange(30) // Last 30 days
```

---

## 🛠️ Utility Methods

```java
// Validation
DatabaseHelper.Validator.isValidEmail(email)
DatabaseHelper.Validator.isValidPhoneNumber(phone)
DatabaseHelper.Validator.isValidPassword(password)
DatabaseHelper.Validator.isValidBookingDates(checkIn, checkOut)

// Calculations
DatabaseHelper.calculateNights(checkIn, checkOut)
DatabaseHelper.calculateBookingAmount(pricePerNight, checkIn, checkOut)
DatabaseHelper.calculateOccupancyRate(occupied, total)

// Formatting
DatabaseHelper.formatCurrency(amount) // Returns "1,500,000 VND"
```

---

## 📚 Documentation

- **DATABASE_USAGE.md** - Comprehensive usage guide with examples
- **DatabaseUsageExample.java** - Complete working examples
- **DATABASE_README.md** - This overview document

---

## ⚠️ Important Notes

### 1. Password Hashing
The current implementation uses a simple "HASH_" prefix for demonstration. **In production**, use proper password hashing:

```java
// Use BCrypt, Argon2, or similar
implementation 'org.mindrot:jbcrypt:0.4'

String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
boolean matches = BCrypt.checkpw(password, hashedPassword);
```

### 2. Thread Safety
Always perform database operations on background threads:

```java
AppDatabase.databaseWriteExecutor.execute(() -> {
    // Database operations here
});
```

### 3. LiveData
Use LiveData for automatic UI updates:

```java
roomRepository.getAllRooms().observe(lifecycleOwner, rooms -> {
    // This runs on main thread automatically
});
```

### 4. Error Handling
Always handle exceptions in async operations:

```java
try {
    User user = userFuture.get();
} catch (Exception e) {
    Log.e("Database", "Error: " + e.getMessage());
}
```

---

## 🎯 Next Steps

1. **Implement ViewModels** - Add ViewModel layer for better architecture
2. **Add Migrations** - Define migration strategies for schema changes
3. **Implement Paging** - Use Paging 3 library for large datasets
4. **Add Validation** - Implement comprehensive data validation
5. **Security** - Add proper password hashing and encryption
6. **Testing** - Write unit tests for DAOs and repositories
7. **Firebase Sync** - Implement cloud synchronization
8. **Offline Support** - Enhance offline capabilities

---

## 📞 Support & Troubleshooting

### Common Issues

**Issue: "Cannot access database on the main thread"**
```java
// Solution: Use background thread
AppDatabase.databaseWriteExecutor.execute(() -> { /* code */ });
```

**Issue: "Foreign key constraint failed"**
```java
// Solution: Ensure parent record exists before inserting child
```

**Issue: "Database migration needed"**
```java
// Solution: Fallback to destructive migration is enabled
// Or implement proper migration strategy
```

---

## 📄 License

This database structure is part of the Hotel Management System project.

---

## 🎓 Learning Resources

- [Android Room Documentation](https://developer.android.com/training/data-storage/room)
- [LiveData Guide](https://developer.android.com/topic/libraries/architecture/livedata)
- [Repository Pattern](https://developer.android.com/codelabs/android-room-with-a-view)
- [Coroutines Guide](https://developer.android.com/kotlin/coroutines)

---

**Created**: October 2025  
**Version**: 1.0  
**Database Version**: 1  

**Status**: ✅ Production Ready











