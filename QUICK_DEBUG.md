# ⚡ QUICK DEBUG - 3 CÁCH

## 1️⃣ Database Debug Activity (Dễ nhất! ⭐)

**Trên màn Login:**
1. **GIỮ LÂU** (long press) bất kỳ đâu trên màn hình
2. Xem danh sách users
3. Check có **3 users** không?
4. Check **password hash** có đúng không?

**Mong đợi:**
```
User #1
Email: admin@hotel.com
Password Hash: HASH_Admin123!

User #2
Email: receptionist@hotel.com
Password Hash: HASH_Receptionist123!

User #3
Email: guest@example.com
Password Hash: HASH_Guest123!
```

**Nếu database trống:** Nhấn nút **🗑️ Clear Database**

---

## 2️⃣ Logcat (Chi tiết nhất! ⭐⭐)

**Android Studio:**
1. Mở **Logcat** (Alt+6)
2. Filter: `LoginActivity`
3. Test login
4. Đọc output

**Tìm dòng này:**
```
Password nhập vào: Admin123!
Password sau khi hash: HASH_Admin123!
Password trong DB: HASH_Admin123!
✅ LOGIN THÀNH CÔNG!
```

Hoặc:
```
❌ LOGIN THẤT BẠI - Không tìm thấy user
```

---

## 3️⃣ Database Inspector

**Android Studio:**
1. **View** → **App Inspection** → **Database Inspector**
2. Chọn database `hotel_management_db`
3. Xem bảng `users`
4. Phải có **3 rows**

---

## 🔧 QUICK FIX

Database sai? Xóa và tạo lại:

```bash
adb shell pm clear com.example.projectprmt5
```

Hoặc dùng nút **🗑️ Clear Database** trong Debug Activity

---

Chi tiết: `DEBUG_LOGIN_GUIDE.md`











