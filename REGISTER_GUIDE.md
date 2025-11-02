# Hướng Dẫn Sử Dụng Chức Năng Đăng Ký

## 📋 Tổng Quan

Chức năng đăng ký đã được tạo hoàn chỉnh cho ứng dụng Hotel Manager. Người dùng có thể tạo tài khoản mới với các vai trò khác nhau (Guest, Receptionist, Manager).

## 🎯 Các File Đã Tạo/Cập Nhật

### 1. Layout XML
- **File**: `app/src/main/res/layout/activity_register.xml`
- **Mô tả**: Giao diện đăng ký với Material Design
- **Các trường nhập liệu**:
  - Họ và tên (Full Name)
  - Email
  - Số điện thoại (Phone Number)
  - Mật khẩu (Password)
  - Xác nhận mật khẩu (Confirm Password)
  - Thông báo: Đăng ký với vai trò Khách hàng
  - Điều khoản và điều kiện (Terms Checkbox)
  
> **Lưu ý**: Vai trò luôn là GUEST (Khách hàng). Tài khoản Receptionist/Manager do Manager tạo.

### 2. Activity Java
- **File**: `app/src/main/java/com/example/projectprmt5/RegisterActivity.java`
- **Chức năng**:
  - Validation đầy đủ cho tất cả các trường
  - Kiểm tra email đã tồn tại
  - Hash password (sử dụng cùng phương thức với Login)
  - Tích hợp với UserRepository
  - Xử lý đăng ký bất đồng bộ

### 3. Strings Resources
- **File**: `app/src/main/res/values/strings.xml`
- **Đã thêm**:
  - `confirm_password`: Xác nhận mật khẩu
  - `register_success`: Đăng ký thành công
  - `register_failed`: Đăng ký thất bại
  - `register_subtitle`: Tạo tài khoản mới
  - `already_have_account`: Đã có tài khoản?
  - `agree_terms`: Tôi đồng ý với điều khoản
  - `name_too_short`: Tên phải có ít nhất 2 ký tự
  - `passwords_not_match`: Mật khẩu không khớp
  - `must_agree_terms`: Bạn phải đồng ý với điều khoản
  - `email_already_exists`: Email đã tồn tại

### 4. Manifest
- **File**: `app/src/main/AndroidManifest.xml`
- **Đã thêm**: RegisterActivity với screenOrientation portrait

### 5. LoginActivity
- **File**: `app/src/main/java/com/example/projectprmt5/LoginActivity.java`
- **Đã cập nhật**: Click vào "Đăng ký" sẽ chuyển đến RegisterActivity

## 🚀 Cách Sử Dụng

### Bước 1: Sync Project
```bash
# Trong Android Studio, click:
File > Sync Project with Gradle Files
```

### Bước 2: Build & Run
```bash
# Build project
./gradlew build

# Hoặc click vào nút Run trong Android Studio
```

### Bước 3: Test Đăng Ký
1. Mở app và click vào link "Đăng ký" ở màn hình Login
2. Điền thông tin:
   - **Họ và tên**: Nguyễn Văn A (ít nhất 2 ký tự)
   - **Email**: test@example.com (định dạng email hợp lệ)
   - **Số điện thoại**: 0901234567 (ít nhất 10 số)
   - **Mật khẩu**: Test123! (ít nhất 6 ký tự)
   - **Xác nhận mật khẩu**: Test123! (phải khớp với mật khẩu)
   - **Vai trò**: Tự động = GUEST (Khách hàng)
   - **Điều khoản**: Tích vào checkbox
3. Click "Đăng ký"
4. Nếu thành công, sẽ quay về màn hình Login

## ✅ Validation Rules

### Họ và Tên (Full Name)
- ✔️ Bắt buộc
- ✔️ Ít nhất 2 ký tự

### Email
- ✔️ Bắt buộc
- ✔️ Định dạng email hợp lệ (sử dụng Patterns.EMAIL_ADDRESS)
- ✔️ Không được trùng với email đã tồn tại trong database

### Số Điện Thoại
- ✔️ Bắt buộc
- ✔️ Ít nhất 10 số

### Mật Khẩu
- ✔️ Bắt buộc
- ✔️ Ít nhất 6 ký tự
- ✔️ Mật khẩu và xác nhận mật khẩu phải khớp

### Điều Khoản
- ✔️ Phải tích vào checkbox để đồng ý

## 🔐 Bảo Mật

### Password Hashing
- Sử dụng cùng phương thức hash với Login: `"HASH_" + password`
- **Lưu ý**: Đây là phương thức đơn giản cho demo
- **Production**: Nên sử dụng BCrypt hoặc Argon2

### Email Unique Constraint
- Database có unique index trên cột email
- Repository kiểm tra email tồn tại trước khi đăng ký

## 🎨 Giao Diện

### Design Pattern
- Material Design Components
- TextInputLayout với icons
- Dropdown cho role selection
- ScrollView để hỗ trợ màn hình nhỏ
- Card elevation và corner radius

### Colors
- Sử dụng theme colors từ `values/colors.xml`
- Primary color cho links và buttons
- Background color từ theme

## 🔄 Flow Diagram

```
[Login Screen]
       |
       | Click "Đăng ký"
       ↓
[Register Screen]
       |
       | Fill form & Submit
       ↓
[Validate Input] ----❌ Invalid---→ Show Error
       |
       | ✅ Valid
       ↓
[Check Email Exists] ----❌ Exists---→ Show "Email đã tồn tại"
       |
       | ✅ Not Exists
       ↓
[Create User] ----❌ Failed---→ Show Error
       |
       | ✅ Success
       ↓
[Show Success Toast]
       |
       ↓
[Back to Login Screen]
```

## 📱 Tài Khoản Test

Sau khi đăng ký, bạn có thể đăng nhập với:
- **Email**: test@example.com
- **Password**: Test123!
- **Role**: GUEST (hoặc role bạn đã chọn)

## 🐛 Troubleshooting

### Lỗi: "Email already exists"
- **Nguyên nhân**: Email đã được đăng ký
- **Giải pháp**: Sử dụng email khác hoặc xóa user cũ từ database

### Lỗi: "RegisterActivity not found"
- **Nguyên nhân**: Project chưa sync
- **Giải pháp**: Sync Project with Gradle Files

### Validation Errors
- Đọc kỹ thông báo lỗi trên từng trường
- Đảm bảo tất cả trường đều hợp lệ

## 📝 Code Examples

### Đăng Ký User
```java
userRepository.registerUser(
    "test@example.com",     // email
    "Test123!",             // password (sẽ được hash)
    "Nguyễn Văn A",         // fullName
    User.Role.GUEST,        // role
    "0901234567"           // phoneNumber
);
```

### Kiểm Tra Email Tồn Tại
```java
Boolean emailExists = userRepository.checkEmailExists("test@example.com").get();
if (emailExists) {
    // Email đã tồn tại
}
```

## 🎯 Tích Hợp

### UserRepository
- Sử dụng method `registerUser()` đã có sẵn
- Method tự động:
  - Hash password
  - Kiểm tra email tồn tại
  - Tạo User entity
  - Insert vào database

### Database
- User entity có các trường:
  - userId (auto-generated)
  - email (unique)
  - passwordHash
  - fullName
  - phoneNumber
  - role
  - isActive (default: true)
  - createdAt (auto: current date)

## 📚 Next Steps

### Tính Năng Có Thể Thêm
1. **Email Verification**: Gửi email xác thực
2. **Phone Verification**: OTP via SMS
3. **Social Login**: Google, Facebook
4. **Stronger Password**: Yêu cầu uppercase, số, ký tự đặc biệt
5. **Terms & Conditions Page**: Link đến trang điều khoản chi tiết
6. **Profile Picture**: Upload ảnh đại diện khi đăng ký
7. **Address Field**: Thêm trường địa chỉ chi tiết

### Cải Thiện Bảo Mật
1. Implement BCrypt hoặc Argon2 cho password hashing
2. Add CAPTCHA để chống bot
3. Rate limiting để chống spam registration
4. Email verification bắt buộc trước khi active account

## ✅ Checklist

- [x] Layout XML đã tạo
- [x] Activity Java đã tạo
- [x] Strings resources đã cập nhật
- [x] Manifest đã đăng ký Activity
- [x] LoginActivity đã liên kết với RegisterActivity
- [x] Validation đầy đủ
- [x] Email uniqueness check
- [x] Password hashing
- [x] Error handling
- [x] Success feedback

## 🎉 Kết Luận

Chức năng đăng ký đã hoàn thành và sẵn sàng sử dụng! Người dùng có thể:
- Tạo tài khoản mới
- Chọn vai trò (Guest/Receptionist/Manager)
- Đăng nhập sau khi đăng ký thành công

**Ready to test! 🚀**

