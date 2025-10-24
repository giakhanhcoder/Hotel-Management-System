# 🔧 FIX: Lỗi Đăng Nhập Sai Password

## ⚠️ Vấn đề

Password trong database **KHÁC** với password trong tài liệu test!

### ❌ Trước khi sửa:

**Database có:**
- Admin: `admin123` (chữ thường)
- Receptionist: `reception123` (chữ thường)  
- Guest: `guest123` (chữ thường)

**Documentation ghi:**
- Admin: `Admin123!` (chữ hoa + ký tự đặc biệt)
- Receptionist: `Receptionist123!` (chữ hoa + ký tự đặc biệt)
- Guest: `Guest123!` (chữ hoa + ký tự đặc biệt)

→ **Login thất bại!**

---

## ✅ Đã sửa

**File:** `app/src/main/java/com/example/projectprmt5/database/AppDatabase.java`

**Dòng 126, 137, 148:** Đã cập nhật password:

```java
// ADMIN
hashPassword("Admin123!")     // Trước: admin123

// RECEPTIONIST
hashPassword("Receptionist123!")  // Trước: reception123

// GUEST
hashPassword("Guest123!")     // Trước: guest123
```

---

## 🔄 QUAN TRỌNG: Bạn cần Reset Database!

App đã được build lại nhưng **database cũ vẫn còn trong thiết bị**. Bạn cần 1 trong 2 cách sau:

### Cách 1: Xóa Data & Cache (Khuyến nghị ⭐)

**Trên Emulator/Device:**
1. **Settings** → **Apps** → **ProjectPRMT5**
2. **Storage** → **Clear Data** hoặc **Clear Storage**
3. **Clear Cache** (optional)
4. Mở lại app
5. Database sẽ được tạo mới với password đúng!

### Cách 2: Uninstall & Reinstall

**Trên Emulator:**
```bash
# Trong Terminal
adb uninstall com.example.projectprmt5
```

Hoặc **giữ icon app → Uninstall**

Sau đó chạy lại:
```bash
.\gradlew installDebug
```

### Cách 3: Dùng lệnh ADB (Nhanh nhất)

```bash
# Xóa database trực tiếp
adb shell run-as com.example.projectprmt5 rm databases/hotel_management_db
adb shell run-as com.example.projectprmt5 rm databases/hotel_management_db-shm
adb shell run-as com.example.projectprmt5 rm databases/hotel_management_db-wal

# Hoặc xóa toàn bộ data
adb shell pm clear com.example.projectprmt5
```

Sau đó mở lại app.

---

## 🧪 Test Login Ngay

Sau khi reset database, dùng tài khoản:

### 👤 MANAGER (Admin)
```
📧 Email: admin@hotel.com
🔒 Password: Admin123!
```

### 👔 RECEPTIONIST
```
📧 Email: receptionist@hotel.com
🔒 Password: Receptionist123!
```

### 🏨 GUEST
```
📧 Email: guest@example.com
🔒 Password: Guest123!
```

---

## ✅ Kiểm tra Database đã reset chưa

Sau khi mở app lần đầu (sau khi reset):

1. **Logcat** sẽ hiển thị:
   ```
   Database created successfully
   Populating initial data...
   ```

2. **Database Inspector** (trong Android Studio):
   - View → Tool Windows → App Inspection
   - Tab Database Inspector
   - Xem bảng `users` → 3 users mới được tạo

3. **Login test:**
   - Nếu login thành công → ✅ Database đã được reset
   - Nếu vẫn sai → Bạn chưa xóa database cũ

---

## 📝 Technical Details

### Password Hashing Flow:

```java
// 1. User nhập: Admin123!
String inputPassword = "Admin123!";

// 2. LoginActivity hash (dòng 136)
String hashedPassword = "HASH_" + inputPassword;  // → "HASH_Admin123!"

// 3. So sánh với database
// Database có: hashPassword("Admin123!") → "HASH_Admin123!"

// 4. Match → Login thành công! ✅
```

### Files liên quan:

- **AppDatabase.java** (dòng 126-154): Prepopulate data
- **LoginActivity.java** (dòng 136): Hash password khi login
- **UserRepository.java** (dòng 139-141): Hash function

---

## 🐛 Troubleshooting

### Vẫn login sai sau khi reset?

**Kiểm tra:**

1. **Database đã được xóa chưa?**
   ```bash
   adb shell run-as com.example.projectprmt5 ls databases/
   # Nếu thấy hotel_management_db → Database vẫn còn
   ```

2. **App version đã update?**
   ```bash
   adb shell dumpsys package com.example.projectprmt5 | grep versionCode
   ```

3. **Logcat có lỗi gì không?**
   - Mở Logcat trong Android Studio
   - Filter: `com.example.projectprmt5`
   - Xem có error nào không

### Email sai format?

Nhớ nhập **ĐÚNG EMAIL**:
- ✅ `admin@hotel.com`
- ❌ `admin` (thiếu @hotel.com)
- ❌ `Admin@hotel.com` (chữ A hoa - email case-sensitive trong database!)

### Password ngắn hơn 6 ký tự?

LoginActivity validate:
```java
if (password.length() < 6) {
    etPassword.setError("Mật khẩu phải từ 6 ký tự");
    return false;
}
```

`Admin123!` = 9 ký tự → OK ✅

---

## 🎯 Summary

| Bước | Hành động | Kết quả |
|------|-----------|---------|
| 1 | ✅ Sửa password trong AppDatabase.java | Done |
| 2 | ✅ Build lại app | Done |
| 3 | ⏳ **BẠN CẦN LÀM:** Xóa database cũ | Chưa |
| 4 | 🎯 Test login với password mới | Sẽ OK |

---

## 📞 Hỗ trợ

Nếu vẫn gặp vấn đề:

1. Screenshot màn hình login
2. Copy Logcat error (nếu có)
3. Kiểm tra `users` table trong Database Inspector
4. Xác nhận đã Clear Data app chưa

**Chúc bạn test thành công! 🚀**


