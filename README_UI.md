# HƯỚNG DẪN GIAO DIỆN - HOTEL MANAGEMENT SYSTEM

## 📋 MỤC LỤC
1. [Tổng Quan](#tổng-quan)
2. [Cấu Trúc Resources](#cấu-trúc-resources)
3. [Hướng Dẫn Sử Dụng](#hướng-dẫn-sử-dụng)
4. [Phân Công Công Việc](#phân-công-công-việc)
5. [Quy Tắc Code](#quy-tắc-code)
6. [Ví Dụ Minh Họa](#ví-dụ-minh-họa)

---

## 🎨 TỔNG QUAN

Dự án sử dụng **Material Design 3** với hệ thống màu sắc và style thống nhất. Tất cả thành viên **BẮT BUỘC** phải sử dụng các resources chung đã được định nghĩa sẵn.

### Màu Sắc Chính
- **Primary (Màu chủ đạo):** `#1976D2` (Xanh dương)
- **Accent (Màu nhấn):** `#FF9800` (Cam)
- **Background:** `#F5F5F5` (Xám nhạt)

### Font Chữ
- **Tiêu đề:** Sans-serif Medium
- **Nội dung:** Sans-serif Regular

---

## 📁 CẤU TRÚC RESOURCES

```
res/
├── values/
│   ├── colors.xml       ✅ Tất cả màu sắc
│   ├── dimens.xml       ✅ Kích thước, margin, padding
│   ├── strings.xml      ✅ Text hiển thị
│   ├── styles.xml       ✅ Style cho text, button, etc.
│   └── themes.xml       ✅ Theme chung của app
├── drawable/
│   ├── bg_button_primary.xml      ✅ Background button chính
│   ├── bg_button_secondary.xml    ✅ Background button phụ
│   ├── bg_button_outlined.xml     ✅ Background button viền
│   ├── bg_edittext.xml            ✅ Background EditText
│   ├── bg_card.xml                ✅ Background Card
│   ├── bg_badge_*.xml             ✅ Background cho badge trạng thái
│   └── bg_ripple.xml              ✅ Hiệu ứng ripple khi click
└── layout/
    ├── toolbar_common.xml         ✅ Toolbar chung
    ├── item_room_card.xml         ✅ Mẫu card phòng
    ├── item_booking_card.xml      ✅ Mẫu card đặt phòng
    └── activity_login_template.xml ✅ Mẫu màn hình login
```

---

## 🎯 HƯỚNG DẪN SỬ DỤNG

### 1. SỬ DỤNG MÀU SẮC

**❌ KHÔNG LÀM NHƯ NÀY:**
```xml
<TextView
    android:textColor="#FF0000" />  <!-- SAI! -->
```

**✅ LÀM NHƯ NÀY:**
```xml
<TextView
    android:textColor="@color/text_primary" />  <!-- ĐÚNG! -->
```

#### Bảng Màu Sắc Thường Dùng

| Mục đích | Resource | Mã màu |
|----------|----------|--------|
| Màu chính | `@color/primary` | #1976D2 |
| Màu nền | `@color/background` | #F5F5F5 |
| Chữ chính | `@color/text_primary` | #212121 |
| Chữ phụ | `@color/text_secondary` | #757575 |
| Thành công | `@color/success` | #4CAF50 |
| Cảnh báo | `@color/warning` | #FF9800 |
| Lỗi | `@color/error` | #F44336 |

**Màu trạng thái phòng:**
```xml
<!-- Phòng trống -->
<TextView android:textColor="@color/room_available" />

<!-- Phòng đã thuê -->
<TextView android:textColor="@color/room_occupied" />

<!-- Phòng đã đặt -->
<TextView android:textColor="@color/room_reserved" />
```

### 2. SỬ DỤNG KÍCH THƯỚC

**❌ SAI:**
```xml
<TextView
    android:textSize="16sp"
    android:padding="16dp" />
```

**✅ ĐÚNG:**
```xml
<TextView
    android:textSize="@dimen/text_size_medium"
    android:padding="@dimen/padding_normal" />
```

#### Bảng Kích Thước

| Loại | Resource | Giá trị |
|------|----------|---------|
| **Padding/Margin** |
| Nhỏ | `@dimen/padding_small` | 8dp |
| Bình thường | `@dimen/padding_normal` | 16dp |
| Lớn | `@dimen/padding_medium` | 24dp |
| **Kích thước chữ** |
| Nhỏ | `@dimen/text_size_small` | 12sp |
| Bình thường | `@dimen/text_size_normal` | 14sp |
| Lớn | `@dimen/text_size_large` | 18sp |
| Tiêu đề | `@dimen/text_size_title` | 24sp |
| **Bo góc** |
| Nhỏ | `@dimen/corner_radius_small` | 4dp |
| Bình thường | `@dimen/corner_radius_normal` | 8dp |

### 3. SỬ DỤNG STRING

**❌ SAI:**
```xml
<Button android:text="Đăng nhập" />  <!-- Hard-code text -->
```

**✅ ĐÚNG:**
```xml
<Button android:text="@string/login" />
```

**Trong Java:**
```java
// ✅ ĐÚNG
String loginText = getString(R.string.login);
textView.setText(R.string.login);

// ❌ SAI
textView.setText("Đăng nhập");  // Không hard-code!
```

### 4. SỬ DỤNG STYLES

#### Button

```xml
<!-- Button chính (màu primary) -->
<Button
    style="@style/Button.Primary"
    android:text="@string/login" />

<!-- Button phụ (màu background) -->
<Button
    style="@style/Button.Secondary"
    android:text="@string/cancel" />

<!-- Button viền -->
<Button
    style="@style/Button.Outlined"
    android:text="@string/cancel" />
```

#### EditText

```xml
<EditText
    style="@style/EditText"
    android:hint="@string/email"
    android:inputType="textEmailAddress" />
```

#### Text Appearance

```xml
<!-- Tiêu đề lớn -->
<TextView
    android:textAppearance="@style/TextAppearance.Headline"
    android:text="Trang chủ" />

<!-- Tiêu đề nhỏ -->
<TextView
    android:textAppearance="@style/TextAppearance.Title"
    android:text="Danh sách phòng" />

<!-- Nội dung -->
<TextView
    android:textAppearance="@style/TextAppearance.Body"
    android:text="Mô tả phòng..." />

<!-- Chú thích -->
<TextView
    android:textAppearance="@style/TextAppearance.Caption"
    android:text="Số khách: 2" />
```

#### Badge (Trạng thái)

```xml
<!-- Badge thành công (màu xanh) -->
<TextView
    style="@style/Badge.Success"
    android:text="Đã xác nhận" />

<!-- Badge cảnh báo (màu cam) -->
<TextView
    style="@style/Badge.Warning"
    android:text="Chờ xác nhận" />

<!-- Badge lỗi (màu đỏ) -->
<TextView
    style="@style/Badge.Error"
    android:text="Đã hủy" />
```

### 5. SỬ DỤNG CARD

```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="@dimen/margin_small"
    app:cardCornerRadius="@dimen/corner_radius_normal"
    app:cardElevation="@dimen/elevation_normal"
    android:foreground="@drawable/bg_ripple"
    android:clickable="true"
    android:focusable="true">
    
    <!-- Nội dung card -->
    
</com.google.android.material.card.MaterialCardView>
```

### 6. HIỂN THỊ TRẠNG THÁI

#### Trạng thái Phòng

```java
public void setRoomStatus(TextView textView, String status) {
    int colorRes;
    String statusText;
    
    switch (status) {
        case Room.RoomStatus.AVAILABLE:
            colorRes = R.color.room_available;
            statusText = getString(R.string.room_available);
            break;
        case Room.RoomStatus.OCCUPIED:
            colorRes = R.color.room_occupied;
            statusText = getString(R.string.room_occupied);
            break;
        case Room.RoomStatus.RESERVED:
            colorRes = R.color.room_reserved;
            statusText = getString(R.string.room_reserved);
            break;
        default:
            colorRes = R.color.room_maintenance;
            statusText = getString(R.string.room_maintenance);
    }
    
    textView.setText(statusText);
    textView.setBackgroundResource(getBadgeDrawable(status));
    textView.setTextColor(getColor(R.color.text_white));
}

private int getBadgeDrawable(String status) {
    if (status.equals(Room.RoomStatus.AVAILABLE)) {
        return R.drawable.bg_badge_success;
    } else if (status.equals(Room.RoomStatus.RESERVED)) {
        return R.drawable.bg_badge_warning;
    } else if (status.equals(Room.RoomStatus.OCCUPIED)) {
        return R.drawable.bg_badge_error;
    }
    return R.drawable.bg_badge;
}
```

---

## 👥 PHÂN CÔNG CÔNG VIỆC - 7 MÀN HÌNH MỖI NGƯỜI (35 MÀN TỔNG)

### **👤 NGƯỜI 1: AUTHENTICATION & USER PROFILE (7 màn) - Độ khó: ⭐⭐**

**Màn hình cần làm:**
1. **SplashActivity** - Màn hình khởi động với logo (2-3 giây)
2. **WelcomeActivity** - Màn chào mừng, chọn Login/Register
3. **LoginActivity** ✅ - Đăng nhập (có template `activity_login_template.xml`)
4. **RegisterActivity** - Đăng ký tài khoản mới
5. **ForgotPasswordActivity** - Quên mật khẩu, gửi email reset
6. **ProfileActivity** - Xem và chỉnh sửa hồ sơ cá nhân
7. **ChangePasswordActivity** - Đổi mật khẩu

**Database liên quan:**
- User entity ✅
- UserRepository ✅

**Checklist chi tiết:**
- [ ] 1. SplashActivity: Logo + tự động chuyển sang Welcome sau 2s
- [ ] 2. WelcomeActivity: 2 button (Login/Register) + ảnh hotel đẹp
- [ ] 3. LoginActivity: Email + Password + Remember me + Validation
- [ ] 4. RegisterActivity: Full form đăng ký + Upload ID photo
- [ ] 5. ForgotPasswordActivity: Nhập email + gửi link reset
- [ ] 6. ProfileActivity: Hiển thị và edit thông tin user
- [ ] 7. ChangePasswordActivity: Old password + New password + Confirm

**Navigation Flow:**
```
Splash → Welcome → Login → Dashboard
         ↓
      Register → Login
```

---

### **🏠 NGƯỜI 2: ROOM MANAGEMENT (7 màn) - Độ khó: ⭐⭐⭐**

**Màn hình cần làm:**
1. **RoomListActivity** ✅ - Danh sách phòng (RecyclerView với `item_room_card.xml`)
2. **RoomDetailActivity** - Chi tiết phòng với ảnh, mô tả, tiện nghi
3. **RoomSearchActivity** - Tìm kiếm phòng với filter (loại, giá, số khách)
4. **RoomAddActivity** - Thêm phòng mới (Manager only)
5. **RoomEditActivity** - Sửa thông tin phòng (Manager only)
6. **RoomGalleryActivity** - Xem ảnh phòng full screen (swipe gallery)
7. **RoomAvailabilityCalendarActivity** - Lịch trống/đã đặt của phòng

**Database liên quan:**
- Room entity ✅
- RoomRepository ✅
- BookingRepository ✅ (để check availability)

**Checklist chi tiết:**
- [ ] 1. RoomListActivity: RecyclerView + Pull to refresh + Màu trạng thái
- [ ] 2. RoomDetailActivity: ViewPager ảnh + Info + Button "Đặt ngay"
- [ ] 3. RoomSearchActivity: Filter by type, price range, guests
- [ ] 4. RoomAddActivity: Form đầy đủ + Upload nhiều ảnh
- [ ] 5. RoomEditActivity: Edit tất cả fields + Update ảnh
- [ ] 6. RoomGalleryActivity: ViewPager2 full screen + Zoom ảnh
- [ ] 7. RoomAvailabilityCalendarActivity: CalendarView + Màu ngày available/booked

**Navigation Flow:**
```
RoomList → RoomDetail → BookingCreate
    ↓          ↓
RoomSearch  RoomGallery
    
Manager: RoomList → RoomAdd/Edit
```

---

### **📅 NGƯỜI 3: BOOKING MANAGEMENT (7 màn) - Độ khó: ⭐⭐⭐**

**Màn hình cần làm:**
1. **BookingListActivity** ✅ - Danh sách booking (RecyclerView với `item_booking_card.xml`)
2. **BookingCreateActivity** - Tạo booking mới (chọn ngày DatePicker)
3. **BookingDetailActivity** - Chi tiết booking + QR code
4. **BookingEditActivity** - Sửa booking (đổi ngày, số khách)
5. **CheckInActivity** - Check-in khách (Receptionist - scan ID, assign room)
6. **CheckOutActivity** - Check-out khách (Receptionist - tính tiền phát sinh)
7. **BookingHistoryActivity** - Lịch sử booking của khách (filter by status)

**Database liên quan:**
- Booking entity ✅
- BookingRepository ✅
- RoomRepository ✅
- UserRepository ✅

**Checklist chi tiết:**
- [ ] 1. BookingListActivity: RecyclerView + Filter by status + Swipe actions
- [ ] 2. BookingCreateActivity: DateRangePicker + Chọn room + Tính tổng tiền
- [ ] 3. BookingDetailActivity: Full info + QR code + Button cancel/edit
- [ ] 4. BookingEditActivity: Chỉ cho phép sửa khi status = PENDING
- [ ] 5. CheckInActivity: Scan booking code + Verify guest + Assign room key
- [ ] 6. CheckOutActivity: Hiển thị bill + Check damages + Process payment
- [ ] 7. BookingHistoryActivity: List + Filter (All/Upcoming/Past/Cancelled)

**Navigation Flow:**
```
RoomDetail → BookingCreate → Payment → BookingDetail
BookingList → BookingDetail → BookingEdit
           → CheckIn → CheckOut
```

---

### **💳 NGƯỜI 4: PAYMENT & INVENTORY (7 màn) - Độ khó: ⭐⭐⭐⭐**

**Màn hình cần làm:**
1. **PaymentActivity** - Thanh toán VNPAY (WebView)
2. **PaymentSuccessActivity** - Thanh toán thành công (download receipt)
3. **PaymentHistoryActivity** - Lịch sử thanh toán (filter by status)
4. **InventoryListActivity** - Danh sách kho (RecyclerView)
5. **InventoryDetailActivity** - Chi tiết item kho + Supplier info
6. **InventoryAddEditActivity** - Thêm/sửa item kho (Manager)
7. **InventoryUsageLogActivity** - Log sử dụng kho (Receptionist - cho room)

**Database liên quan:**
- Payment entity ✅
- PaymentRepository ✅
- Inventory entity ✅
- InventoryRepository ✅
- InventoryUsage entity ✅
- InventoryUsageRepository ✅

**Checklist chi tiết:**
- [ ] 1. PaymentActivity: WebView VNPAY + Handle callback + Loading
- [ ] 2. PaymentSuccessActivity: Receipt + Download PDF + Share
- [ ] 3. PaymentHistoryActivity: RecyclerView + Filter + Refund button
- [ ] 4. InventoryListActivity: Badge màu đỏ nếu low stock
- [ ] 5. InventoryDetailActivity: Chart usage history + Restock button
- [ ] 6. InventoryAddEditActivity: Form + Barcode scanner + Set threshold
- [ ] 7. InventoryUsageLogActivity: Select room + Select items + Quantity

**Navigation Flow:**
```
BookingCreate → Payment → PaymentSuccess → BookingDetail
                    ↓
              PaymentHistory

InventoryList → InventoryDetail → InventoryUsageLog
            → InventoryAddEdit
```

---

### **📊 NGƯỜI 5: DASHBOARD, REPORTS & FEEDBACK (7 màn) - Độ khó: ⭐⭐⭐⭐**

**Màn hình cần làm:**
1. **GuestDashboardActivity** - Dashboard cho khách (My bookings, Notifications)
2. **ReceptionistDashboardActivity** - Dashboard lễ tân (Today check-in/out, Tasks)
3. **ManagerDashboardActivity** - Dashboard quản lý (Charts, KPIs, Reports)
4. **RevenueReportActivity** - Báo cáo doanh thu (Charts + Export Excel)
5. **OccupancyReportActivity** - Báo cáo tỷ lệ lấp đầy (Pie chart + Table)
6. **FeedbackListActivity** - Danh sách đánh giá (RecyclerView + Average rating)
7. **FeedbackFormActivity** - Form gửi đánh giá (RatingBar + Comment)

**Database liên quan:**
- Tất cả entities (để tạo reports) ✅
- Feedback entity ✅
- FeedbackRepository ✅

**Checklist chi tiết:**
- [ ] 1. GuestDashboardActivity: Welcome + My bookings + Quick actions
- [ ] 2. ReceptionistDashboardActivity: Today's check-in/out list + Pending tasks
- [ ] 3. ManagerDashboardActivity: KPI cards + Line chart revenue + Quick stats
- [ ] 4. RevenueReportActivity: Filter by date + Bar chart + Export CSV
- [ ] 5. OccupancyReportActivity: Pie chart + Percentage + Table by room type
- [ ] 6. FeedbackListActivity: RecyclerView + Sort by rating + Average stars
- [ ] 7. FeedbackFormActivity: Multi RatingBars + Comment + Upload photos

**Navigation Flow:**
```
Login → Dashboard (theo role)

GuestDashboard → BookingList → FeedbackForm

ReceptionistDashboard → CheckIn/Out → InventoryUsage

ManagerDashboard → Reports (Revenue/Occupancy) → Export
               → RoomManagement → InventoryManagement
```

**Thư viện cần thêm cho Charts:**
```gradle
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

---

## 📊 TỔNG KẾT PHÂN CÔNG

| Người | Chức năng | Số màn | Độ khó | Database chính |
|-------|-----------|--------|--------|----------------|
| 1 | Authentication & Profile | 7 | ⭐⭐ | User |
| 2 | Room Management | 7 | ⭐⭐⭐ | Room |
| 3 | Booking Management | 7 | ⭐⭐⭐ | Booking |
| 4 | Payment & Inventory | 7 | ⭐⭐⭐⭐ | Payment + Inventory |
| 5 | Dashboard & Reports | 7 | ⭐⭐⭐⭐ | All entities |
| **TỔNG** | | **35 màn** | | **7 entities** |

---

## 📐 QUY TẮC CODE

### ✅ BẮT BUỘC

1. **Sử dụng Resources:**
   - ❌ KHÔNG hard-code màu sắc, kích thước, text
   - ✅ Luôn dùng `@color/`, `@dimen/`, `@string/`

2. **Đặt tên ID rõ ràng:**
   ```xml
   <!-- ✅ ĐÚNG -->
   <Button android:id="@+id/btnLogin" />
   <TextView android:id="@+id/tvRoomNumber" />
   <EditText android:id="@+id/etEmail" />
   <RecyclerView android:id="@+id/rvRoomList" />
   
   <!-- ❌ SAI -->
   <Button android:id="@+id/button1" />
   <TextView android:id="@+id/text" />
   ```

3. **Prefix cho ID:**
   - `btn` - Button
   - `tv` - TextView
   - `et` - EditText
   - `rv` - RecyclerView
   - `iv` - ImageView
   - `ll` - LinearLayout
   - `cv` - CardView

4. **Comment code:**
   ```java
   // ✅ ĐÚNG - Comment bằng tiếng Việt
   // Lấy danh sách phòng trống
   List<Room> availableRooms = roomRepository.getRoomsByStatusSync("AVAILABLE");
   ```

5. **Repository Pattern:**
   - Luôn sử dụng Repository
   - KHÔNG truy cập DAO trực tiếp
   ```java
   // ✅ ĐÚNG
   roomRepository.insert(room);
   
   // ❌ SAI
   appDatabase.roomDao().insert(room);
   ```

6. **Background Thread:**
   ```java
   // ✅ ĐÚNG
   AppDatabase.databaseWriteExecutor.execute(() -> {
       // Database operations
   });
   
   // ❌ SAI - Database trên main thread sẽ crash
   room = roomDao.getRoomByIdSync(1);
   ```

7. **Format tiền tệ:**
   ```java
   // ✅ ĐÚNG
   String formattedPrice = DatabaseHelper.formatCurrency(800000);
   // Kết quả: "800,000 VND"
   ```

### 🎨 UI Best Practices

1. **Sử dụng ConstraintLayout cho layout phức tạp**
2. **Sử dụng RecyclerView thay vì ListView**
3. **Thêm ripple effect cho items có thể click:**
   ```xml
   android:foreground="@drawable/bg_ripple"
   android:clickable="true"
   android:focusable="true"
   ```
4. **Luôn có contentDescription cho ImageView**
5. **Sử dụng ScrollView nếu nội dung có thể dài**

---

## 📝 VÍ DỤ MINH HỌA

### Ví Dụ 1: RecyclerView Adapter cho Room List

```java
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    
    private List<Room> roomList;
    private Context context;
    private OnRoomClickListener listener;
    
    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }
    
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_room_card, parent, false);
        return new RoomViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        
        // Set room number
        holder.tvRoomNumber.setText(
            context.getString(R.string.room_number) + " " + room.getRoomNumber()
        );
        
        // Set room type
        holder.tvRoomType.setText(getRoomTypeString(room.getRoomType()));
        
        // Set price
        holder.tvPrice.setText(
            DatabaseHelper.formatCurrency(room.getPricePerNight()) + "/đêm"
        );
        
        // Set max guests
        holder.tvMaxGuests.setText(
            "Tối đa: " + room.getMaxGuests() + " khách"
        );
        
        // Set status with color
        setRoomStatus(holder.tvRoomStatus, room.getStatus());
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRoomClick(room);
            }
        });
    }
    
    private void setRoomStatus(TextView textView, String status) {
        String statusText;
        int backgroundRes;
        
        switch (status) {
            case Room.RoomStatus.AVAILABLE:
                statusText = context.getString(R.string.room_available);
                backgroundRes = R.drawable.bg_badge_success;
                break;
            case Room.RoomStatus.OCCUPIED:
                statusText = context.getString(R.string.room_occupied);
                backgroundRes = R.drawable.bg_badge_error;
                break;
            case Room.RoomStatus.RESERVED:
                statusText = context.getString(R.string.room_reserved);
                backgroundRes = R.drawable.bg_badge_warning;
                break;
            default:
                statusText = context.getString(R.string.room_maintenance);
                backgroundRes = R.drawable.bg_badge;
        }
        
        textView.setText(statusText);
        textView.setBackgroundResource(backgroundRes);
    }
    
    private String getRoomTypeString(String roomType) {
        switch (roomType) {
            case Room.RoomType.SINGLE:
                return context.getString(R.string.room_type_single);
            case Room.RoomType.DOUBLE:
                return context.getString(R.string.room_type_double);
            case Room.RoomType.SUITE:
                return context.getString(R.string.room_type_suite);
            case Room.RoomType.DELUXE:
                return context.getString(R.string.room_type_deluxe);
            default:
                return roomType;
        }
    }
    
    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvRoomType, tvPrice, tvMaxGuests, tvRoomStatus;
        ImageView ivRoomImage;
        
        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvMaxGuests = itemView.findViewById(R.id.tvMaxGuests);
            tvRoomStatus = itemView.findViewById(R.id.tvRoomStatus);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
        }
    }
}
```

### Ví Dụ 2: Activity với LiveData

```java
public class RoomListActivity extends AppCompatActivity {
    
    private RecyclerView rvRoomList;
    private RoomAdapter roomAdapter;
    private RoomRepository roomRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        
        // Initialize views
        initViews();
        
        // Initialize repository
        roomRepository = new RoomRepository(getApplication());
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Load data
        loadRooms();
    }
    
    private void initViews() {
        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.rooms);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Setup RecyclerView
        rvRoomList = findViewById(R.id.rvRoomList);
    }
    
    private void setupRecyclerView() {
        roomAdapter = new RoomAdapter(this);
        roomAdapter.setOnRoomClickListener(room -> {
            // Navigate to room detail
            Intent intent = new Intent(this, RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", room.getRoomId());
            startActivity(intent);
        });
        
        rvRoomList.setLayoutManager(new LinearLayoutManager(this));
        rvRoomList.setAdapter(roomAdapter);
    }
    
    private void loadRooms() {
        // Sử dụng LiveData để tự động cập nhật khi data thay đổi
        roomRepository.getAllActiveRooms().observe(this, rooms -> {
            if (rooms != null && !rooms.isEmpty()) {
                roomAdapter.setRoomList(rooms);
            } else {
                // Show empty state
                showEmptyState();
            }
        });
    }
    
    private void showEmptyState() {
        // TODO: Show empty state UI
        Toast.makeText(this, "Không có phòng nào", Toast.LENGTH_SHORT).show();
    }
}
```

### Ví Dụ 3: Validation Input

```java
public class ValidationHelper {
    
    public static boolean validateEmail(EditText editText) {
        String email = editText.getText().toString().trim();
        
        if (email.isEmpty()) {
            editText.setError(editText.getContext().getString(R.string.field_required));
            return false;
        }
        
        if (!DatabaseHelper.Validator.isValidEmail(email)) {
            editText.setError(editText.getContext().getString(R.string.invalid_email));
            return false;
        }
        
        return true;
    }
    
    public static boolean validatePassword(EditText editText) {
        String password = editText.getText().toString();
        
        if (password.isEmpty()) {
            editText.setError(editText.getContext().getString(R.string.field_required));
            return false;
        }
        
        if (!DatabaseHelper.Validator.isValidPassword(password)) {
            editText.setError(editText.getContext().getString(R.string.password_too_short));
            return false;
        }
        
        return true;
    }
}
```

---

## 🚨 LƯU Ý QUAN TRỌNG

### 1. Trước Khi Code

- [ ] Đọc kỹ phần phân công
- [ ] Xem template layouts đã có sẵn
- [ ] Hiểu rõ Database entities và repositories
- [ ] Đọc `DATABASE_USAGE.md` để biết cách dùng database

### 2. Trong Quá Trình Code

- [ ] Luôn test trên thiết bị thật hoặc emulator
- [ ] Commit code thường xuyên
- [ ] Không sửa file của người khác trừ khi thống nhất
- [ ] Hỏi nhóm nếu gặp vấn đề

### 3. Sau Khi Hoàn Thành

- [ ] Test tất cả tính năng
- [ ] Check linter errors
- [ ] Viết comment cho code phức tạp
- [ ] Update checklist trong README này

---

## 📱 SCREENSHOTS MẪU

*(Mỗi người nên chụp màn hình demo tính năng của mình và thêm vào đây)*

### Login Screen
```
[TODO: Thêm screenshot]
```

### Room List
```
[TODO: Thêm screenshot]
```

### Booking Detail
```
[TODO: Thêm screenshot]
```

---

## 🆘 HỖ TRỢ

### Gặp Vấn Đề?

1. **Lỗi database:** Đọc `DATABASE_USAGE.md`
2. **Lỗi UI:** Kiểm tra lại colors, dimens, styles
3. **Lỗi build:** Sync Gradle lại
4. **Crash app:** Check Logcat để xem lỗi

### Liên Hệ

- **Team Leader:** [Tên người phụ trách]
- **Group Chat:** [Link group]

---

## ✅ CHECKLIST TỔNG QUAN

### Resources (Hoàn thành)
- [x] colors.xml
- [x] dimens.xml
- [x] strings.xml
- [x] styles.xml
- [x] themes.xml
- [x] drawable backgrounds
- [x] layout templates

### Tính Năng (Cần làm)
- [ ] Authentication (Người 1)
- [ ] Room Management (Người 2)
- [ ] Booking (Người 3)
- [ ] Payment & Inventory (Người 4)
- [ ] Dashboard & Reports (Người 5)

---

**Cập nhật lần cuối:** $(date +"%d/%m/%Y")  
**Version:** 1.0  
**Dự án:** Hotel Management System

