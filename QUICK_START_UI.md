# ⚡ HƯỚNG DẪN NHANH - GIAO DIỆN

## 🎯 3 QUY TẮC VÀNG

### 1️⃣ LUÔN DÙNG RESOURCES - KHÔNG HARD-CODE

```xml
<!-- ❌ SAI -->
<TextView android:textColor="#FF0000" android:text="Đăng nhập" />

<!-- ✅ ĐÚNG -->
<TextView android:textColor="@color/text_primary" android:text="@string/login" />
```

### 2️⃣ DÙNG ĐÚNG STYLE

```xml
<!-- Button chính -->
<Button style="@style/Button.Primary" android:text="@string/login" />

<!-- EditText -->
<EditText style="@style/EditText" android:hint="@string/email" />
```

### 3️⃣ ID RÕ RÀNG

```xml
<Button android:id="@+id/btnLogin" />        <!-- Button -->
<TextView android:id="@+id/tvRoomNumber" />  <!-- TextView -->
<EditText android:id="@+id/etEmail" />       <!-- EditText -->
```

---

## 📋 BẢNG TRA CỨU NHANH

### Màu Sắc Hay Dùng

| Mục đích | Resource |
|----------|----------|
| Màu chính | `@color/primary` |
| Màu nền | `@color/background` |
| Chữ đen | `@color/text_primary` |
| Chữ xám | `@color/text_secondary` |
| Thành công (xanh) | `@color/success` |
| Cảnh báo (cam) | `@color/warning` |
| Lỗi (đỏ) | `@color/error` |

### Kích Thước Hay Dùng

| Mục đích | Resource |
|----------|----------|
| Padding nhỏ | `@dimen/padding_small` (8dp) |
| Padding thường | `@dimen/padding_normal` (16dp) |
| Chữ nhỏ | `@dimen/text_size_small` (12sp) |
| Chữ thường | `@dimen/text_size_normal` (14sp) |
| Chữ lớn | `@dimen/text_size_large` (18sp) |
| Bo góc | `@dimen/corner_radius_normal` (8dp) |

### Text Hay Dùng

| Tiếng Việt | Resource |
|------------|----------|
| Đăng nhập | `@string/login` |
| Lưu | `@string/save` |
| Hủy | `@string/cancel` |
| Tìm kiếm | `@string/search` |
| Phòng | `@string/rooms` |
| Đặt phòng | `@string/bookings` |

---

## 🎨 TEMPLATES SẴN CÓ

### 1. Room Card (Card phòng)
```xml
<include layout="@layout/item_room_card" />
```

### 2. Booking Card (Card đặt phòng)
```xml
<include layout="@layout/item_booking_card" />
```

### 3. Toolbar
```xml
<include layout="@layout/toolbar_common" />
```

---

## 💻 CODE MẪU NHANH

### RecyclerView Adapter

```java
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_card, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.tvRoomNumber.setText(room.getRoomNumber());
        holder.tvPrice.setText(
            DatabaseHelper.formatCurrency(room.getPricePerNight())
        );
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvPrice;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
```

### Load Data với LiveData

```java
public class MyActivity extends AppCompatActivity {
    
    private RoomRepository roomRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        roomRepository = new RoomRepository(getApplication());
        
        // Tự động cập nhật khi data thay đổi
        roomRepository.getAllRooms().observe(this, rooms -> {
            // Update UI
            adapter.setRoomList(rooms);
        });
    }
}
```

### Hiển Thị Badge Trạng Thái

```java
private void setRoomStatus(TextView textView, String status) {
    int backgroundRes;
    String statusText;
    
    if (status.equals(Room.RoomStatus.AVAILABLE)) {
        backgroundRes = R.drawable.bg_badge_success;
        statusText = getString(R.string.room_available);
    } else if (status.equals(Room.RoomStatus.OCCUPIED)) {
        backgroundRes = R.drawable.bg_badge_error;
        statusText = getString(R.string.room_occupied);
    } else {
        backgroundRes = R.drawable.bg_badge_warning;
        statusText = getString(R.string.room_reserved);
    }
    
    textView.setText(statusText);
    textView.setBackgroundResource(backgroundRes);
    textView.setTextColor(getColor(R.color.text_white));
}
```

---

## ⚠️ LỖI THƯỜNG GẶP

### 1. Crash do Database trên Main Thread

```java
// ❌ SAI - Sẽ crash app
User user = userDao.getUserByIdSync(1);

// ✅ ĐÚNG - Dùng background thread
AppDatabase.databaseWriteExecutor.execute(() -> {
    User user = userDao.getUserByIdSync(1);
    runOnUiThread(() -> {
        // Update UI ở đây
    });
});
```

### 2. Hard-code String

```java
// ❌ SAI
textView.setText("Đăng nhập");

// ✅ ĐÚNG
textView.setText(R.string.login);
```

### 3. Quên Validation

```java
// ✅ ĐÚNG - Luôn validate input
if (!validateEmail(etEmail)) {
    return;
}
if (!validatePassword(etPassword)) {
    return;
}
// Proceed with login...
```

---

## 📁 FILES QUAN TRỌNG

1. **README_UI.md** - Hướng dẫn đầy đủ ⭐
2. **DATABASE_USAGE.md** - Hướng dẫn database ⭐
3. **colors.xml** - Tất cả màu sắc
4. **strings.xml** - Tất cả text
5. **styles.xml** - Tất cả styles

---

## 👥 PHÂN CÔNG

- **Người 1:** Authentication (Login, Register, Profile)
- **Người 2:** Room Management (Danh sách phòng, CRUD)
- **Người 3:** Booking (Đặt phòng, Check-in/out)
- **Người 4:** Payment & Inventory
- **Người 5:** Dashboard & Reports

*Chi tiết xem trong README_UI.md*

---

## 🆘 CẦN GIÚP?

1. Đọc **README_UI.md** (hướng dẫn chi tiết)
2. Xem **DATABASE_USAGE.md** (database)
3. Hỏi team leader
4. Hỏi group

---

**Chúc code vui vẻ! 🚀**


