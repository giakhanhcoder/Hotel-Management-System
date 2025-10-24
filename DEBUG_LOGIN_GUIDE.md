# 🔍 HƯỚNG DẪN DEBUG LOGIN

## ✅ ĐÃ THÊM DEBUG TOOLS

App hiện có **3 cách debug** để tìm lỗi login!

---

## 🛠️ CÁCH 1: Xem Logcat (Khuyến nghị ⭐)

### Bước 1: Mở Logcat trong Android Studio

1. **View** → **Tool Windows** → **Logcat**
2. Hoặc nhấn **Alt+6**

### Bước 2: Filter Log

Trong ô filter, gõ:
```
LoginActivity
```

Hoặc click dropdown **Show only selected application**

### Bước 3: Test Login

1. Mở app trên emulator
2. Nhập email/password
3. Nhấn Login
4. **Xem Logcat ngay!**

### Bước 4: Đọc Log Output

Bạn sẽ thấy:

```
========== LOGIN DEBUG START ==========
Email nhập vào: admin@hotel.com
Password nhập vào: Admin123!
Password length: 9
Password sau khi hash: HASH_Admin123!
Bắt đầu query database...
Kết quả query: Tìm thấy user
User ID: 1
User Email: admin@hotel.com
User Full Name: System Administrator
User Role: MANAGER
User isActive: true
Password trong DB: HASH_Admin123!
✅ LOGIN THÀNH CÔNG!
========== LOGIN DEBUG END ==========
```

### ✅ Nếu thành công:
```
✅ LOGIN THÀNH CÔNG!
```

### ❌ Nếu thất bại:
```
❌ LOGIN THẤT BẠI - Không tìm thấy user hoặc password sai
```

Hoặc:

```
Kết quả query: KHÔNG tìm thấy user
```

**Nghĩa là:**
- Email sai HOẶC
- Password sai HOẶC
- Database chưa có user này

---

## 🛠️ CÁCH 2: Database Debug Activity (Siêu dễ! ⭐⭐⭐)

### Bước 1: Mở Database Debug

**Trên màn hình Login**, **GIỮ LÂU** (long press) vào **bất kỳ đâu** trên màn hình

→ Màn hình **Database Debug** sẽ mở!

### Bước 2: Xem Database Info

Màn hình sẽ hiển thị:

```
🔍 DATABASE DEBUG TOOL
━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📁 Database: hotel_management_db
👥 Total Users: 3

━━━━━━━━━━━━━━━━━━━━━━━━━━━━
👤 ALL USERS IN DATABASE:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━

User #1
─────────────────────────────
ID: 1
Email: admin@hotel.com
Full Name: System Administrator
Role: MANAGER
Password Hash: HASH_Admin123!
Is Active: ✅ Yes
Phone: +84901234567

User #2
─────────────────────────────
ID: 2
Email: receptionist@hotel.com
Full Name: Front Desk Staff
Role: RECEPTIONIST
Password Hash: HASH_Receptionist123!
Is Active: ✅ Yes
Phone: +84909876543

User #3
─────────────────────────────
ID: 3
Email: guest@example.com
Full Name: John Doe
Role: GUEST
Password Hash: HASH_Guest123!
Is Active: ✅ Yes
Phone: +84905555555
```

### ✅ Kiểm tra:

1. **Total Users**: Phải có **3 users**
   - Nếu = 0 → Database trống, cần Clear Data app
   - Nếu < 3 → Database chưa đầy đủ

2. **Password Hash**: Phải có prefix `HASH_`
   - Admin: `HASH_Admin123!`
   - Receptionist: `HASH_Receptionist123!`
   - Guest: `HASH_Guest123!`

3. **Email**: Phải đúng chính xác
   - `admin@hotel.com` (chữ thường)
   - `receptionist@hotel.com` (chữ thường)
   - `guest@example.com` (chữ thường)

### Nút chức năng:

**🔄 Refresh Database Info**: Load lại database

**🗑️ Clear Database**: Xóa database và tạo lại
- Nhấn nút này nếu database có vấn đề
- App sẽ tự restart
- Database mới sẽ được tạo với dữ liệu đúng

---

## 🛠️ CÁCH 3: Database Inspector (Android Studio)

### Bước 1: Mở Database Inspector

1. **View** → **Tool Windows** → **App Inspection**
2. Tab **Database Inspector**
3. Chọn app **com.example.projectprmt5**
4. Chọn database **hotel_management_db**

### Bước 2: Xem bảng Users

1. Click vào bảng **users**
2. Xem tất cả records

### Bước 3: Kiểm tra

Bảng `users` phải có **3 rows**:

| userId | email | passwordHash | fullName | role | isActive |
|--------|-------|--------------|----------|------|----------|
| 1 | admin@hotel.com | HASH_Admin123! | System Administrator | MANAGER | 1 |
| 2 | receptionist@hotel.com | HASH_Receptionist123! | Front Desk Staff | RECEPTIONIST | 1 |
| 3 | guest@example.com | HASH_Guest123! | John Doe | GUEST | 1 |

---

## 🔴 CÁC LỖI THƯỜNG GẶP

### Lỗi 1: Database trống (0 users)

**Logcat:**
```
Total users in database: 0
⚠️ WARNING: Database is EMPTY!
```

**Giải pháp:**
```bash
# Clear data app
adb shell pm clear com.example.projectprmt5
```

Hoặc dùng nút **🗑️ Clear Database** trong Debug Activity

---

### Lỗi 2: Password không khớp

**Logcat:**
```
Password nhập vào: Admin123!
Password sau khi hash: HASH_Admin123!
Password trong DB: HASH_admin123     ← SAI!
❌ LOGIN THẤT BẠI
```

**Nguyên nhân:** Database cũ có password khác

**Giải pháp:** Clear database

---

### Lỗi 3: Email sai

**Logcat:**
```
Email nhập vào: Admin@hotel.com     ← Chữ A hoa
Kết quả query: KHÔNG tìm thấy user
```

**Nguyên nhân:** Email trong DB là `admin@hotel.com` (chữ thường)

**Giải pháp:** Nhập đúng `admin@hotel.com`

---

### Lỗi 4: User bị vô hiệu hóa

**Logcat:**
```
User isActive: false
❌ Tài khoản bị vô hiệu hóa
```

**Giải pháp:** 
- Trong Database Inspector, sửa `isActive` = 1
- Hoặc clear database để tạo lại

---

## 📋 CHECKLIST DEBUG

Làm theo thứ tự:

### ☑️ Bước 1: Kiểm tra Database
- [ ] Mở **Database Debug Activity** (long press màn login)
- [ ] Xem có **3 users** không?
- [ ] Xem **password hash** đúng không?

### ☑️ Bước 2: Kiểm tra Input
- [ ] Email đúng format: `admin@hotel.com` (chữ thường)
- [ ] Password đúng: `Admin123!` (có chữ hoa + ký tự đặc biệt)
- [ ] Password >= 6 ký tự

### ☑️ Bước 3: Xem Logcat
- [ ] Filter: `LoginActivity`
- [ ] Test login
- [ ] Đọc log output
- [ ] Check `Password nhập vào` vs `Password trong DB`

### ☑️ Bước 4: So sánh
```
Bạn nhập:           HASH_Admin123!
Database có:        HASH_Admin123!
                    ✅ KHỚP → Login OK
```

Hoặc:
```
Bạn nhập:           HASH_Admin123!
Database có:        HASH_admin123
                    ❌ KHÔNG KHỚP → Login FAILED
```

---

## 🚨 EMERGENCY FIX

Nếu **TẤT CẢ** đều không work:

### 1. Clear Everything
```bash
# Uninstall app
adb uninstall com.example.projectprmt5

# Clear cache
adb shell pm clear com.example.projectprmt5

# Reinstall
cd D:\ProjectPRM
.\gradlew installDebug
```

### 2. Fresh Install
1. Xóa app khỏi emulator (long press icon → Uninstall)
2. Chạy: `.\gradlew clean`
3. Chạy: `.\gradlew installDebug`
4. Mở app lần đầu → Database tự tạo
5. Mở Database Debug → Check có 3 users
6. Test login

---

## 💡 TIPS

### Tip 1: Copy Log
Trong Logcat, click phải → **Copy** để copy log và gửi cho team

### Tip 2: Export Database
Trong Database Inspector: **Export Database** để lưu file .db

### Tip 3: Quick Test
Dùng **Database Debug Activity** để test nhanh nhất:
1. Long press màn login
2. Xem users
3. Nhớ email + password
4. Back về màn login
5. Login

---

## 📞 BÁO LỖI

Nếu vẫn không được, cung cấp:

1. **Screenshot** Database Debug Activity
2. **Logcat output** (copy full log từ START đến END)
3. **Email + Password** bạn đang nhập
4. **App version**: Xem trong `build.gradle`

---

## ✅ KẾT QUẢ MONG ĐỢI

Sau khi debug, bạn sẽ biết chính xác:

✅ Database có bao nhiêu users  
✅ Password trong DB là gì  
✅ Email đúng format chưa  
✅ Password bạn nhập vs password trong DB có khớp không  
✅ User có active không  

→ Tìm ra lỗi và fix ngay!

---

**Chúc bạn debug thành công! 🎉**


