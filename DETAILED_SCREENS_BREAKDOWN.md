# 📱 CHI TIẾT 35 MÀN HÌNH - HOTEL MANAGEMENT SYSTEM

## 📑 MỤC LỤC
- [NGƯỜI 1: Authentication & User Profile (7 màn)](#người-1-authentication--user-profile)
- [NGƯỜI 2: Room Management (7 màn)](#người-2-room-management)
- [NGƯỜI 3: Booking Management (7 màn)](#người-3-booking-management)
- [NGƯỜI 4: Payment & Inventory (7 màn)](#người-4-payment--inventory)
- [NGƯỜI 5: Dashboard, Reports & Feedback (7 màn)](#người-5-dashboard-reports--feedback)

---

## 👤 NGƯỜI 1: AUTHENTICATION & USER PROFILE

### 1. SplashActivity
**Mục đích:** Màn hình khởi động đầu tiên khi mở app

**UI Elements:**
- Logo hotel (ImageView - center)
- App name "Hotel Manager" (TextView - dưới logo)
- Loading indicator (ProgressBar)
- Background màu primary

**Logic:**
```java
- Delay 2-3 giây
- Check xem user đã đăng nhập chưa (SharedPreferences)
- Nếu đã login → chuyển đến Dashboard (theo role)
- Nếu chưa login → chuyển đến WelcomeActivity
```

**Layout:** `activity_splash.xml`

**Độ khó:** ⭐ (Dễ)

---

### 2. WelcomeActivity
**Mục đích:** Giới thiệu app và cho user chọn Login hoặc Register

**UI Elements:**
- ViewPager2 với 3 slides giới thiệu tính năng
- Dots indicator
- Button "Đăng nhập" (Primary)
- Button "Đăng ký" (Outlined)
- Skip button (top-right)

**Logic:**
```java
- ViewPager2 swipe giữa các slides
- Skip → LoginActivity
- Button Đăng nhập → LoginActivity
- Button Đăng ký → RegisterActivity
```

**Layout:** `activity_welcome.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 3. LoginActivity ✅
**Mục đích:** Đăng nhập vào hệ thống

**UI Elements:**
- Logo (ImageView)
- Email input (TextInputEditText)
- Password input (TextInputEditText with password toggle)
- Checkbox "Ghi nhớ đăng nhập"
- Button "Đăng nhập" (Primary)
- TextView "Quên mật khẩu?" (clickable)
- TextView "Chưa có tài khoản? Đăng ký" (clickable)

**Logic:**
```java
- Validate email format
- Validate password (minimum 6 characters)
- Call userRepository.login(email, hashedPassword)
- Lưu session vào SharedPreferences nếu Remember Me checked
- Navigate đến Dashboard theo role:
  - GUEST → GuestDashboardActivity
  - RECEPTIONIST → ReceptionistDashboardActivity
  - MANAGER → ManagerDashboardActivity
- Show toast thông báo nếu login fail
```

**Template có sẵn:** `activity_login_template.xml` ✅

**Độ khó:** ⭐⭐ (Trung bình)

---

### 4. RegisterActivity
**Mục đích:** Đăng ký tài khoản mới (chỉ cho Guest)

**UI Elements:**
- Scroll View (vì form dài)
- Full Name (EditText)
- Email (EditText)
- Password (EditText)
- Confirm Password (EditText)
- Phone Number (EditText)
- Address (EditText)
- Upload ID Photo (Button + ImageView preview)
- Checkbox "Đồng ý điều khoản"
- Button "Đăng ký" (Primary)

**Logic:**
```java
- Validate all fields
- Check password match with confirm password
- Check email không trùng (userRepository.checkEmailExists())
- Upload ID photo (sử dụng image picker)
- Hash password
- Create User object với role = GUEST
- Call userRepository.registerUser(...)
- Show success dialog → Navigate to LoginActivity
```

**Layout:** `activity_register.xml`

**Độ khó:** ⭐⭐⭐ (Khó - nhiều validations)

---

### 5. ForgotPasswordActivity
**Mục đích:** Reset mật khẩu qua email

**UI Elements:**
- Icon email lớn (ImageView)
- Text hướng dẫn
- Email input (TextInputEditText)
- Button "Gửi link reset" (Primary)
- Button "Quay lại đăng nhập" (Text button)

**Logic:**
```java
- Validate email format
- Check email tồn tại trong database
- Gửi email với link reset password (integration với email service)
- Show success dialog
- Navigate back to LoginActivity
```

**Layout:** `activity_forgot_password.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 6. ProfileActivity
**Mục đích:** Xem và sửa thông tin cá nhân

**UI Elements:**
- Toolbar với title "Hồ sơ"
- Avatar image (circular - clickable để đổi ảnh)
- Email (TextView - không edit được)
- Full Name (EditText)
- Phone Number (EditText)
- Address (EditText)
- Role badge (TextView với background màu theo role)
- Button "Lưu thay đổi" (Primary)
- Button "Đổi mật khẩu" (Secondary)
- Button "Đăng xuất" (Outlined - red text)

**Logic:**
```java
- Load user info từ database theo userId đã login
- Enable/disable edit mode
- Update user info khi click "Lưu"
- Navigate to ChangePasswordActivity khi click "Đổi mật khẩu"
- Clear session và navigate to LoginActivity khi "Đăng xuất"
```

**Layout:** `activity_profile.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 7. ChangePasswordActivity
**Mục đích:** Đổi mật khẩu

**UI Elements:**
- Toolbar với title "Đổi mật khẩu"
- Old Password (TextInputEditText)
- New Password (TextInputEditText)
- Confirm New Password (TextInputEditText)
- Password strength indicator (ProgressBar + TextView)
- Button "Cập nhật mật khẩu" (Primary)

**Logic:**
```java
- Validate old password đúng không
- Validate new password (min 6 chars, có ít nhất 1 số, 1 chữ hoa)
- Check new password match với confirm
- Hash new password
- Update user password in database
- Show success toast
- Navigate back to ProfileActivity
```

**Layout:** `activity_change_password.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

## 🏠 NGƯỜI 2: ROOM MANAGEMENT

### 1. RoomListActivity ✅
**Mục đích:** Hiển thị danh sách tất cả phòng

**UI Elements:**
- Toolbar với title "Phòng"
- SearchView (tìm theo số phòng hoặc loại)
- Filter chips (All, Available, Occupied, Reserved, Maintenance)
- RecyclerView với `item_room_card.xml` ✅
- FloatingActionButton "+" (chỉ hiện cho Manager)
- SwipeRefreshLayout

**Logic:**
```java
- Load rooms từ roomRepository.getAllActiveRooms()
- Filter theo status khi click chips
- Search real-time theo room number/type
- Click room item → RoomDetailActivity
- Click FAB → RoomAddActivity (Manager only)
- Pull to refresh data
- Hiển thị badge màu cho từng status
```

**Template có sẵn:** `item_room_card.xml` ✅

**Độ khó:** ⭐⭐ (Trung bình)

---

### 2. RoomDetailActivity
**Mục đích:** Chi tiết đầy đủ về 1 phòng

**UI Elements:**
- Toolbar với menu options (Edit, Delete - Manager only)
- ViewPager2 hiển thị ảnh phòng (swipe)
- Dots indicator
- Room number & type (TextViews)
- Status badge (colored)
- Price per night (highlighted - large text)
- Max guests (với icon)
- Floor number
- Description (expandable TextView)
- Amenities (ChipGroup)
- Button "Xem lịch trống" (Secondary)
- Button "Đặt ngay" (Primary - chỉ khi available)

**Logic:**
```java
- Load room details theo roomId
- ViewPager2 cho gallery (click ảnh → RoomGalleryActivity)
- Click "Xem lịch trống" → RoomAvailabilityCalendarActivity
- Click "Đặt ngay" → BookingCreateActivity với roomId
- Menu Edit → RoomEditActivity (Manager)
- Menu Delete → Confirm dialog → Delete room (Manager)
```

**Layout:** `activity_room_detail.xml`

**Độ khó:** ⭐⭐⭐ (Khó - nhiều components)

---

### 3. RoomSearchActivity
**Mục đích:** Tìm kiếm phòng với nhiều filter

**UI Elements:**
- Toolbar với title "Tìm phòng"
- Check-in DatePicker
- Check-out DatePicker
- Number of guests (NumberPicker hoặc ±buttons)
- Room type dropdown (Spinner)
- Price range slider (RangeSlider)
- Button "Tìm kiếm" (Primary)
- RecyclerView hiển thị kết quả

**Logic:**
```java
- Set default dates (today và tomorrow)
- Validate check-out > check-in
- Filter rooms:
  - Status = AVAILABLE
  - maxGuests >= input
  - Price in range
  - Type match (nếu chọn)
  - Không có booking conflict trong khoảng thời gian
- Display results trong RecyclerView
- Click room → RoomDetailActivity
```

**Layout:** `activity_room_search.xml`

**Độ khó:** ⭐⭐⭐ (Khó - complex logic)

---

### 4. RoomAddActivity
**Mục đích:** Thêm phòng mới (Manager only)

**UI Elements:**
- Toolbar "Thêm phòng mới"
- ScrollView
- Room Number (EditText)
- Room Type (Spinner)
- Price per Night (EditText - number)
- Max Guests (NumberPicker)
- Floor Number (NumberPicker)
- Description (EditText - multiline)
- Upload images (RecyclerView horizontal - multiple images)
- Amenities (CheckBoxes: WiFi, TV, AC, MiniBar, etc.)
- Status (Spinner - default AVAILABLE)
- Button "Thêm phòng" (Primary)

**Logic:**
```java
- Validate all required fields
- Check room number không trùng
- Upload images to storage
- Create Room object
- Call roomRepository.insert(room)
- Show success message
- Navigate back to RoomListActivity
```

**Layout:** `activity_room_add.xml`

**Độ khó:** ⭐⭐⭐⭐ (Rất khó - nhiều fields, upload ảnh)

---

### 5. RoomEditActivity
**Mục đích:** Sửa thông tin phòng (Manager only)

**UI Elements:**
- Giống RoomAddActivity nhưng pre-filled data
- Toolbar "Sửa phòng"
- Button "Cập nhật" (Primary)

**Logic:**
```java
- Load room data theo roomId
- Pre-fill tất cả fields
- Allow edit
- Validate changes
- Update room trong database
- Update lastUpdatedAt
- Navigate back to RoomDetailActivity
```

**Layout:** `activity_room_edit.xml` (có thể reuse add layout)

**Độ khó:** ⭐⭐⭐ (Khó)

---

### 6. RoomGalleryActivity
**Mục đích:** Xem ảnh phòng full screen

**UI Elements:**
- ViewPager2 full screen
- Image counter (1/5) - TextView overlay
- Zoom-able images (PhotoView library)
- Close button (X) top-right
- Share button (top-right)

**Logic:**
```java
- Display images in ViewPager2
- Enable pinch-to-zoom
- Swipe left/right giữa ảnh
- Click close → finish activity
- Click share → Share image
```

**Thư viện cần:** 
```gradle
implementation 'com.github.chrisbanes:PhotoView:2.3.0'
```

**Layout:** `activity_room_gallery.xml`

**Độ khó:** ⭐⭐ (Trung bình - dùng library)

---

### 7. RoomAvailabilityCalendarActivity
**Mục đích:** Xem lịch trống/đã đặt của phòng

**UI Elements:**
- Toolbar "Lịch trống - Phòng [number]"
- CalendarView
- Legend:
  - Xanh lá: Available
  - Đỏ: Booked
  - Xám: Past dates
- RecyclerView hiển thị list bookings của room

**Logic:**
```java
- Load bookings cho room này
- Highlight dates đã booked trên CalendarView
- Disable past dates
- Show booking details khi click vào booked date
- RecyclerView hiển thị upcoming bookings
```

**Thư viện Calendar:**
```gradle
implementation 'com.github.prolificinteractive:material-calendarview:2.0.1'
```

**Layout:** `activity_room_availability_calendar.xml`

**Độ khó:** ⭐⭐⭐ (Khó - calendar logic)

---

## 📅 NGƯỜI 3: BOOKING MANAGEMENT

### 1. BookingListActivity ✅
**Mục đích:** Danh sách booking (theo role)

**UI Elements:**
- Toolbar "Đặt phòng"
- Tab Layout:
  - Guest: My Bookings
  - Receptionist: All Bookings (Today, Upcoming, All)
  - Manager: All Bookings với filters
- RecyclerView với `item_booking_card.xml` ✅
- Filter by status (Chips)
- Search by booking code
- FloatingActionButton "+" (Guest tạo booking mới)

**Logic:**
```java
- Load bookings theo role:
  - Guest: bookingRepository.getBookingsByGuest(userId)
  - Receptionist/Manager: bookingRepository.getAllBookings()
- Filter real-time by status
- Search by booking code
- Click booking → BookingDetailActivity
- Swipe actions: Cancel, Edit (tùy status)
- Click FAB → BookingCreateActivity (Guest)
```

**Template có sẵn:** `item_booking_card.xml` ✅

**Độ khó:** ⭐⭐⭐ (Khó - nhiều roles)

---

### 2. BookingCreateActivity
**Mục đích:** Tạo booking mới

**UI Elements:**
- Toolbar "Đặt phòng mới"
- ScrollView
- Room info card (nếu từ RoomDetail) hoặc Button "Chọn phòng"
- Check-in date (DatePicker dialog)
- Check-out date (DatePicker dialog)
- Number of guests (NumberPicker)
- Special requests (EditText - multiline, optional)
- Price breakdown:
  - Price per night
  - Number of nights
  - Total amount (highlighted)
- Button "Tiếp tục thanh toán" (Primary)

**Logic:**
```java
- If from RoomDetail: pre-select room
- Else: show RoomSearchActivity để chọn
- Validate dates (check-out > check-in, not past)
- Check room availability
- Calculate total amount:
  - nights = checkOut - checkIn
  - total = pricePerNight * nights
- Create Booking object với status = PENDING
- Insert booking vào database
- Navigate to PaymentActivity with bookingId
```

**Layout:** `activity_booking_create.xml`

**Độ khó:** ⭐⭐⭐ (Khó - date logic, calculations)

---

### 3. BookingDetailActivity
**Mục đích:** Chi tiết booking + QR code

**UI Elements:**
- Toolbar "Chi tiết đặt phòng"
- QR Code (ImageView - chứa booking code)
- Status badge (lớn, có màu)
- Booking code (copyable)
- Guest info
- Room info (clickable → RoomDetail)
- Check-in/out dates
- Number of guests
- Special requests (nếu có)
- Total amount
- Payment status
- Timeline (Booked → Confirmed → Checked-in → Checked-out)
- Buttons (theo status & role):
  - Edit (Guest, status = PENDING)
  - Cancel (Guest, status != CHECKED_OUT)
  - Check-in (Receptionist, status = CONFIRMED)
  - Check-out (Receptionist, status = CHECKED_IN)
  - Submit Feedback (Guest, status = CHECKED_OUT)

**Logic:**
```java
- Load booking details
- Generate QR code from booking code
- Display timeline với màu sắc theo progress
- Click room → RoomDetailActivity
- Handle buttons theo role và status
- Click copy booking code
- Click share QR code
```

**Thư viện QR Code:**
```gradle
implementation 'com.google.zxing:core:3.5.1'
implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
```

**Layout:** `activity_booking_detail.xml`

**Độ khó:** ⭐⭐⭐ (Khó - QR code, timeline)

---

### 4. BookingEditActivity
**Mục đích:** Sửa booking (chỉ khi status = PENDING)

**UI Elements:**
- Giống BookingCreateActivity
- Pre-filled data
- Warning "Chỉnh sửa có thể thay đổi giá"
- Button "Cập nhật đặt phòng"

**Logic:**
```java
- Load booking data
- Allow edit dates và special requests only
- Check availability cho dates mới
- Recalculate total amount
- Update booking
- Show confirmation
```

**Layout:** `activity_booking_edit.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 5. CheckInActivity
**Mục đích:** Check-in khách (Receptionist)

**UI Elements:**
- Toolbar "Check-in"
- Scan QR button hoặc Input booking code
- Guest info display (name, phone, ID photo)
- Booking details
- Room assignment (có thể đổi phòng nếu cần)
- ID verification checkbox
- Deposit amount (optional)
- Notes (EditText)
- Button "Xác nhận check-in" (Primary)

**Logic:**
```java
- Scan QR code hoặc manual input booking code
- Load booking và guest info
- Verify booking status = CONFIRMED
- Check ID photo
- Cho phép đổi room nếu available
- Update booking:
  - status = CHECKED_IN
  - actualCheckInTime = now
  - checkedInByUserId = receptionistId
- Update room status = OCCUPIED
- Print/show room key information
- Navigate to CheckInSuccessActivity
```

**Thư viện Scan QR:**
```gradle
// (Đã có trong BookingDetail)
```

**Layout:** `activity_check_in.xml`

**Độ khó:** ⭐⭐⭐⭐ (Rất khó - QR scan, nhiều logic)

---

### 6. CheckOutActivity
**Mục đích:** Check-out khách (Receptionist)

**UI Elements:**
- Toolbar "Check-out"
- Guest & room info
- Booking summary
- Check damages/mini-bar (checkbox list)
- Additional charges:
  - Late checkout fee
  - Damages
  - Mini-bar
  - Services
- Total to pay
- Payment method
- Notes
- Button "Xác nhận check-out" (Primary)

**Logic:**
```java
- Load booking theo roomId hoặc booking code
- Calculate additional charges
- Total amount = original + additional
- Update booking:
  - status = CHECKED_OUT
  - actualCheckOutTime = now
  - checkedOutByUserId = receptionistId
- Update room status = AVAILABLE
- Create Payment record nếu có additional charges
- Send checkout confirmation email/SMS
- Navigate to success screen
```

**Layout:** `activity_check_out.xml`

**Độ khó:** ⭐⭐⭐⭐ (Rất khó - tính toán, nhiều cases)

---

### 7. BookingHistoryActivity
**Mục đích:** Lịch sử booking của guest

**UI Elements:**
- Toolbar "Lịch sử đặt phòng"
- Filter tabs:
  - All
  - Upcoming (CONFIRMED, PENDING)
  - Past (CHECKED_OUT)
  - Cancelled
- RecyclerView
- Empty state khi không có booking

**Logic:**
```java
- Load bookings cho guest đã login
- Filter theo tab selected
- Sort by booking date DESC
- Click booking → BookingDetailActivity
- Show statistics (total bookings, total spent)
```

**Layout:** `activity_booking_history.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

## 💳 NGƯỜI 4: PAYMENT & INVENTORY

### 1. PaymentActivity
**Mục đích:** Thanh toán qua VNPAY

**UI Elements:**
- Toolbar "Thanh toán"
- Booking summary card
- Amount to pay (highlighted)
- Payment method options:
  - VNPAY
  - Cash (at hotel)
  - Card
- WebView (cho VNPAY)
- Loading progress
- Terms & conditions checkbox

**Logic:**
```java
- Load booking info
- Display amount
- When select VNPAY:
  - Generate VNPAY payment URL
  - Load URL in WebView
  - Handle callback from VNPAY
  - Parse response (success/fail)
  - Update payment status
  - Navigate to PaymentSuccessActivity or show error
- When select Cash/Card:
  - Create Payment with PENDING status
  - Note "Pay at hotel"
```

**Thư viện:**
```gradle
implementation 'com.squareup.okhttp3:okhttp:4.11.0'
```

**Layout:** `activity_payment.xml`

**Độ khó:** ⭐⭐⭐⭐⭐ (Rất rất khó - VNPAY integration)

---

### 2. PaymentSuccessActivity
**Mục đích:** Xác nhận thanh toán thành công

**UI Elements:**
- Success icon (animated checkmark)
- "Thanh toán thành công!"
- Transaction details:
  - Transaction ID
  - Amount paid
  - Date & time
  - Payment method
- Booking code (large, copyable)
- QR code
- Button "Xem chi tiết booking"
- Button "Tải hóa đơn (PDF)"
- Button "Chia sẻ"
- Button "Về trang chủ"

**Logic:**
```java
- Display payment và booking info
- Generate QR code
- Download PDF receipt
- Share receipt via apps
- Navigate to BookingDetail hoặc Dashboard
```

**Thư viện PDF:**
```gradle
implementation 'com.itextpdf:itext7-core:7.2.5'
```

**Layout:** `activity_payment_success.xml`

**Độ khó:** ⭐⭐⭐ (Khó - PDF generation)

---

### 3. PaymentHistoryActivity
**Mục đích:** Lịch sử thanh toán

**UI Elements:**
- Toolbar "Lịch sử thanh toán"
- Filter by status:
  - All
  - Success
  - Pending
  - Failed
  - Refunded
- Filter by date range
- RecyclerView with payment items:
  - Transaction ID
  - Amount
  - Status badge
  - Date
  - Booking code
- Total statistics (top)

**Logic:**
```java
- Load payments từ paymentRepository
- Filter by status
- Filter by date range
- Click payment → PaymentDetailActivity
- Show refund button nếu eligible
- Calculate totals
```

**Layout:** `activity_payment_history.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 4. InventoryListActivity
**Mục đích:** Quản lý kho (Manager/Receptionist)

**UI Elements:**
- Toolbar "Quản lý kho"
- Search bar
- Filter by category (Chips)
- Badge "Low Stock: X items" (red)
- RecyclerView:
  - Item name
  - Item code
  - Current quantity / Minimum quantity
  - Badge (red nếu low stock)
  - Category
- FAB "+" (Manager only)
- Sort options (Name, Quantity, Category)

**Logic:**
```java
- Load inventory từ inventoryRepository.getAllActiveInventory()
- Highlight low stock items (currentQty <= minQty)
- Search real-time
- Filter by category
- Sort
- Click item → InventoryDetailActivity
- Click FAB → InventoryAddEditActivity
```

**Layout:** `activity_inventory_list.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 5. InventoryDetailActivity
**Mục đích:** Chi tiết item kho

**UI Elements:**
- Toolbar với menu (Edit, Delete - Manager)
- Item image/icon
- Item name & code
- Category badge
- Current quantity (large, colored)
- Minimum quantity
- Unit
- Unit price
- Supplier info
- Chart: Usage history (last 30 days)
- Button "Log sử dụng" (Receptionist)
- Button "Restock" (Manager)
- RecyclerView: Recent usage logs

**Logic:**
```java
- Load inventory detail
- Load usage history
- Display chart (Line chart)
- Click "Log sử dụng" → InventoryUsageLogActivity
- Click "Restock" → Dialog nhập số lượng → Update stock
- Menu Edit → InventoryAddEditActivity
- Menu Delete → Confirm → Delete
```

**Thư viện Chart:**
```gradle
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

**Layout:** `activity_inventory_detail.xml`

**Độ khó:** ⭐⭐⭐ (Khó - charts)

---

### 6. InventoryAddEditActivity
**Mục đích:** Thêm/sửa item kho

**UI Elements:**
- Toolbar "Thêm/Sửa vật phẩm"
- ScrollView
- Item name (EditText)
- Item code (EditText hoặc Scan Barcode)
- Category (Spinner)
- Description (EditText)
- Current quantity (NumberPicker)
- Minimum quantity (NumberPicker)
- Unit (EditText)
- Unit price (EditText - number)
- Supplier name (EditText)
- Supplier contact (EditText)
- Upload image (optional)
- Button "Lưu"

**Logic:**
```java
- Validate fields
- Check item code không trùng (khi add)
- Scan barcode nếu có
- Upload image
- Create/Update Inventory object
- Save to database
```

**Thư viện Barcode:**
```gradle
implementation 'com.google.android.gms:play-services-vision:20.1.3'
```

**Layout:** `activity_inventory_add_edit.xml`

**Độ khó:** ⭐⭐⭐⭐ (Rất khó - barcode scan)

---

### 7. InventoryUsageLogActivity
**Mục đích:** Log việc sử dụng kho (Receptionist)

**UI Elements:**
- Toolbar "Log sử dụng"
- Select room (Spinner hoặc search)
- Select items (RecyclerView với checkbox):
  - Item name
  - Available quantity
  - Input quantity used (NumberPicker)
- Usage type (Spinner):
  - Room Service
  - Cleaning
  - Maintenance
  - Restocking
  - Wastage
- Notes (EditText)
- Button "Xác nhận" (Primary)

**Logic:**
```java
- Load inventory items
- Select room (nếu room service)
- Select multiple items
- Input quantity cho mỗi item
- Validate quantity <= available
- Create InventoryUsage records
- Auto reduce inventory quantity
- Show alert nếu item trở thành low stock
- Clear form and show success
```

**Layout:** `activity_inventory_usage_log.xml`

**Độ khó:** ⭐⭐⭐⭐ (Rất khó - multiple selections, validations)

---

## 📊 NGƯỜI 5: DASHBOARD, REPORTS & FEEDBACK

### 1. GuestDashboardActivity
**Mục đích:** Dashboard cho khách

**UI Elements:**
- Toolbar "Chào [Guest Name]"
- Profile image (top, clickable)
- Quick stats cards:
  - Upcoming bookings
  - Past bookings
  - Total spent
- "My Bookings" section (horizontal RecyclerView)
- Quick actions (Grid):
  - Tìm phòng
  - Đặt phòng
  - Lịch sử booking
  - Hồ sơ
- Promotions banner (ViewPager)
- Recent feedback

**Logic:**
```java
- Load guest data
- Get booking stats
- Display upcoming bookings (horizontally)
- Quick action buttons → navigate to respective activities
- Click profile → ProfileActivity
```

**Layout:** `activity_guest_dashboard.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 2. ReceptionistDashboardActivity
**Mục đích:** Dashboard cho lễ tân

**UI Elements:**
- Toolbar "Lễ tân - [Name]"
- Today's summary:
  - Check-ins today
  - Check-outs today
  - Occupied rooms
  - Available rooms
- Pending tasks (RecyclerView):
  - Upcoming check-ins
  - Upcoming check-outs
  - Pending bookings to confirm
- Quick actions:
  - Check-in
  - Check-out
  - All bookings
  - Inventory usage log
- Room status overview (Grid with colors)

**Logic:**
```java
- Load today's check-ins/outs
- Get room statistics
- Display pending tasks
- Click task → navigate to CheckIn/CheckOut
- Room grid shows real-time status
```

**Layout:** `activity_receptionist_dashboard.xml`

**Độ khó:** ⭐⭐⭐ (Khó - nhiều data sources)

---

### 3. ManagerDashboardActivity
**Mục đích:** Dashboard cho quản lý với KPIs

**UI Elements:**
- Toolbar "Quản lý - [Name]"
- KPI Cards (4 cards):
  - Total Revenue (this month)
  - Occupancy Rate (%)
  - Total Bookings
  - Active Rooms
- Line chart: Revenue trend (last 6 months)
- Pie chart: Room type distribution
- Quick stats:
  - Low stock items (with alert)
  - Pending bookings
  - Average rating
- Quick actions:
  - View Reports
  - Manage Rooms
  - Manage Inventory
  - Manage Staff (future)
- Recent bookings (RecyclerView)

**Logic:**
```java
- Calculate KPIs from database
- Generate revenue chart data
- Generate pie chart for room types
- Get low stock count
- Get average feedback rating
- Navigate to detailed reports
```

**Thư viện Charts:**
```gradle
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

**Layout:** `activity_manager_dashboard.xml`

**Độ khó:** ⭐⭐⭐⭐⭐ (Rất rất khó - nhiều calculations, charts)

---

### 4. RevenueReportActivity
**Mục đích:** Báo cáo doanh thu chi tiết

**UI Elements:**
- Toolbar "Báo cáo doanh thu"
- Date range picker (Start - End)
- Filter options:
  - By room type
  - By payment method
  - By status
- Bar chart: Revenue by month
- Line chart: Daily revenue trend
- Statistics table:
  - Total revenue
  - Total bookings
  - Total refunds
  - Net revenue
  - Average booking value
- RecyclerView: List of payments
- Export buttons:
  - Export PDF
  - Export Excel/CSV
  - Share

**Logic:**
```java
- Filter by date range
- Get payment data
- Calculate statistics
- Generate charts
- Export to PDF/CSV
- Share report
```

**Thư viện Export:**
```gradle
implementation 'org.apache.poi:poi:5.2.3' // Excel
implementation 'com.itextpdf:itext7-core:7.2.5' // PDF
```

**Layout:** `activity_revenue_report.xml`

**Độ khó:** ⭐⭐⭐⭐⭐ (Rất rất khó - complex calculations, export)

---

### 5. OccupancyReportActivity
**Mục đích:** Báo cáo tỷ lệ lấp đầy

**UI Elements:**
- Toolbar "Báo cáo lấp đầy"
- Date range picker
- Overall occupancy rate (large % display)
- Pie chart: Occupied vs Available
- Bar chart: Occupancy by room type
- Table: Room type breakdown
  - Room type
  - Total rooms
  - Occupied
  - Available
  - Occupancy %
- Line chart: Occupancy trend over time
- Export button

**Logic:**
```java
- Calculate occupancy rate = (occupied / total) * 100
- Group by room type
- Generate charts
- Export report
```

**Layout:** `activity_occupancy_report.xml`

**Độ khó:** ⭐⭐⭐⭐ (Rất khó - calculations, charts)

---

### 6. FeedbackListActivity
**Mục đích:** Danh sách đánh giá của khách

**UI Elements:**
- Toolbar "Đánh giá khách hàng"
- Overall rating (large stars + number)
- Rating breakdown:
  - 5 stars: X reviews
  - 4 stars: X reviews
  - etc.
- Filter:
  - All ratings
  - 5 stars only
  - 4+ stars
  - Sort by date/rating
- RecyclerView with feedback items:
  - Guest name (or Anonymous)
  - Rating stars
  - Comment
  - Date
  - Room booked
- Empty state

**Logic:**
```java
- Load all feedbacks
- Calculate average rating
- Count ratings by stars
- Filter by rating
- Sort
- Click feedback → show full detail dialog
```

**Layout:** `activity_feedback_list.xml`

**Độ khó:** ⭐⭐ (Trung bình)

---

### 7. FeedbackFormActivity
**Mục đích:** Guest gửi đánh giá sau checkout

**UI Elements:**
- Toolbar "Đánh giá của bạn"
- Booking info card (read-only)
- Overall rating (RatingBar - large)
- Detailed ratings:
  - Cleanliness (RatingBar)
  - Service (RatingBar)
  - Amenities (RatingBar)
  - Value for Money (RatingBar)
- Comment (EditText - multiline)
- Upload photos (optional - max 3)
- Checkbox "Đánh giá ẩn danh"
- Button "Gửi đánh giá"

**Logic:**
```java
- Load booking info
- Validate: overall rating required
- Upload photos nếu có
- Create Feedback object
- Save to database
- Show thank you dialog
- Navigate to BookingHistory
```

**Layout:** `activity_feedback_form.xml`

**Độ khó:** ⭐⭐⭐ (Khó - multiple rating bars, upload)

---

## 📊 TỔNG HỢP

| # | Activity | Người | Độ khó | Có Template |
|---|----------|-------|--------|-------------|
| 1 | SplashActivity | 1 | ⭐ | ❌ |
| 2 | WelcomeActivity | 1 | ⭐⭐ | ❌ |
| 3 | LoginActivity | 1 | ⭐⭐ | ✅ |
| 4 | RegisterActivity | 1 | ⭐⭐⭐ | ❌ |
| 5 | ForgotPasswordActivity | 1 | ⭐⭐ | ❌ |
| 6 | ProfileActivity | 1 | ⭐⭐ | ❌ |
| 7 | ChangePasswordActivity | 1 | ⭐⭐ | ❌ |
| 8 | RoomListActivity | 2 | ⭐⭐ | ✅ |
| 9 | RoomDetailActivity | 2 | ⭐⭐⭐ | ❌ |
| 10 | RoomSearchActivity | 2 | ⭐⭐⭐ | ❌ |
| 11 | RoomAddActivity | 2 | ⭐⭐⭐⭐ | ❌ |
| 12 | RoomEditActivity | 2 | ⭐⭐⭐ | ❌ |
| 13 | RoomGalleryActivity | 2 | ⭐⭐ | ❌ |
| 14 | RoomAvailabilityCalendarActivity | 2 | ⭐⭐⭐ | ❌ |
| 15 | BookingListActivity | 3 | ⭐⭐⭐ | ✅ |
| 16 | BookingCreateActivity | 3 | ⭐⭐⭐ | ❌ |
| 17 | BookingDetailActivity | 3 | ⭐⭐⭐ | ❌ |
| 18 | BookingEditActivity | 3 | ⭐⭐ | ❌ |
| 19 | CheckInActivity | 3 | ⭐⭐⭐⭐ | ❌ |
| 20 | CheckOutActivity | 3 | ⭐⭐⭐⭐ | ❌ |
| 21 | BookingHistoryActivity | 3 | ⭐⭐ | ❌ |
| 22 | PaymentActivity | 4 | ⭐⭐⭐⭐⭐ | ❌ |
| 23 | PaymentSuccessActivity | 4 | ⭐⭐⭐ | ❌ |
| 24 | PaymentHistoryActivity | 4 | ⭐⭐ | ❌ |
| 25 | InventoryListActivity | 4 | ⭐⭐ | ❌ |
| 26 | InventoryDetailActivity | 4 | ⭐⭐⭐ | ❌ |
| 27 | InventoryAddEditActivity | 4 | ⭐⭐⭐⭐ | ❌ |
| 28 | InventoryUsageLogActivity | 4 | ⭐⭐⭐⭐ | ❌ |
| 29 | GuestDashboardActivity | 5 | ⭐⭐ | ❌ |
| 30 | ReceptionistDashboardActivity | 5 | ⭐⭐⭐ | ❌ |
| 31 | ManagerDashboardActivity | 5 | ⭐⭐⭐⭐⭐ | ❌ |
| 32 | RevenueReportActivity | 5 | ⭐⭐⭐⭐⭐ | ❌ |
| 33 | OccupancyReportActivity | 5 | ⭐⭐⭐⭐ | ❌ |
| 34 | FeedbackListActivity | 5 | ⭐⭐ | ❌ |
| 35 | FeedbackFormActivity | 5 | ⭐⭐⭐ | ❌ |

**TỔNG:** 35 màn hình | 4 templates có sẵn ✅

---

## 🎯 ƯU TIÊN PHÁT TRIỂN

### Sprint 1 (Tuần 1-2): Core Features - 12 màn
1. SplashActivity
2. WelcomeActivity  
3. LoginActivity ✅
4. RoomListActivity ✅
5. RoomDetailActivity
6. BookingListActivity ✅
7. BookingCreateActivity
8. PaymentActivity
9. GuestDashboardActivity
10. ManagerDashboardActivity
11. InventoryListActivity
12. FeedbackListActivity

### Sprint 2 (Tuần 3-4): Extended Features - 15 màn
Phần còn lại của mỗi người

### Sprint 3 (Tuần 5): Polish & Integration
- Testing, bug fixes, UI improvements

---

**Tài liệu được tạo cho:** Hotel Management System  
**Tổng số màn hình:** 35  
**Số người:** 5  
**Trung bình:** 7 màn/người


