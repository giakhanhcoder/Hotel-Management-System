# 📝 YÊU CẦU VÀ CÁCH SỬA CHO SHOES MANAGEMENT APP

## 🔍 PHÂN TÍCH YÊU CẦU

Theo yêu cầu trong đề bài, đây là những gì cần thực hiện:

### 1. **DATABASE NAME** ⚠️ CẦN SỬA
- **Yêu cầu:** Database name phải là `ShoesDatabase`
- **Hiện tại:** Database tên là `hotel_management_db` (trong `AppDatabase.java`)
- **Cần sửa:** 
  ```java
  // Trong AppDatabase.java, dòng 47
  private static final String DATABASE_NAME = "ShoesDatabase"; // Đổi từ "hotel_management_db"
  ```

### 2. **TABLE SHOESDATA** ⚠️ CẦN TẠO MỚI
- **Yêu cầu:** Tạo table `ShoesData` với các trường:
  - `Shoes_ID`: Auto generated (Primary Key, Auto Increment)
  - `Shoes_Name`: Tên giày
  - `Shoes_No`: Số giày (UNIQUE - không được trùng)
  - `Type`: Loại giày
  - `Price`: Giá

- **Cần tạo:**
  - Entity: `ShoesData.java` trong `database/entities/`
  - DAO: `ShoesDao.java` trong `database/dao/`
  - Repository: `ShoesRepository.java` trong `repository/`

### 3. **RECYCLERVIEW CHO LIST SCREEN** ✅ ĐÃ CÓ
- **Yêu cầu:** Phải dùng RecyclerView cho list screen
- **Hiện tại:** Dự án đã sử dụng RecyclerView (BookingDashboardActivity, BookingAdapter)
- **Cần làm:** Tạo màn hình list giày sử dụng RecyclerView (tương tự BookingDashboardActivity)

### 4. **SCREEN TITLES TRONG ACTIONBAR** ✅ ĐÃ CÓ
- **Yêu cầu:** Screen titles hiển thị trên ActionBar
- **Hiện tại:** Đã có sử dụng `setTitle()` và ActionBar
- **Cần làm:** Đảm bảo màn hình list giày có title trên ActionBar

### 5. **DEFAULT DATA (INSERT STATEMENT)** ⚠️ CẦN THÊM
- **Yêu cầu:** Cần có INSERT statement để chạy List screen nếu Create screen chưa hoàn thành
- **Cần làm:** Thêm default data trong `AppDatabase` callback hoặc tạo file SQL riêng

### 6. **XÓA BUILD FILES TRƯỚC KHI NÉN** ⚠️ CẦN NHỚ
- **Yêu cầu:** Trước khi zip project, phải xóa tất cả files trong `app\build` directory
- **Cần làm:** Xóa thư mục `app/build` trước khi nén

---

## 📋 CÁC BƯỚC CẦN THỰC HIỆN

### BƯỚC 1: Tạo Entity ShoesData
```java
// File: app/src/main/java/com/example/projectprmt5/database/entities/ShoesData.java
@Entity(tableName = "ShoesData")
public class ShoesData {
    @PrimaryKey(autoGenerate = true)
    private int Shoes_ID;
    
    private String Shoes_Name;
    
    @ColumnInfo(name = "Shoes_No")
    @NonNull
    private String Shoes_No; // UNIQUE
    
    private String Type;
    
    private double Price;
    
    // Constructors, getters, setters...
}
```

### BƯỚC 2: Tạo DAO
```java
// File: app/src/main/java/com/example/projectprmt5/database/dao/ShoesDao.java
@Dao
public interface ShoesDao {
    @Query("SELECT * FROM ShoesData ORDER BY Shoes_ID ASC")
    LiveData<List<ShoesData>> getAllShoes();
    
    @Insert
    void insert(ShoesData shoes);
    
    @Query("SELECT * FROM ShoesData WHERE Shoes_No = :shoesNo")
    ShoesData getShoesByNo(String shoesNo);
    
    // ... other queries
}
```

### BƯỚC 3: Cập nhật AppDatabase
- Đổi tên database: `ShoesDatabase`
- Thêm `ShoesData` vào entities
- Thêm `ShoesDao` vào abstract methods
- Thêm default data trong callback

### BƯỚC 4: Tạo Repository
```java
// File: app/src/main/java/com/example/projectprmt5/repository/ShoesRepository.java
public class ShoesRepository {
    private ShoesDao shoesDao;
    private LiveData<List<ShoesData>> allShoes;
    
    // ... implementation
}
```

### BƯỚC 5: Tạo List Screen với RecyclerView
- Activity: `ShoesListActivity.java`
- Layout: `activity_shoes_list.xml` (có RecyclerView)
- Adapter: `ShoesAdapter.java`
- Item layout: `item_shoes.xml`

### BƯỚC 6: Thêm Default Data
```java
// Trong AppDatabase.java, method populateInitialData()
ShoesDao shoesDao = database.shoesDao();
shoesDao.insert(new ShoesData("Nike Air Max", "N001", "Running", 2500000));
shoesDao.insert(new ShoesData("Adidas Ultraboost", "A001", "Running", 3200000));
shoesDao.insert(new ShoesData("Converse Chuck", "C001", "Casual", 1500000));
// ... thêm vài dòng default data
```

---

## 🚨 CHÚ Ý QUAN TRỌNG

### 1. **Naming Conventions** (Quy tắc đặt tên)
- Project name và package phải đúng quy tắc
- Tên biến, property, class phải khớp với yêu cầu đề bài
- **Shoes_ID, Shoes_Name, Shoes_No, Type, Price** - phải viết đúng như vậy

### 2. **RecyclerView là BẮT BUỘC**
- KHÔNG được dùng ListView
- KHÔNG được dùng các Layout khác cho list

### 3. **Code Similarity** (Độ tương đồng code)
- Code không được giống nhau > 30% với bạn cùng lớp
- Không copy code từ nguồn khác

### 4. **Internet Usage**
- KHÔNG được kết nối Internet trong lúc thi

### 5. **Irrelevant Code**
- Xóa code không liên quan đến yêu cầu
- Chỉ giữ code cần thiết cho Shoes Management App

---

## 📝 CHECKLIST TRƯỚC KHI NỘP

- [ ] Database name = `ShoesDatabase`
- [ ] Table `ShoesData` với đúng 5 trường: Shoes_ID, Shoes_Name, Shoes_No, Type, Price
- [ ] Shoes_No là UNIQUE
- [ ] List screen dùng RecyclerView
- [ ] Screen titles hiển thị trên ActionBar
- [ ] Có default INSERT data để test List screen
- [ ] Xóa thư mục `app/build` trước khi zip
- [ ] Tên biến, class khớp với yêu cầu
- [ ] Không có code không liên quan
- [ ] Không kết nối Internet

---

## 💡 GỢI Ý THỰC HIỆN

1. **Tạo mới database riêng** cho Shoes App (không dùng Hotel Database)
2. **Hoặc chuyển đổi** toàn bộ Hotel App thành Shoes App
3. **Đảm bảo naming** đúng: Shoes_ID, Shoes_Name, Shoes_No, Type, Price
4. **Test kỹ** RecyclerView và default data

---

**LƯU Ý:** Đây là comment/hướng dẫn để tham khảo. Bạn cần thực hiện các bước trên để đáp ứng yêu cầu đề bài.




