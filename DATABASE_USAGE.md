# Room Database Usage Guide - Hotel Management System

This document provides a comprehensive guide on how to use the Room Database structure created for the Hotel Management System (HMS).

## Table of Contents
1. [Database Structure](#database-structure)
2. [Setup](#setup)
3. [Basic Usage](#basic-usage)
4. [Repository Pattern](#repository-pattern)
5. [Common Operations](#common-operations)
6. [Best Practices](#best-practices)

---

## Database Structure

The HMS database consists of 7 main entities:

### 1. **User**
- Manages all user accounts (Guests, Receptionists, Managers)
- Fields: userId, email, passwordHash, fullName, role, phoneNumber, etc.
- Roles: GUEST, RECEPTIONIST, MANAGER

### 2. **Room**
- Represents hotel rooms
- Fields: roomId, roomNumber, roomType, status, pricePerNight, maxGuests, etc.
- Room Types: SINGLE, DOUBLE, SUITE, DELUXE
- Room Status: AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED

### 3. **Booking**
- Manages room reservations
- Fields: bookingId, bookingCode, guestId, roomId, checkInDate, checkOutDate, status, etc.
- Booking Status: PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED

### 4. **Payment**
- Tracks payment transactions
- Fields: paymentId, transactionId, bookingId, amount, status, vnpayUrl, etc.
- Payment Status: PENDING, SUCCESS, FAILED, REFUNDED
- Payment Methods: VNPAY, CASH, CARD

### 5. **Inventory**
- Manages hotel supplies and items
- Fields: inventoryId, itemName, itemCode, category, currentQuantity, minimumQuantity, etc.
- Categories: AMENITY, CLEANING, LINEN, FOOD, BEVERAGE, OTHER

### 6. **InventoryUsage**
- Tracks inventory consumption
- Fields: usageId, inventoryId, roomId, quantityUsed, usageType, etc.
- Usage Types: ROOM_SERVICE, CLEANING, MAINTENANCE, RESTOCKING, WASTAGE

### 7. **Feedback**
- Stores guest reviews and ratings
- Fields: feedbackId, bookingId, guestId, rating, comment, etc.
- Ratings: Overall rating and detailed ratings (cleanliness, service, amenities, value)

---

## Setup

### 1. Gradle Dependencies

Already configured in `app/build.gradle`:
```gradle
// Room Database
implementation libs.room.runtime
annotationProcessor libs.room.compiler
implementation libs.room.ktx

// Lifecycle components
implementation libs.lifecycle.viewmodel
implementation libs.lifecycle.livedata

// Coroutines
implementation libs.coroutines.core
implementation libs.coroutines.android

// Gson
implementation libs.gson
```

### 2. Initialize Database

The database is automatically initialized when you first access it:

```java
// In your Application class or Activity
AppDatabase database = AppDatabase.getInstance(context);
```

The database comes pre-populated with:
- 1 Manager account (admin@hotel.com / admin123)
- 1 Receptionist account (receptionist@hotel.com / reception123)
- 1 Guest account (guest@example.com / guest123)
- 15 sample rooms
- 5 sample inventory items

---

## Basic Usage

### 1. Using Repositories (Recommended)

Repositories provide a clean API for database operations:

```java
// In your Activity or Fragment
public class MainActivity extends AppCompatActivity {
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize repositories
        userRepository = new UserRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
        bookingRepository = new BookingRepository(getApplication());
    }
}
```

### 2. Using DAOs Directly (Advanced)

```java
AppDatabase database = AppDatabase.getInstance(context);
UserDao userDao = database.userDao();
RoomDao roomDao = database.roomDao();
```

---

## Repository Pattern

### User Operations

```java
// Login
Future<User> loginFuture = userRepository.login(email, passwordHash);
// Get result in background thread
User user = loginFuture.get();

// Register new user
Future<Long> registerFuture = userRepository.registerUser(
    "newuser@example.com",
    "password123",
    "John Doe",
    User.Role.GUEST,
    "+84901234567"
);
long userId = registerFuture.get();

// Get user by ID (LiveData - observes changes)
LiveData<User> userLiveData = userRepository.getUserById(userId);
userLiveData.observe(this, user -> {
    if (user != null) {
        // Update UI
        textViewName.setText(user.getFullName());
    }
});

// Get all receptionists
LiveData<List<User>> receptionistsLiveData = 
    userRepository.getUsersByRole(User.Role.RECEPTIONIST);
receptionistsLiveData.observe(this, receptionists -> {
    // Update RecyclerView
    adapter.setUsers(receptionists);
});

// Update user
user.setPhoneNumber("+84909999999");
userRepository.update(user);
```

### Room Operations

```java
// Get all available rooms
LiveData<List<Room>> availableRooms = 
    roomRepository.getRoomsByStatus(Room.RoomStatus.AVAILABLE);
availableRooms.observe(this, rooms -> {
    // Display rooms in UI
    roomAdapter.setRooms(rooms);
});

// Search rooms with filters
LiveData<List<Room>> searchResults = 
    roomRepository.searchRoomsWithFilters(
        2,          // numberOfGuests
        500000,     // minPrice
        2000000     // maxPrice
    );

// Add new room
Room newRoom = new Room("501", Room.RoomType.SUITE, 1500000, 4);
newRoom.setDescription("Luxury suite with sea view");
newRoom.setFloorNumber(5);
Future<Long> insertFuture = roomRepository.insert(newRoom);

// Update room status
roomRepository.updateRoomStatus(roomId, Room.RoomStatus.OCCUPIED);

// Get available room count
LiveData<Integer> availableCount = roomRepository.getAvailableRoomCount();
availableCount.observe(this, count -> {
    textViewAvailable.setText("Available: " + count);
});
```

### Booking Operations

```java
// Create new booking
Calendar checkIn = Calendar.getInstance();
checkIn.add(Calendar.DAY_OF_YEAR, 1);

Calendar checkOut = Calendar.getInstance();
checkOut.add(Calendar.DAY_OF_YEAR, 3);

Booking booking = new Booking(
    guestId,
    roomId,
    checkIn.getTime(),
    checkOut.getTime(),
    2,          // numberOfGuests
    1600000.0   // totalAmount (2 nights * 800k)
);
booking.setSpecialRequests("Late check-in");

Future<Long> bookingFuture = bookingRepository.insert(booking);
long bookingId = bookingFuture.get();

// Get guest bookings
LiveData<List<Booking>> guestBookings = 
    bookingRepository.getBookingsByGuest(guestId);
guestBookings.observe(this, bookings -> {
    bookingAdapter.setBookings(bookings);
});

// Check room availability
Date checkInDate = new Date();
Date checkOutDate = new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000);
Future<Boolean> isAvailable = 
    bookingRepository.checkRoomAvailability(roomId, checkInDate, checkOutDate);
if (isAvailable.get()) {
    // Room is available
}

// Check-in guest
bookingRepository.checkIn(bookingId, receptionistId);

// Check-out guest
bookingRepository.checkOut(bookingId, receptionistId);

// Cancel booking
bookingRepository.updateBookingStatus(bookingId, Booking.BookingStatus.CANCELLED);

// Get revenue report
Date startDate = DatabaseHelper.DateRange.getThisMonth().startDate;
Date endDate = DatabaseHelper.DateRange.getThisMonth().endDate;
Future<Double> revenueFuture = 
    bookingRepository.getTotalRevenueInDateRange(startDate, endDate);
double revenue = revenueFuture.get();
```

### Payment Operations

```java
// Create payment
Payment payment = new Payment(
    bookingId,
    1600000.0,
    Payment.PaymentMethod.VNPAY
);
payment.setVnpayUrl("https://vnpay.example.com/payment?id=...");
Future<Long> paymentFuture = paymentRepository.insert(payment);

// Update payment status after VNPAY callback
paymentRepository.updatePaymentWithVNPAYResponse(
    paymentId,
    Payment.PaymentStatus.SUCCESS,
    "00",           // responseCode
    "VNP123456"     // vnpayTransactionNo
);

// Process refund
paymentRepository.processRefund(
    paymentId,
    1600000.0,
    "REF" + System.currentTimeMillis(),
    "Customer cancellation"
);

// Get payments by booking
LiveData<List<Payment>> paymentsLiveData = 
    paymentRepository.getPaymentsByBooking(bookingId);

// Get financial report
Future<Double> totalPayments = 
    paymentRepository.getTotalSuccessfulPayments(startDate, endDate);
Future<Double> totalRefunds = 
    paymentRepository.getTotalRefunds(startDate, endDate);
```

### Inventory Operations

```java
// Get all inventory
LiveData<List<Inventory>> inventoryLiveData = 
    inventoryRepository.getAllActiveInventory();

// Get low stock items
LiveData<List<Inventory>> lowStockLiveData = 
    inventoryRepository.getLowStockItems();
lowStockLiveData.observe(this, items -> {
    // Show alert for low stock items
    if (!items.isEmpty()) {
        showLowStockAlert(items);
    }
});

// Add stock (restocking)
inventoryRepository.addStock(inventoryId, 50);

// Reduce stock (after usage)
inventoryRepository.reduceStock(inventoryId, 5);

// Get inventory by category
LiveData<List<Inventory>> amenitiesLiveData = 
    inventoryRepository.getInventoryByCategory(Inventory.Category.AMENITY);
```

### Inventory Usage Operations

```java
// Log inventory usage
InventoryUsage usage = new InventoryUsage(
    inventoryId,
    2,              // quantityUsed
    receptionistId,
    InventoryUsage.UsageType.ROOM_SERVICE
);
usage.setRoomId(roomId);
usage.setNotes("Complimentary towels for room 201");

// This automatically reduces inventory stock
Future<Long> usageFuture = inventoryUsageRepository.insert(usage);

// Get usage by room
LiveData<List<InventoryUsage>> roomUsageLiveData = 
    inventoryUsageRepository.getUsageByRoom(roomId);

// Get usage report
Future<Integer> totalUsage = 
    inventoryUsageRepository.getTotalUsageForItem(
        inventoryId, 
        startDate, 
        endDate
    );
```

### Feedback Operations

```java
// Submit feedback
Feedback feedback = new Feedback(
    bookingId,
    guestId,
    4.5f,
    "Great stay! Very clean and comfortable."
);
feedback.setCleanlinessRating(5.0f);
feedback.setServiceRating(4.5f);
feedback.setAmenitiesRating(4.0f);
feedback.setValueForMoneyRating(4.5f);

Future<Long> feedbackFuture = feedbackRepository.insert(feedback);

// Get all feedback
LiveData<List<Feedback>> allFeedbackLiveData = 
    feedbackRepository.getAllFeedback();

// Get average rating
LiveData<Float> averageRatingLiveData = 
    feedbackRepository.getAverageRating();
averageRatingLiveData.observe(this, rating -> {
    textViewRating.setText(String.format("%.1f", rating));
});

// Get high-rated feedback (4+ stars)
LiveData<List<Feedback>> topFeedbackLiveData = 
    feedbackRepository.getFeedbackByMinRating(4.0f);
```

---

## Common Operations

### Dashboard Statistics

```java
// Manager Dashboard
public void loadDashboardStats() {
    // Available rooms
    roomRepository.getAvailableRoomCount().observe(this, count -> {
        textViewAvailableRooms.setText(String.valueOf(count));
    });
    
    // Today's bookings
    DatabaseHelper.DateRange today = DatabaseHelper.DateRange.getToday();
    bookingRepository.getBookingsInDateRange(today.startDate, today.endDate)
        .observe(this, bookings -> {
            textViewTodayBookings.setText(String.valueOf(bookings.size()));
        });
    
    // Low stock items count
    inventoryRepository.getLowStockCountLive().observe(this, count -> {
        textViewLowStock.setText(String.valueOf(count));
        if (count > 0) {
            imageViewAlert.setVisibility(View.VISIBLE);
        }
    });
    
    // Average rating
    feedbackRepository.getAverageRating().observe(this, rating -> {
        ratingBar.setRating(rating);
    });
    
    // Monthly revenue
    DatabaseHelper.DateRange thisMonth = DatabaseHelper.DateRange.getThisMonth();
    AppDatabase.databaseWriteExecutor.execute(() -> {
        try {
            double revenue = bookingRepository.getTotalRevenueInDateRange(
                thisMonth.startDate, thisMonth.endDate
            ).get();
            
            runOnUiThread(() -> {
                textViewRevenue.setText(
                    DatabaseHelper.formatCurrency(revenue)
                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}
```

### Search and Filter

```java
// Search users by name or email
searchEditText.addTextChangedListener(new TextWatcher() {
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        userRepository.searchUsers(s.toString()).observe(
            MainActivity.this, 
            users -> adapter.setUsers(users)
        );
    }
    // ... other methods
});

// Filter bookings by status
spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String status = statuses[position];
        bookingRepository.getBookingsByStatus(status).observe(
            MainActivity.this,
            bookings -> bookingAdapter.setBookings(bookings)
        );
    }
    // ... other methods
});
```

---

## Best Practices

### 1. **Always Use Background Threads for Database Operations**
```java
// Good - Using Repository (handles threading automatically)
Future<User> userFuture = userRepository.getUserByIdSync(userId);

// Get result in background
AppDatabase.databaseWriteExecutor.execute(() -> {
    try {
        User user = userFuture.get();
        // Update UI on main thread
        runOnUiThread(() -> {
            textView.setText(user.getFullName());
        });
    } catch (Exception e) {
        e.printStackTrace();
    }
});
```

### 2. **Use LiveData for Observing Changes**
```java
// LiveData automatically updates UI when data changes
LiveData<List<Room>> rooms = roomRepository.getAllActiveRooms();
rooms.observe(this, roomList -> {
    // This runs on main thread automatically
    adapter.setRooms(roomList);
});
```

### 3. **Handle Null Values**
```java
Future<User> userFuture = userRepository.getUserByEmail(email);
AppDatabase.databaseWriteExecutor.execute(() -> {
    try {
        User user = userFuture.get();
        if (user != null) {
            // User exists
        } else {
            // User not found
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
});
```

### 4. **Use Transactions for Complex Operations**
```java
AppDatabase.databaseWriteExecutor.execute(() -> {
    AppDatabase db = AppDatabase.getInstance(context);
    db.runInTransaction(() -> {
        // Multiple operations that should succeed or fail together
        long bookingId = db.bookingDao().insert(booking);
        Payment payment = new Payment((int) bookingId, amount, method);
        db.paymentDao().insert(payment);
        db.roomDao().updateRoomStatus(roomId, Room.RoomStatus.RESERVED);
    });
});
```

### 5. **Validate Data Before Insertion**
```java
if (DatabaseHelper.Validator.isValidEmail(email) &&
    DatabaseHelper.Validator.isValidPassword(password)) {
    userRepository.registerUser(email, password, name, role, phone);
} else {
    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
}
```

### 6. **Clean Up Resources**
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    // LiveData observers are automatically cleaned up
    // But for manual observers, remove them
}
```

---

## Sample ViewModel Implementation

```java
public class BookingViewModel extends AndroidViewModel {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private LiveData<List<Booking>> allBookings;
    
    public BookingViewModel(Application application) {
        super(application);
        bookingRepository = new BookingRepository(application);
        roomRepository = new RoomRepository(application);
        allBookings = bookingRepository.getAllBookings();
    }
    
    public LiveData<List<Booking>> getAllBookings() {
        return allBookings;
    }
    
    public LiveData<List<Booking>> getBookingsByGuest(int guestId) {
        return bookingRepository.getBookingsByGuest(guestId);
    }
    
    public void insert(Booking booking) {
        bookingRepository.insert(booking);
    }
    
    public void checkIn(int bookingId, int receptionistId) {
        bookingRepository.checkIn(bookingId, receptionistId);
    }
}
```

---

## Troubleshooting

### Database Migration Issues
If you change entity structure, the app may crash. For development:
```java
// In AppDatabase.java, .fallbackToDestructiveMigration() is already added
// This will recreate the database on schema changes (data will be lost)
```

### Foreign Key Constraints
When deleting entities with foreign key relationships, be aware:
- Deleting a User will CASCADE delete their Bookings
- Deleting a Room is RESTRICTED if there are active Bookings
- Handle these cases in your UI

### Performance
For large datasets:
- Use pagination with `PagingSource`
- Limit query results with `LIMIT`
- Add appropriate indexes (already done)

---

## Additional Resources

- [Official Room Documentation](https://developer.android.com/training/data-storage/room)
- [LiveData Guide](https://developer.android.com/topic/libraries/architecture/livedata)
- [Repository Pattern](https://developer.android.com/codelabs/android-room-with-a-view)

---

## Support

For issues or questions about the database structure, refer to:
- Entity classes in `app/src/main/java/com/example/projectprmt5/database/entities/`
- DAO interfaces in `app/src/main/java/com/example/projectprmt5/database/dao/`
- Repository classes in `app/src/main/java/com/example/projectprmt5/repository/`


