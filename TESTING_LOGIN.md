# 🧪 TEST LOGIN ACTIVITY

## ⚠️ QUAN TRỌNG: ĐỌC TRƯỚC KHI TEST!

**Nếu app đã chạy trước đây, BẠN CẦN RESET DATABASE:**

### Cách 1: Clear Data (Khuyến nghị ⭐)
1. **Settings** → **Apps** → **ProjectPRMT5**
2. **Storage** → **Clear Data**
3. Mở lại app

### Cách 2: Lệnh ADB
```bash
adb shell pm clear com.example.projectprmt5
```

**Lý do:** Password trong database đã được cập nhật từ `admin123` → `Admin123!`

Chi tiết: Xem file `FIX_LOGIN_PASSWORD.md`

---

## ✅ ĐÃ TẠO XONG

### 1. LoginActivity.java ✅
**Đường dẫn:** `app/src/main/java/com/example/projectprmt5/LoginActivity.java`

**Chức năng:**
- ✅ Validation email & password
- ✅ Login với database (UserRepository)
- ✅ SharedPreferences (Remember Me)
- ✅ Auto-login nếu Remember Me
- ✅ Navigate theo role (Guest/Receptionist/Manager)
- ✅ Loading state
- ✅ Error handling

### 2. AndroidManifest.xml ✅
- ✅ LoginActivity đã được khai báo
- ✅ Set làm LAUNCHER activity
- ✅ MainActivity không còn là launcher

### 3. Layout ✅
- ✅ Sử dụng `activity_login_template.xml` có sẵn

---

## 🧪 CÁCH TEST

### Bước 1: Build & Run
```
1. Sync Gradle (File > Sync Project with Gradle Files)
2. Build project (Build > Make Project) hoặc Ctrl+F9
3. Run app (Shift+F10)
```

### Bước 2: Test Login với Tài Khoản Mẫu

Database đã có sẵn 3 tài khoản:

#### Tài khoản 1: Manager
- **Email:** `admin@hotel.com`
- **Password:** `Admin123!`
- **Role:** MANAGER

#### Tài khoản 2: Receptionist
- **Email:** `receptionist@hotel.com`
- **Password:** `Receptionist123!`
- **Role:** RECEPTIONIST

#### Tài khoản 3: Guest
- **Email:** `guest@example.com`
- **Password:** `Guest123!`
- **Role:** GUEST

**Lưu ý:** Password trong database có prefix `HASH_` nhưng bạn chỉ cần nhập password gốc (app sẽ tự hash).

---

## ✅ CHECKLIST TEST

### Test Cases Cơ Bản:

- [ ] **TC1: Empty Fields**
  - Không nhập gì → Click "Đăng nhập"
  - Expected: Hiện lỗi "Trường này bắt buộc"

- [ ] **TC2: Invalid Email**
  - Email: `test` (không có @)
  - Password: `123456`
  - Expected: Hiện lỗi "Email không hợp lệ"

- [ ] **TC3: Short Password**
  - Email: `test@test.com`
  - Password: `123` (< 6 ký tự)
  - Expected: Hiện lỗi "Mật khẩu phải có ít nhất 6 ký tự"

- [ ] **TC4: Wrong Credentials**
  - Email: `wrong@email.com`
  - Password: `wrongpass`
  - Expected: Toast "Email hoặc mật khẩu không đúng"

- [ ] **TC5: Correct Login - Guest**
  - Email: `guest@example.com`
  - Password: `guest123`
  - Expected: Toast "Đăng nhập thành công" + "Welcome Guest!" + Navigate to MainActivity

- [ ] **TC6: Correct Login - Receptionist**
  - Email: `receptionist@hotel.com`
  - Password: `reception123`
  - Expected: Toast "Welcome Receptionist!" + Navigate

- [ ] **TC7: Correct Login - Manager**
  - Email: `admin@hotel.com`
  - Password: `admin123`
  - Expected: Toast "Welcome Manager!" + Navigate

- [ ] **TC8: Remember Me**
  - Login với Remember Me checked
  - Close app
  - Reopen app
  - Expected: Tự động login, không cần nhập lại

- [ ] **TC9: Loading State**
  - Click "Đăng nhập"
  - Expected: Button text đổi thành "Đang tải..." và disabled

---

## 🐛 NẾU GẶP LỖI

### Lỗi: Cannot resolve symbol 'R'
```bash
Solution:
1. Build > Clean Project
2. Build > Rebuild Project
3. File > Invalidate Caches > Invalidate and Restart
```

### Lỗi: java.lang.RuntimeException: Cannot create an instance of class UserRepository
```bash
Solution: Check UserRepository constructor có đúng không
// Should be:
public UserRepository(Application application) {
    // ...
}
```

### Lỗi: Database chưa có data
```bash
Solution:
1. Uninstall app từ device/emulator
2. Run lại app → Database sẽ tự populate initial data
```

### Lỗi: Login always fails
```bash
Debug steps:
1. Check password trong database có prefix "HASH_" không
2. Check code login có hash password với "HASH_" không
3. Check email có đúng chính xác không (case-sensitive)
```

### Lỗi: App crashes on login
```bash
Check Logcat:
1. View > Tool Windows > Logcat
2. Tìm dòng màu đỏ
3. Đọc error message
4. Common issues:
   - Database not initialized
   - UI update trên background thread
   - Null pointer exception
```

---

## 📱 DEMO FLOW

### Flow 1: First Time User
```
1. Open app
2. See LoginActivity
3. Enter guest@example.com / guest123
4. Click "Đăng nhập"
5. See "Đăng nhập thành công"
6. See "Welcome Guest!"
7. Navigate to MainActivity (temporary - sẽ là GuestDashboard)
```

### Flow 2: Remember Me
```
1. Login với Remember Me checked
2. App navigate to main screen
3. Press back → Exit app
4. Reopen app
5. Auto login → Directly to main screen
```

### Flow 3: Wrong Password
```
1. Enter admin@hotel.com / wrongpass
2. Click "Đăng nhập"
3. See loading state
4. See "Email hoặc mật khẩu không đúng"
5. Stay on login screen
```

---

## 🔧 CUSTOMIZATION

### Thay đổi màu loading button:
```java
// Trong LoginActivity.java - method showLoading()
btnLogin.setBackgroundColor(getColor(R.color.text_disabled));
```

### Thêm ProgressBar:
```xml
<!-- Trong activity_login_template.xml, thêm: -->
<ProgressBar
    android:id="@+id/progressBar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:visibility="gone" />
```

Sau đó uncomment dòng progressBar trong code.

---

## 📊 EXPECTED RESULTS

### Khi login thành công:
1. ✅ Toast "Đăng nhập thành công"
2. ✅ Toast "Welcome [Role]!"
3. ✅ Navigate to MainActivity
4. ✅ SharedPreferences saved (userId, email, role)
5. ✅ LoginActivity finish (không thể back về)

### Khi login thất bại:
1. ✅ Toast "Email hoặc mật khẩu không đúng"
2. ✅ Stay on LoginActivity
3. ✅ Fields không bị clear
4. ✅ Button enabled lại

---

## 🎯 NEXT STEPS

Sau khi test login thành công, có thể làm tiếp:

### Người 1 (Authentication) tiếp tục:
1. ✅ LoginActivity (DONE)
2. ⏭️ SplashActivity (next)
3. ⏭️ WelcomeActivity
4. ⏭️ RegisterActivity
5. ⏭️ ForgotPasswordActivity
6. ⏭️ ProfileActivity
7. ⏭️ ChangePasswordActivity

### Integration với màn khác:
- Người 5 tạo GuestDashboardActivity → Update navigation
- Người 5 tạo ManagerDashboardActivity → Update navigation
- Người 5 tạo ReceptionistDashboardActivity → Update navigation

---

## 📝 NOTES

- **Password hashing:** Hiện tại đang dùng simple hash `HASH_` + password. Production nên dùng BCrypt.
- **Remember Me:** Data lưu trong SharedPreferences, có thể bị clear khi uninstall app.
- **Auto-login:** Check trong `onCreate()` của LoginActivity.
- **Role-based navigation:** Hiện tại tạm navigate đến MainActivity, sau sẽ update khi dashboard activities được tạo.

---

**Test completed:** [ ]  
**Bugs found:** [ ]  
**Issues:** [ ]

---

Chúc bạn test thành công! 🚀

