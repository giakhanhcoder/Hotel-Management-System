# 📚 HƯỚNG DẪN THÊM DỮ LIỆU VÀO DATABASE

## 🎯 Tổng quan

Database của dự án có **3 cách thêm dữ liệu**:

1. **Dữ liệu mẫu tự động** (prepopulate) - Đã có sẵn
2. **Thêm từ code Java** - Cho tính năng đăng ký, booking, etc.
3. **Thêm qua UI** - Người dùng nhập liệu

---

## 1️⃣ Dữ liệu mẫu đã có sẵn (Prepopulated Data)

Database **tự động tạo** dữ liệu mẫu khi app chạy lần đầu:

### 👤 Users (3 tài khoản)
```java
// MANAGER
Email: admin@hotel.com
Password: Admin123!

// RECEPTIONIST  
Email: receptionist@hotel.com
Password: Receptionist123!

// GUEST
Email: guest@example.com
Password: Guest123!
```

### 🏨 Rooms (4 phòng)
- Phòng 101 (Single) - 500,000 VND/đêm
- Phòng 201 (Double) - 800,000 VND/đêm
- Phòng 301 (Suite) - 1,500,000 VND/đêm
- Phòng 401 (Deluxe) - 2,000,000 VND/đêm

### 📦 Inventory (10+ items)
- Khăn tắm, dầu gội, dầu xả
- Bàn chải đánh răng, nước rửa miệng
- Giấy vệ sinh, nước khoáng
- v.v.

**Xem chi tiết tại:** `app/src/main/java/com/example/projectprmt5/database/AppDatabase.java` (dòng 96-280)

---

## 2️⃣ Thêm dữ liệu từ Code Java

### Cách 1: Dùng Repository (Khuyến nghị ⭐)

#### A. Thêm User mới (Đăng ký)

```java
// Trong RegisterActivity hoặc bất kỳ Activity nào
UserRepository userRepository = new UserRepository(getApplication());

// Tạo user mới
User newUser = new User();
newUser.setEmail("newuser@example.com");
newUser.setPasswordHash(hashPassword("password123")); // Cần hash password
newUser.setFullName("Nguyễn Văn A");
newUser.setPhoneNumber("0901234567");
newUser.setRole(User.UserRole.GUEST);

// Insert vào database (chạy async)
userRepository.insert(newUser, new UserRepository.InsertCallback() {
    @Override
    public void onSuccess(long userId) {
        Toast.makeText(this, "Đăng ký thành công! ID: " + userId, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onError(Exception e) {
        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

#### B. Thêm Room mới (Quản lý phòng)

```java
RoomRepository roomRepository = new RoomRepository(getApplication());

// Tạo phòng mới
com.example.projectprmt5.database.entities.Room newRoom = 
    new com.example.projectprmt5.database.entities.Room();
newRoom.setRoomNumber("501");
newRoom.setRoomType(com.example.projectprmt5.database.entities.Room.RoomType.SUITE);
newRoom.setPricePerNight(1800000);
newRoom.setMaxGuests(4);
newRoom.setDescription("Phòng Suite cao cấp");
newRoom.setFloorNumber(5);

// Thêm amenities
List<String> amenities = Arrays.asList("WiFi", "TV 4K", "Máy lạnh", "MiniBar", "Bồn tắm");
newRoom.setAmenities(amenities);

// Insert
roomRepository.insert(newRoom, new RoomRepository.InsertCallback() {
    @Override
    public void onSuccess(long roomId) {
        Log.d("RoomManagement", "Phòng mới ID: " + roomId);
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("RoomManagement", "Lỗi: " + e.getMessage());
    }
});
```

#### C. Thêm Booking (Đặt phòng)

```java
BookingRepository bookingRepository = new BookingRepository(getApplication());

// Tạo booking
Booking booking = new Booking();
booking.setGuestId(1); // ID của guest
booking.setRoomId(2); // ID của phòng 201
booking.setCheckInDate(new Date()); // Hôm nay
booking.setCheckOutDate(new Date(System.currentTimeMillis() + 3*24*60*60*1000L)); // +3 ngày
booking.setNumberOfGuests(2);
booking.setTotalAmount(800000 * 3); // 3 đêm
booking.setStatus(Booking.BookingStatus.PENDING);
booking.setSpecialRequests("Tầng cao, view đẹp");
booking.setBookingCode("BK" + System.currentTimeMillis());

bookingRepository.insert(booking, new BookingRepository.InsertCallback() {
    @Override
    public void onSuccess(long bookingId) {
        // Tạo Payment cho booking này
        createPayment(bookingId, booking.getTotalAmount());
    }
    
    @Override
    public void onError(Exception e) {
        Toast.makeText(this, "Đặt phòng thất bại", Toast.LENGTH_SHORT).show();
    }
});
```

#### D. Thêm Payment (Thanh toán)

```java
PaymentRepository paymentRepository = new PaymentRepository(getApplication());

Payment payment = new Payment();
payment.setBookingId((int) bookingId);
payment.setAmount(2400000);
payment.setPaymentMethod(Payment.PaymentMethod.VNPAY);
payment.setStatus(Payment.PaymentStatus.PENDING);
payment.setCurrency("VND");
payment.setTransactionId("TXN" + System.currentTimeMillis());

paymentRepository.insert(payment, new PaymentRepository.InsertCallback() {
    @Override
    public void onSuccess(long paymentId) {
        // Redirect đến VNPAY gateway
        openVNPayGateway(payment);
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("Payment", "Error: " + e.getMessage());
    }
});
```

#### E. Thêm Inventory (Quản lý kho)

```java
InventoryRepository inventoryRepository = new InventoryRepository(getApplication());

Inventory item = new Inventory();
item.setItemName("Khăn tắm cao cấp");
item.setItemCode("TOWEL-PREMIUM-001");
item.setCategory(Inventory.Category.LINEN);
item.setCurrentQuantity(50);
item.setMinimumQuantity(10);
item.setUnit("cái");
item.setUnitPrice(150000);
item.setSupplierName("Công ty TNHH Vải Việt");
item.setSupplierContact("0912345678");

inventoryRepository.insert(item, new InventoryRepository.InsertCallback() {
    @Override
    public void onSuccess(long inventoryId) {
        Toast.makeText(this, "Đã thêm vật tư", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("Inventory", "Error: " + e.getMessage());
    }
});
```

#### F. Ghi nhận sử dụng Inventory

```java
InventoryUsageRepository usageRepository = new InventoryUsageRepository(getApplication());

InventoryUsage usage = new InventoryUsage();
usage.setInventoryId(1); // ID của item
usage.setRoomId(101); // Phòng 101
usage.setLoggedByUserId(2); // ID của receptionist
usage.setQuantityUsed(2); // Dùng 2 cái
usage.setUsageType(InventoryUsage.UsageType.ROOM_SERVICE);
usage.setNotes("Cung cấp cho khách phòng 101");

usageRepository.insert(usage, new InventoryUsageRepository.InsertCallback() {
    @Override
    public void onSuccess(long usageId) {
        // Tự động trừ inventory trong database trigger/callback
        Toast.makeText(this, "Đã ghi nhận", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("Usage", "Error: " + e.getMessage());
    }
});
```

#### G. Thêm Feedback (Đánh giá)

```java
FeedbackRepository feedbackRepository = new FeedbackRepository(getApplication());

Feedback feedback = new Feedback();
feedback.setBookingId(1);
feedback.setGuestId(3);
feedback.setRating(4.5f);
feedback.setCleanlinessRating(5.0f);
feedback.setServiceRating(4.5f);
feedback.setAmenitiesRating(4.0f);
feedback.setValueForMoneyRating(4.5f);
feedback.setComment("Phòng sạch sẽ, nhân viên thân thiện. Rất hài lòng!");
feedback.setAnonymous(false);

feedbackRepository.insert(feedback, new FeedbackRepository.InsertCallback() {
    @Override
    public void onSuccess(long feedbackId) {
        Toast.makeText(this, "Cảm ơn đánh giá!", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("Feedback", "Error: " + e.getMessage());
    }
});
```

---

### Cách 2: Dùng DAO trực tiếp (Advanced)

```java
// Lấy database instance
AppDatabase db = AppDatabase.getInstance(getApplicationContext());

// Sử dụng ExecutorService để chạy async
AppDatabase.databaseWriteExecutor.execute(() -> {
    // Thêm user
    User user = new User();
    user.setEmail("test@test.com");
    user.setFullName("Test User");
    user.setPasswordHash("hashedPassword");
    user.setRole(User.UserRole.GUEST);
    
    long userId = db.userDao().insert(user);
    
    // Update UI trên main thread
    runOnUiThread(() -> {
        Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
    });
});
```

---

### Cách 3: Dùng DatabaseHelper (Tiện lợi)

```java
DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

// Thêm user
User user = new User();
user.setEmail("helper@test.com");
user.setFullName("Helper Test");
user.setPasswordHash("password123");
user.setRole(User.UserRole.GUEST);

dbHelper.insertUser(user, new DatabaseHelper.OperationCallback<Long>() {
    @Override
    public void onSuccess(Long userId) {
        Log.d("DB", "Inserted user ID: " + userId);
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("DB", "Error: " + e.getMessage());
    }
});
```

---

## 3️⃣ Update & Delete dữ liệu

### Update

```java
// Update user profile
userRepository.update(updatedUser, new UserRepository.UpdateCallback() {
    @Override
    public void onSuccess() {
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onError(Exception e) {
        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});

// Update room status
room.setStatus(Room.RoomStatus.OCCUPIED);
roomRepository.update(room, callback);

// Update booking status
booking.setStatus(Booking.BookingStatus.CONFIRMED);
bookingRepository.update(booking, callback);
```

### Delete

```java
// Xóa user
userRepository.delete(user, new UserRepository.DeleteCallback() {
    @Override
    public void onSuccess() {
        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onError(Exception e) {
        Toast.makeText(this, "Không thể xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});

// Xóa booking (sẽ cascade delete payment & feedback)
bookingRepository.delete(booking, callback);
```

---

## 4️⃣ Query dữ liệu

### Lấy tất cả records (với LiveData)

```java
// Trong Activity
userRepository.getAllUsers().observe(this, users -> {
    // Update RecyclerView
    userAdapter.setUsers(users);
});

roomRepository.getAllRooms().observe(this, rooms -> {
    roomAdapter.setRooms(rooms);
});

bookingRepository.getAllBookings().observe(this, bookings -> {
    bookingAdapter.setBookings(bookings);
});
```

### Query có điều kiện

```java
// Lấy user theo email
userRepository.getUserByEmail("admin@hotel.com", new UserRepository.QueryCallback<User>() {
    @Override
    public void onSuccess(User user) {
        if (user != null) {
            Log.d("User", "Found: " + user.getFullName());
        }
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("User", "Error: " + e.getMessage());
    }
});

// Lấy phòng available
roomRepository.getRoomsByStatus(Room.RoomStatus.AVAILABLE).observe(this, rooms -> {
    // Hiển thị phòng trống
});

// Lấy booking của 1 guest
bookingRepository.getBookingsByGuestId(guestId).observe(this, bookings -> {
    // Hiển thị lịch sử đặt phòng
});

// Lấy payment theo status
paymentRepository.getPaymentsByStatus(Payment.PaymentStatus.SUCCESS).observe(this, payments -> {
    // Hiển thị các thanh toán thành công
});
```

---

## 5️⃣ Hash Password (Quan trọng! 🔒)

**KHÔNG BAO GIỜ** lưu password dạng plain text!

```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}

// Sử dụng:
String hashed = PasswordUtils.hashPassword("MyPassword123!");
user.setPasswordHash(hashed);
```

---

## 6️⃣ Ví dụ thực tế: Flow đặt phòng hoàn chỉnh

```java
public class BookingActivity extends AppCompatActivity {
    
    private BookingRepository bookingRepository;
    private PaymentRepository paymentRepository;
    
    private void completeBooking() {
        // 1. Tạo booking
        Booking booking = new Booking();
        booking.setGuestId(getCurrentUserId());
        booking.setRoomId(selectedRoomId);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setTotalAmount(calculateTotal());
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setBookingCode(generateBookingCode());
        
        // 2. Insert booking
        bookingRepository.insert(booking, new BookingRepository.InsertCallback() {
            @Override
            public void onSuccess(long bookingId) {
                // 3. Tạo payment
                createPayment((int) bookingId, booking.getTotalAmount());
                
                // 4. Update room status
                updateRoomStatus(selectedRoomId, Room.RoomStatus.RESERVED);
            }
            
            @Override
            public void onError(Exception e) {
                Toast.makeText(BookingActivity.this, 
                    "Đặt phòng thất bại: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void createPayment(int bookingId, double amount) {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentMethod(Payment.PaymentMethod.VNPAY);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        
        paymentRepository.insert(payment, new PaymentRepository.InsertCallback() {
            @Override
            public void onSuccess(long paymentId) {
                // Redirect to VNPAY
                Intent intent = new Intent(BookingActivity.this, VNPayActivity.class);
                intent.putExtra("paymentId", paymentId);
                startActivity(intent);
            }
            
            @Override
            public void onError(Exception e) {
                Log.e("Payment", "Error: " + e.getMessage());
            }
        });
    }
    
    private void updateRoomStatus(int roomId, String status) {
        RoomRepository roomRepository = new RoomRepository(getApplication());
        roomRepository.getRoomById(roomId, new RoomRepository.QueryCallback<Room>() {
            @Override
            public void onSuccess(Room room) {
                room.setStatus(status);
                roomRepository.update(room, new RoomRepository.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Room", "Status updated to: " + status);
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        Log.e("Room", "Update failed: " + e.getMessage());
                    }
                });
            }
            
            @Override
            public void onError(Exception e) {
                Log.e("Room", "Query failed: " + e.getMessage());
            }
        });
    }
}
```

---

## 7️⃣ Kiểm tra dữ liệu trong Database

### Sử dụng Android Studio Database Inspector

1. Chạy app trên emulator/device
2. Menu: **View > Tool Windows > App Inspection**
3. Tab **Database Inspector**
4. Chọn app và database `hotel_management_db`
5. Xem tất cả tables và data realtime!

### Export database file

```java
// Trong code
File dbFile = getDatabasePath("hotel_management_db");
Log.d("DB_PATH", "Database location: " + dbFile.getAbsolutePath());

// Path thường là: 
// /data/data/com.example.projectprmt5/databases/hotel_management_db
```

---

## 8️⃣ Tips & Best Practices

### ✅ Nên làm:

1. **Luôn dùng Repository** thay vì DAO trực tiếp
2. **Chạy database operations trên background thread** (Repository đã làm sẵn)
3. **Dùng LiveData** để observe data changes
4. **Hash password** trước khi lưu
5. **Validate dữ liệu** trước khi insert
6. **Handle errors** với callback

### ❌ Không nên:

1. ~~Chạy database trên Main Thread~~ → ANR (App Not Responding)
2. ~~Lưu password plain text~~ → Bảo mật kém
3. ~~Hardcode values~~ → Dùng constants
4. ~~Bỏ qua error handling~~ → App crash

---

## 9️⃣ Troubleshooting

### Lỗi: "Cannot access database on the main thread"

**Giải pháp:** Dùng Repository hoặc ExecutorService

```java
// ❌ SAI
User user = db.userDao().getUserById(1); // Main thread!

// ✅ ĐÚNG
userRepository.getUserById(1, callback); // Background thread
```

### Lỗi: "FOREIGN KEY constraint failed"

**Nguyên nhân:** Thêm booking với roomId/guestId không tồn tại

**Giải pháp:** Kiểm tra ID tồn tại trước

```java
roomRepository.getRoomById(roomId, new RoomRepository.QueryCallback<Room>() {
    @Override
    public void onSuccess(Room room) {
        if (room != null) {
            // Room tồn tại, OK để tạo booking
            createBooking(roomId);
        } else {
            Toast.makeText(this, "Phòng không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onError(Exception e) {
        Log.e("Room", "Error: " + e.getMessage());
    }
});
```

### Database bị lỗi, muốn reset

```java
// Xóa database và tạo lại
getApplicationContext().deleteDatabase("hotel_management_db");
// Restart app → Database sẽ được tạo lại với dữ liệu mẫu
```

---

## 🎓 Tài liệu tham khảo

- **DATABASE_USAGE.md** - Chi tiết về cấu trúc database
- **DATABASE_README.md** - Tổng quan dự án database
- **app/src/main/java/com/example/projectprmt5/repository/** - Source code Repository
- **app/src/main/java/com/example/projectprmt5/database/dao/** - Source code DAO

---

## 📞 Hỗ trợ

Nếu gặp vấn đề, kiểm tra:
1. Logcat trong Android Studio
2. Database Inspector
3. File `DATABASE_USAGE.md`

**Chúc bạn code vui vẻ! 🚀**


