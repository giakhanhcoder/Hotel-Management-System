# Cập Nhật: Đăng Ký Chỉ Cho Khách Hàng

## 🔄 Thay Đổi

### Trước Đây
- ❌ Người dùng có thể chọn vai trò: Guest, Receptionist, Manager
- ❌ Dropdown để chọn role

### Bây Giờ
- ✅ Chỉ đăng ký với vai trò **GUEST** (Khách hàng)
- ✅ Không còn dropdown chọn vai trò
- ✅ Vai trò Receptionist và Manager phải do Manager tạo

## 📝 Các File Đã Cập Nhật

### 1. `activity_register.xml`
**Đã xóa**:
```xml
<!-- Role Selector dropdown -->
```

**Đã thêm**:
```xml
<!-- Info message -->
<TextView 
    text="💡 Bạn sẽ được đăng ký với vai trò Khách hàng..." />
```

### 2. `RegisterActivity.java`
**Đã xóa**:
- `AutoCompleteTextView actvRole`
- `String[] roleOptions`
- `String selectedRole`
- Method `setupRoleDropdown()`

**Đã thêm**:
```java
private static final String REGISTER_ROLE = User.Role.GUEST;
```

**Đã cập nhật**:
- `registerUser()` luôn dùng `REGISTER_ROLE` (GUEST)
- Comments giải thích: "Only Manager can create Receptionist/Manager accounts"

### 3. `strings.xml`
**Đã thêm**:
```xml
<string name="register_as_guest_info">
    💡 Bạn sẽ được đăng ký với vai trò Khách hàng. 
    Tài khoản nhân viên do Quản lý tạo.
</string>
```

## 🎯 Logic Mới

### Đăng Ký Công Khai (Public Registration)
```
User fills form
    → Always registered as GUEST
    → Can login and access guest features
```

### Tạo Tài Khoản Nhân Viên (Staff Account Creation)
```
Manager Dashboard
    → User Management
    → Create Receptionist/Manager accounts
    → Set credentials
    → Assign permissions
```

## 📱 Màn Hình Đăng Ký Mới

```
┌─────────────────────────────┐
│      Hotel Manager          │
├─────────────────────────────┤
│    Đăng ký                  │
│    Tạo tài khoản mới        │
│                             │
│  👤 Họ và tên               │
│  ┌───────────────────────┐  │
│  │ Nguyễn Văn A          │  │
│  └───────────────────────┘  │
│                             │
│  📧 Email                   │
│  ┌───────────────────────┐  │
│  │ test@example.com      │  │
│  └───────────────────────┘  │
│                             │
│  📱 Số điện thoại           │
│  ┌───────────────────────┐  │
│  │ 0901234567            │  │
│  └───────────────────────┘  │
│                             │
│  🔒 Mật khẩu                │
│  ┌───────────────────────┐  │
│  │ ••••••••              │  │
│  └───────────────────────┘  │
│                             │
│  🔒 Xác nhận mật khẩu        │
│  ┌───────────────────────┐  │
│  │ ••••••••              │  │
│  └───────────────────────┘  │
│                             │
│  💡 Bạn sẽ được đăng ký với │
│     vai trò Khách hàng.     │
│     Tài khoản nhân viên do  │
│     Quản lý tạo.            │
│                             │
│  ☑️ Tôi đồng ý với điều     │
│     khoản và điều kiện      │
│                             │
│  ┌───────────────────────┐  │
│  │     ĐĂNG KÝ           │  │
│  └───────────────────────┘  │
│                             │
│  Đã có tài khoản? Đăng nhập │
└─────────────────────────────┘
```

## ✅ Lợi Ích

### 1. Bảo Mật Tốt Hơn
- ❌ Không ai có thể tự đăng ký làm Manager
- ✅ Chỉ Manager mới tạo được tài khoản nhân viên
- ✅ Kiểm soát quyền truy cập chặt chẽ

### 2. Phù Hợp Thực Tế
- ✅ Khách hàng tự đăng ký để đặt phòng
- ✅ Nhân viên được tuyển dụng và cấp tài khoản
- ✅ Đúng với quy trình khách sạn thực tế

### 3. Đơn Giản Hơn
- ✅ Ít field hơn → UX tốt hơn
- ✅ Không cần chọn role → Nhanh hơn
- ✅ Rõ ràng mục đích: đăng ký khách hàng

## 🔐 Quản Lý Vai Trò

### GUEST (Khách hàng)
- 📝 **Cách tạo**: Tự đăng ký qua RegisterActivity
- 🎯 **Quyền**: Xem phòng, đặt phòng, thanh toán, đánh giá

### RECEPTIONIST (Lễ tân)
- 📝 **Cách tạo**: Manager tạo trong ManagerDashboard
- 🎯 **Quyền**: Check-in, check-out, quản lý booking

### MANAGER (Quản lý)
- 📝 **Cách tạo**: Manager hiện tại tạo Manager mới
- 🎯 **Quyền**: Full access, quản lý user, báo cáo

## 🚀 Next Steps

### Cần Phát Triển: Manager User Management
```java
// TODO: Tạo màn hình quản lý user cho Manager
public class UserManagementActivity extends AppCompatActivity {
    
    // Tạo tài khoản Receptionist
    private void createReceptionist(String email, String password, 
                                   String fullName, String phone) {
        userRepository.registerUser(
            email, password, fullName, 
            User.Role.RECEPTIONIST,  // Manager có thể set role này
            phone
        );
    }
    
    // Tạo tài khoản Manager
    private void createManager(String email, String password,
                              String fullName, String phone) {
        userRepository.registerUser(
            email, password, fullName,
            User.Role.MANAGER,  // Chỉ Manager mới tạo được
            phone
        );
    }
    
    // Xem danh sách user
    // Chỉnh sửa user
    // Vô hiệu hóa/Kích hoạt user
}
```

## 📊 Flow Diagram

```
PUBLIC REGISTRATION (Đăng ký công khai)
═══════════════════════════════════════
    [Guest visits app]
           ↓
    [Click "Đăng ký"]
           ↓
    [Fill registration form]
           ↓
    [Submit] → Role = GUEST (auto)
           ↓
    [Account created]
           ↓
    [Login as GUEST]
           ↓
    [Access guest features]


STAFF ACCOUNT CREATION (Tạo tài khoản nhân viên)
═════════════════════════════════════════════════
    [Manager logs in]
           ↓
    [Manager Dashboard]
           ↓
    [User Management]
           ↓
    [Create New User]
           ↓
    [Select Role: Receptionist/Manager]
           ↓
    [Fill user info]
           ↓
    [Account created]
           ↓
    [Credentials sent to staff]
           ↓
    [Staff logs in]
```

## 🎉 Kết Quả

### Trước
- Form đăng ký có dropdown chọn role
- Ai cũng có thể chọn Manager (không an toàn)

### Sau
- ✅ Form đăng ký đơn giản hơn
- ✅ Tự động đăng ký làm Khách hàng
- ✅ Có thông báo rõ ràng về vai trò
- ✅ Bảo mật tốt hơn
- ✅ Phù hợp với thực tế

**Hoàn thành cập nhật! 🎊**
















