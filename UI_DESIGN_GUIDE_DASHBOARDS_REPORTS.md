# 📊 UI DESIGN GUIDE - Dashboard & Reports Activities

## 🎯 MỤC TIÊU

Thiết kế UI cho 5 màn hình của Người 5 - Dashboard, Reports & Feedback:
1. ManagerDashboardActivity
2. RevenueReportActivity  
3. OccupancyReportActivity
4. FeedbackListActivity
5. FeedbackFormActivity

---

## 📐 NGUYÊN TẮC THIẾT KẾ CHUNG

### 1. **Layout Structure**
```
CoordinatorLayout
├── AppBarLayout (MaterialToolbar)
└── NestedScrollView
    └── LinearLayout (Vertical)
        ├── KPI/Summary Cards (Grid)
        ├── Charts Section
        ├── Data Table/List
        └── Quick Actions
```

### 2. **Color Coding**
- **Primary Info:** Blue (#1976D2)
- **Success:** Green (#4CAF50)
- **Warning:** Orange (#FF9800)
- **Error:** Red (#F44336)
- **Revenue:** Purple (#9C27B0)
- **Text:** #212121 (primary), #757575 (secondary)

### 3. **Card Elevation Hierarchy**
- KPI Cards: elevation_normal (4dp)
- Chart Cards: elevation_normal (4dp)
- Data Cards: elevation_small (2dp)
- Action Buttons: elevation_normal (4dp)

---

## 1. ManagerDashboardActivity

### 🎨 Layout Structure

```xml
CoordinatorLayout
├── AppBarLayout (MaterialToolbar: "Quản lý - [Name]" + Logout menu)
└── NestedScrollView
    └── LinearLayout (Vertical)
        ├── Welcome Section
        ├── KPI Cards (2x2 Grid)
        ├── Charts Section (2 rows)
        ├── Quick Stats (3x1)
        ├── Quick Actions (2x2 Grid)
        └── Recent Bookings (RecyclerView)
```

### 📊 UI Components

#### A. Welcome Section
```xml
MaterialCardView
├── LinearLayout (Horizontal)
    ├── ImageView (Manager icon)
    ├── LinearLayout (Vertical)
    │   ├── TextView: "Chào mừng, [Manager Name]!"
    │   └── TextView: Current date & time
    └── ImageView (Logout icon - optional)
```

#### B. KPI Cards (2x2 Grid)
**Card 1: Total Revenue (This Month)**
```xml
MaterialCardView (background: #9C27B0 - Purple)
├── LinearLayout (Vertical, center)
    ├── TextView: Large number (headline) - "500,000,000"
    ├── TextView: "VNĐ" (caption)
    └── TextView: "Doanh thu tháng này"
```

**Card 2: Occupancy Rate**
```xml
MaterialCardView (background: #2196F3 - Blue)
├── LinearLayout (Vertical, center)
    ├── TextView: Large number (headline) - "85"
    ├── TextView: "%" (caption)
    └── TextView: "Tỷ lệ lấp đầy"
```

**Card 3: Total Bookings**
```xml
MaterialCardView (background: #4CAF50 - Green)
├── LinearLayout (Vertical, center)
    ├── TextView: Large number (headline) - "124"
    └── TextView: "Tổng đặt phòng"
```

**Card 4: Active Rooms**
```xml
MaterialCardView (background: #FF9800 - Orange)
├── LinearLayout (Vertical, center)
    ├── TextView: Large number (headline) - "45/50"
    └── TextView: "Phòng đang hoạt động"
```

#### C. Charts Section

**Row 1: Revenue Trend (Line Chart)**
```xml
MaterialCardView
├── TextView: "Xu hướng doanh thu (6 tháng)"
└── LineChart (MPAndroidChart library)
    - X-axis: Months
    - Y-axis: Revenue (VNĐ)
    - Data points for last 6 months
```

**Row 2: Room Type Distribution (Pie Chart)**
```xml
MaterialCardView
├── TextView: "Phân bố loại phòng"
└── PieChart (MPAndroidChart library)
    - Single, Double, Suite, Deluxe
    - Color-coded slices
```

#### D. Quick Stats (Horizontal)

```xml
LinearLayout (Horizontal, 3 items)
├── Stat Card 1 (1/3 width)
│   ├── ImageView (Alert icon if count > 0)
│   ├── TextView: Count number
│   └── TextView: "Sắp hết hàng"
├── Stat Card 2 (1/3 width)
│   ├── ImageView
│   ├── TextView: Count number
│   └── TextView: "Đặt phòng chờ"
└── Stat Card 3 (1/3 width)
    ├── ImageView (RatingBar)
    ├── TextView: Rating number
    └── TextView: "Đánh giá TB"
```

#### E. Quick Actions (2x2 Grid)

```xml
GridLayout (2 columns)
├── Card: View Reports
│   ├── ImageView (Bar chart icon)
│   └── TextView: "Báo cáo"
├── Card: Manage Rooms
│   ├── ImageView (Room icon)
│   └── TextView: "Phòng"
├── Card: Manage Inventory
│   ├── ImageView (Inventory icon)
│   └── TextView: "Kho"
└── Card: View Feedback
    ├── ImageView (Star icon)
    └── TextView: "Đánh giá"
```

#### F. Recent Bookings

```xml
MaterialCardView
├── TextView: "Đặt phòng gần đây"
└── RecyclerView (Horizontal)
    - Item: item_booking_card.xml
    - Show last 10 bookings
```

### 📐 Dimensions

- **KPI Cards:** minHeight="100dp", padding="16dp"
- **Chart Cards:** height="250dp", padding="16dp"
- **Quick Stats:** height="80dp"
- **Action Cards:** height="100dp"

---

## 2. RevenueReportActivity

### 🎨 Layout Structure

```xml
CoordinatorLayout
├── AppBarLayout (MaterialToolbar + Back button)
└── NestedScrollView
    └── LinearLayout (Vertical)
        ├── Filter Section
        ├── Summary Card
        ├── Monthly Revenue (Bar Chart)
        ├── Daily Trend (Line Chart)
        ├── Statistics Table
        └── Export Buttons
```

### 📊 UI Components

#### A. Filter Section

```xml
MaterialCardView
├── TextView: "Chọn khoảng thời gian"
├── LinearLayout (Horizontal)
│   ├── ImageView (Calendar icon)
│   ├── TextView: "Từ ngày"
│   └── TextView: Selected start date
├── LinearLayout (Horizontal)
│   ├── ImageView (Calendar icon)
│   ├── TextView: "Đến ngày"
│   └── TextView: Selected end date
├── Button: "Áp dụng bộ lọc"
└── Spinner: Filter by room type (Optional)
```

#### B. Summary Card

```xml
MaterialCardView (background: #9C27B0)
├── LinearLayout (Horizontal)
│   ├── LinearLayout (Vertical - left, 1/2 width)
│   │   ├── TextView: "Tổng doanh thu"
│   │   ├── TextView: Large number - "1,500,000,000"
│   │   └── TextView: "VNĐ"
│   └── LinearLayout (Vertical - right, 1/2 width)
│       ├── TextView: "Số đặt phòng"
│       ├── TextView: Large number - "58"
│       └── TextView: "bookings"
```

#### C. Monthly Revenue (Bar Chart)

```xml
MaterialCardView
├── TextView: "Doanh thu theo tháng"
└── BarChart (MPAndroidChart)
    - X-axis: 12 months
    - Y-axis: Revenue
    - Grouped bars for different sources
```

#### D. Daily Trend (Line Chart)

```xml
MaterialCardView
├── TextView: "Xu hướng theo ngày"
└── LineChart (MPAndroidChart)
    - X-axis: Days in selected range
    - Y-axis: Daily revenue
```

#### E. Statistics Table

```xml
MaterialCardView
├── TextView: "Thống kê chi tiết"
└── TableLayout (5 rows, 2 columns)
    ├── Row: "Tổng doanh thu" | "1,500,000,000 VNĐ"
    ├── Row: "Tổng đặt phòng" | "58"
    ├── Row: "Tổng hoàn tiền" | "50,000,000 VNĐ"
    ├── Row: "Doanh thu ròng" | "1,450,000,000 VNĐ"
    └── Row: "Giá trị TB/booking" | "25,862,068 VNĐ"
```

#### F. Export Buttons

```xml
LinearLayout (Horizontal, 3 buttons)
├── Button: "Xuất PDF" (icon: PDF)
├── Button: "Xuất Excel" (icon: Excel)
└── Button: "Chia sẻ" (icon: Share)
```

---

## 3. OccupancyReportActivity

### 🎨 Layout Structure

```xml
CoordinatorLayout
├── AppBarLayout (MaterialToolbar + Back button)
└── NestedScrollView
    └── LinearLayout (Vertical)
        ├── Date Filter
        ├── Overall Occupancy (Large Display)
        ├── Pie Chart (Occupied vs Available)
        ├── Bar Chart (By Room Type)
        ├── Room Type Table
        └── Export Button
```

### 📊 UI Components

#### A. Date Filter

```xml
MaterialCardView
├── TextView: "Chọn khoảng thời gian"
├── DatePicker Start
└── DatePicker End
```

#### B. Overall Occupancy (Large Display)

```xml
MaterialCardView (background: #2196F3, height: 150dp)
├── LinearLayout (Vertical, center)
    ├── TextView: "85%" (xxxlarge, bold, white)
    ├── TextView: "Tỷ lệ lấp đầy" (caption, white)
    └── TextView: "45/50 phòng" (small, white)
```

#### C. Pie Chart

```xml
MaterialCardView
├── TextView: "Phân bố trạng thái phòng"
└── PieChart (MPAndroidChart)
    - Occupied: Red (#F44336)
    - Available: Green (#4CAF50)
    - Maintenance: Gray (#9E9E9E)
```

#### D. Bar Chart

```xml
MaterialCardView
├── TextView: "Tỷ lệ lấp đầy theo loại phòng"
└── BarChart (Horizontal)
    - Single: XX%
    - Double: XX%
    - Suite: XX%
    - Deluxe: XX%
```

#### E. Room Type Table

```xml
MaterialCardView
├── TextView: "Chi tiết theo loại phòng"
└── TableLayout (Header + rows)
    Columns:
    - Loại phòng
    - Tổng phòng
    - Đã thuê
    - Còn trống
    - Tỷ lệ %
```

---

## 4. FeedbackListActivity

### 🎨 Layout Structure

```xml
CoordinatorLayout
├── AppBarLayout (MaterialToolbar)
└── NestedScrollView
    └── LinearLayout (Vertical)
        ├── Overall Rating Display
        ├── Rating Breakdown
        ├── Filter & Sort
        └── RecyclerView (Feedback List)
```

### 📊 UI Components

#### A. Overall Rating Display

```xml
MaterialCardView (background: #FF9800, height: 120dp)
├── LinearLayout (Vertical, center)
    ├── RatingBar (large, 5 stars)
    ├── TextView: "4.5" (xxlarge, bold, white)
    ├── TextView: "Tổng đánh giá" (caption, white)
    └── TextView: "(125 đánh giá)" (small, white)
```

#### B. Rating Breakdown

```xml
MaterialCardView
├── TextView: "Phân bố đánh giá"
└── LinearLayout (Vertical, 5 rows)
    Each row:
    ├── LinearLayout (Horizontal)
    │   ├── TextView: "⭐⭐⭐⭐⭐" (5 stars)
    │   ├── ProgressBar (horizontal, 70%)
    │   └── TextView: "87" (count)
    └── (Repeat for 4, 3, 2, 1 stars)
```

#### C. Filter & Sort

```xml
MaterialCardView
├── TextView: "Lọc & Sắp xếp"
└── LinearLayout (Horizontal)
    ├── Spinner: "Tất cả" / "5 sao" / "4+ sao"
    └── Button: "Sắp xếp" (Date / Rating)
```

#### D. RecyclerView

```xml
RecyclerView
└── Items: item_feedback_card.xml
    - Guest avatar (or anonymous icon)
    - Guest name (or "Anonymous")
    - Rating stars
    - Comment
    - Date
    - Room/Booking info
```

---

## 5. FeedbackFormActivity

### 🎨 Layout Structure

```xml
CoordinatorLayout
├── AppBarLayout (MaterialToolbar + Back button)
└── NestedScrollView
    └── LinearLayout (Vertical)
        ├── Booking Info Card
        ├── Overall Rating Section
        ├── Detailed Ratings Section
        ├── Comment Section
        ├── Photo Upload (Optional)
        └── Submit Button
```

### 📊 UI Components

#### A. Booking Info Card

```xml
MaterialCardView
├── TextView: "Thông tin đặt phòng"
└── LinearLayout (Vertical)
    ├── Row: "Mã đặt phòng" | "BK12345"
    ├── Row: "Phòng" | "201 - Deluxe"
    ├── Row: "Nhận phòng" | "15/12/2024"
    └── Row: "Trả phòng" | "17/12/2024"
```

#### B. Overall Rating

```xml
MaterialCardView
├── TextView: "Đánh giá tổng thể *" (required)
├── TextView: "Vuốt để chọn sao"
└── RatingBar
    - Large size
    - 5 stars
    - Step: 0.5
    - NOT isIndicator (editable)
```

#### C. Detailed Ratings

```xml
MaterialCardView
├── TextView: "Đánh giá chi tiết"
└── LinearLayout (Vertical, 4 rows)
    Each row:
    ├── LinearLayout (Horizontal)
    │   ├── TextView: "Độ sạch sẽ"
    │   └── RatingBar (editable, 5 stars)
    └── (Repeat for: Dịch vụ, Tiện nghi, Giá trị)
```

#### D. Comment Section

```xml
MaterialCardView
├── TextView: "Bình luận (tùy chọn)"
└── TextInputLayout
    └── TextInputEditText
        - Multiline: true
        - Lines: 5
        - Hint: "Chia sẻ trải nghiệm của bạn..."
        - Max length: 500
```

#### E. Photo Upload (Optional)

```xml
MaterialCardView
├── TextView: "Thêm ảnh (tối đa 3)"
└── HorizontalScrollView
    └── LinearLayout (Horizontal)
        ├── ImageView (Placeholder 1) + Remove button
        ├── ImageView (Placeholder 2) + Remove button
        ├── ImageView (Placeholder 3) + Remove button
        └── ImageView (Add button) - opens gallery
```

#### F. Submit Button

```xml
MaterialCardView
├── CheckBox: "Gửi đánh giá ẩn danh"
└── Button: "Gửi đánh giá"
    - Full width
    - Height: 56dp
    - Background: Primary color
```

---

## 📊 CHART DESIGN GUIDELINES

### MPAndroidChart Configuration

```java
// Common settings for all charts
chart.setDescription(null); // Remove description
chart.getLegend().setEnabled(true);
chart.setScaleEnabled(true);
chart.setPinchZoom(true);

// Colors
int[] colors = {
    ContextCompat.getColor(this, R.color.primary),
    ContextCompat.getColor(this, R.color.success),
    ContextCompat.getColor(this, R.color.warning),
    ContextCompat.getColor(this, R.color.error)
};

// X-axis (common)
XAxis xAxis = chart.getXAxis();
xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
xAxis.setGranularity(1f);
xAxis.setTextColor(Color.BLACK);
xAxis.setTextSize(10f);

// Y-axis (left)
YAxis leftAxis = chart.getAxisLeft();
leftAxis.setTextColor(Color.BLACK);
leftAxis.setTextSize(10f);

// Y-axis (right)
YAxis rightAxis = chart.getAxisRight();
rightAxis.setEnabled(false);
```

---

## 🎨 COLOR PALETTE

### KPI Cards
```xml
<!-- Revenue -->
app:cardBackgroundColor="@color/role_manager" <!-- Purple -->

<!-- Occupancy -->
app:cardBackgroundColor="@color/info" <!-- Blue -->

<!-- Bookings -->
app:cardBackgroundColor="@color/success" <!-- Green -->

<!-- Active -->
app:cardBackgroundColor="@color/warning" <!-- Orange -->
```

### Charts
```xml
<!-- Line Chart -->
- Line color: Primary blue
- Fill color: Light blue with gradient

<!-- Bar Chart -->
- Bar color: Primary blue
- Value colors: Blue, Green, Orange, Red

<!-- Pie Chart -->
- Slice colors: 
  - Success (Green) for positive
  - Error (Red) for negative
  - Warning (Orange) for neutral
```

---

## 📱 RESPONSIVE DESIGN

### Tablet Support
```xml
<!-- Use resource qualifiers -->
res/layout/activity_manager_dashboard.xml (Phone)
res/layout-w600dp/activity_manager_dashboard.xml (Tablet)

<!-- Tablet: 2-column grid for charts -->
<!-- Tablet: Wider KPI cards -->
<!-- Tablet: Larger chart heights -->
```

---

## ✅ CHECKLIST

### ManagerDashboardActivity
- [ ] Welcome section with date/time
- [ ] 4 KPI cards (2x2 grid)
- [ ] Line chart: Revenue trend
- [ ] Pie chart: Room types
- [ ] Quick stats (3 items)
- [ ] Quick actions (2x2 grid)
- [ ] Recent bookings RecyclerView
- [ ] Logout menu
- [ ] Real-time updates

### RevenueReportActivity
- [ ] Date range picker
- [ ] Summary card
- [ ] Bar chart: Monthly revenue
- [ ] Line chart: Daily trend
- [ ] Statistics table
- [ ] Export buttons (PDF, Excel, Share)
- [ ] Back navigation

### OccupancyReportActivity
- [ ] Date filter
- [ ] Overall occupancy display
- [ ] Pie chart: Occupied vs Available
- [ ] Bar chart: By room type
- [ ] Room type table
- [ ] Export button
- [ ] Back navigation

### FeedbackListActivity
- [ ] Overall rating display
- [ ] Rating breakdown bars
- [ ] Filter & sort
- [ ] RecyclerView with feedback items
- [ ] Empty state
- [ ] Back navigation

### FeedbackFormActivity
- [ ] Booking info card
- [ ] Overall rating (editable)
- [ ] Detailed ratings (4 items)
- [ ] Comment input (multiline)
- [ ] Photo upload (3 images)
- [ ] Anonymous checkbox
- [ ] Submit button
- [ ] Back navigation
- [ ] Validation

---

## 🔧 TECHNICAL REQUIREMENTS

### Dependencies Needed
```gradle
// Charts
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

// PDF Export
implementation 'com.itextpdf:itext7-core:7.2.5'

// Excel Export
implementation 'org.apache.poi:poi:5.2.3'

// Permission for file storage
implementation 'androidx.core:core-ktx:1.12.0'
```

### Permissions Needed
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
```

---

## 🎯 KEY UI PATTERNS

### 1. Card Hierarchy
- **KPI Cards:** Prominent, colored backgrounds, large numbers
- **Chart Cards:** White background, elevated, with title
- **Data Cards:** Lighter elevation, detailed info
- **Action Cards:** Medium elevation, icon + text

### 2. Data Display
- **Large numbers:** 32sp, bold, prominent
- **Labels:** 12sp, secondary color
- **Units:** 14sp, tertiary color
- **Spacing:** 16dp between elements

### 3. Interactive Elements
- **Buttons:** 48dp height, rounded corners
- **Cards:** Clickable, ripple effect
- **Charts:** Pinch zoom, long press for details
- **Input fields:** Floating labels, validation errors

### 4. Loading States
- **Charts:** Skeleton loader with shimmer
- **RecyclerView:** Placeholder cards
- **Data:** ProgressBar overlay
- **Export:** Loading dialog

---

**📝 Lưu ý:** Đây là hướng dẫn thiết kế UI. Cần implement chi tiết từng layout với đầy đủ constraints và responsive design.

*Created: 2024*









