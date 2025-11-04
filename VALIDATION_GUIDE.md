# Hướng Dẫn Validation Form Đăng Ký

## 🎯 Tổng Quan

Form đăng ký đã được cải thiện với **validation chặt chẽ** và **thông báo lỗi rõ ràng** cho từng trường. Validation được thực hiện ở cả **thời gian thực** (khi gõ) và **khi submit**.

## ✅ Các Quy Tắc Validation

### 1. Họ và Tên (Full Name)

#### Quy tắc:
- ✔️ **Bắt buộc**: Không được để trống
- ✔️ **Độ dài**: 2-50 ký tự
- ✔️ **Ký tự hợp lệ**: Chỉ chữ cái (có dấu tiếng Việt) và khoảng trắng
- ❌ **Không hợp lệ**: Chữ số, ký tự đặc biệt (@, #, $, ...)

#### Ví dụ:
```
✅ Hợp lệ:
- Nguyễn Văn A
- Trần Thị Bích Ngọc
- Lê Hoàng

❌ Không hợp lệ:
- A (quá ngắn, < 2 ký tự)
- Nguyen123 (chứa số)
- User@123 (chứa ký tự đặc biệt)
- [Tên quá 50 ký tự]
```

#### Regex Pattern:
```java
Pattern NAME_PATTERN = Pattern.compile(
    "^[a-zA-ZàáảãạâầấẩẫậăằắẳẵặèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵđÀÁẢÃẠÂẦẤẨẪẬĂẰẮẲẴẶÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴĐ\\s]+$"
);
```

#### Thông báo lỗi:
- "Trường này bắt buộc"
- "Tên phải có ít nhất 2 ký tự"
- "Tên không được quá 50 ký tự"
- "Tên chỉ được chứa chữ cái và khoảng trắng"

---

### 2. Email

#### Quy tắc:
- ✔️ **Bắt buộc**: Không được để trống
- ✔️ **Format**: Phải đúng định dạng email (user@domain.com)
- ✔️ **Độ dài**: Tối đa 100 ký tự
- ✔️ **Unique**: Không được trùng với email đã tồn tại

#### Ví dụ:
```
✅ Hợp lệ:
- user@example.com
- nguyen.van.a@gmail.com
- contact@company.vn

❌ Không hợp lệ:
- user (thiếu @domain)
- @example.com (thiếu username)
- user@.com (thiếu domain)
- user @example.com (có khoảng trắng)
```

#### Validation:
```java
Patterns.EMAIL_ADDRESS.matcher(email).matches()
```

#### Thông báo lỗi:
- "Trường này bắt buộc"
- "Email không hợp lệ (vd: example@email.com)"
- "Email không được quá 100 ký tự"
- "Email đã tồn tại trong hệ thống"

---

### 3. Số Điện Thoại (Phone Number)

#### Quy tắc:
- ✔️ **Bắt buộc**: Không được để trống
- ✔️ **Format Việt Nam**:
  - Bắt đầu bằng `0` hoặc `+84`
  - Theo sau là 9 số
  - Tổng: 10-11 số
- ✔️ **Cho phép**: Khoảng trắng và dấu chấm (sẽ tự động loại bỏ khi validate)

#### Ví dụ:
```
✅ Hợp lệ:
- 0901234567
- +84901234567
- 090 123 4567
- 090.123.4567

❌ Không hợp lệ:
- 123456 (quá ngắn)
- 12345678901 (không bắt đầu bằng 0 hoặc +84)
- 0abc123456 (chứa chữ)
```

#### Regex Pattern:
```java
Pattern PHONE_PATTERN = Pattern.compile(
    "^(0|\\+84)(\\s|\\.)?([0-9]{9})$"
);
```

#### Helper Text:
"VD: 0901234567 hoặc +84901234567"

#### Thông báo lỗi:
- "Trường này bắt buộc"
- "SĐT phải bắt đầu bằng 0 hoặc +84 và có 10-11 số"

---

### 4. Mật Khẩu (Password)

#### Quy tắc:
- ✔️ **Bắt buộc**: Không được để trống
- ✔️ **Độ dài**: 6-50 ký tự
- ✔️ **Độ mạnh**: Phải chứa cả chữ cái VÀ số
- ✔️ **Ký tự cho phép**: Chữ, số, và ký tự đặc biệt (@$!%*#?&)
- ❌ **Không cho phép**: Khoảng trắng

#### Ví dụ:
```
✅ Hợp lệ:
- Test123
- Pass@2024
- Secure#456
- MyPassword1

❌ Không hợp lệ:
- test (thiếu số)
- 123456 (thiếu chữ)
- ab123 (quá ngắn, < 6 ký tự)
- Pass 123 (có khoảng trắng)
```

#### Regex Pattern:
```java
Pattern PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$"
);
```
- `(?=.*[A-Za-z])`: Phải có ít nhất 1 chữ cái
- `(?=.*\\d)`: Phải có ít nhất 1 số
- `[A-Za-z\\d@$!%*#?&]{6,}`: 6+ ký tự từ bộ cho phép

#### Helper Text:
"Ít nhất 6 ký tự, có chữ và số"

#### Thông báo lỗi:
- "Trường này bắt buộc"
- "Mật khẩu phải có ít nhất 6 ký tự"
- "Mật khẩu không được quá 50 ký tự"
- "Mật khẩu phải chứa cả chữ và số"
- "Mật khẩu không được chứa khoảng trắng"

---

### 5. Xác Nhận Mật Khẩu (Confirm Password)

#### Quy tắc:
- ✔️ **Bắt buộc**: Không được để trống
- ✔️ **Khớp**: Phải giống hệt với mật khẩu

#### Thông báo lỗi:
- "Trường này bắt buộc"
- "Mật khẩu xác nhận không khớp"

---

### 6. Điều Khoản (Terms & Conditions)

#### Quy tắc:
- ✔️ **Bắt buộc**: Phải tích checkbox

#### Thông báo lỗi:
- Toast: "Bạn phải đồng ý với điều khoản và điều kiện"

---

## 🎨 UI/UX Features

### 1. Real-time Validation
Lỗi sẽ **tự động biến mất** khi người dùng bắt đầu sửa:

```java
etFullName.addTextChangedListener(new TextWatcher() {
    @Override
    public void onTextChanged(...) {
        tilFullName.setError(null);  // Clear error
        tilFullName.setErrorEnabled(false);
    }
});
```

### 2. Helper Text
Hiển thị gợi ý **bên dưới** trường nhập:

```xml
<TextInputLayout
    app:helperText="VD: 0901234567 hoặc +84901234567"
    app:helperTextEnabled="true">
```

**Hiển thị cho**:
- Số điện thoại: "VD: 0901234567 hoặc +84901234567"
- Mật khẩu: "Ít nhất 6 ký tự, có chữ và số"

### 3. Error Display
Lỗi hiển thị **màu đỏ** ngay bên dưới trường:

```xml
<TextInputLayout
    app:errorEnabled="true">
```

### 4. Focus Management
Tự động focus vào **trường lỗi đầu tiên**:

```java
if (isValid) etFullName.requestFocus();
```

---

## 🔄 Flow Validation

### 1. Khi Người Dùng Nhập (Real-time)
```
User types → Clear previous error → Show helper text
```

### 2. Khi Submit Form
```
Click "Đăng ký" 
    ↓
Clear all errors
    ↓
Validate từng trường (Name → Email → Phone → Password → Confirm → Terms)
    ↓
Có lỗi? → Show error + Focus vào field đầu tiên
    ↓
Không lỗi? → Check email exists
    ↓
Email tồn tại? → Show error
    ↓
OK? → Submit to database
```

---

## 💻 Code Implementation

### Validation Method
```java
private boolean validateInput(String fullName, String email, 
                             String phoneNumber, String password, 
                             String confirmPassword) {
    boolean isValid = true;
    
    // Clear all errors
    tilFullName.setError(null);
    tilEmail.setError(null);
    // ... other fields
    
    // Validate Name
    if (TextUtils.isEmpty(fullName)) {
        tilFullName.setError(getString(R.string.field_required));
        if (isValid) etFullName.requestFocus();
        isValid = false;
    } else if (fullName.trim().length() < 2) {
        tilFullName.setError(getString(R.string.name_too_short));
        if (isValid) etFullName.requestFocus();
        isValid = false;
    }
    // ... other validations
    
    return isValid;
}
```

### Real-time Validation
```java
private void setupRealtimeValidation() {
    etFullName.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, ...) {
            tilFullName.setError(null);
            tilFullName.setErrorEnabled(false);
        }
    });
    // ... other fields
}
```

---

## 📊 Test Cases

### Test Case 1: Họ Tên
| Input | Expected | Pass/Fail |
|-------|----------|-----------|
| "" | ❌ "Trường này bắt buộc" | ✅ |
| "A" | ❌ "Tên phải có ít nhất 2 ký tự" | ✅ |
| "Nguyễn Văn A" | ✅ Valid | ✅ |
| "User123" | ❌ "Tên chỉ được chứa chữ cái..." | ✅ |

### Test Case 2: Email
| Input | Expected | Pass/Fail |
|-------|----------|-----------|
| "" | ❌ "Trường này bắt buộc" | ✅ |
| "invalid" | ❌ "Email không hợp lệ" | ✅ |
| "test@example.com" | ✅ Valid | ✅ |
| "admin@hotel.com" | ❌ "Email đã tồn tại" | ✅ |

### Test Case 3: Số Điện Thoại
| Input | Expected | Pass/Fail |
|-------|----------|-----------|
| "" | ❌ "Trường này bắt buộc" | ✅ |
| "123" | ❌ "SĐT phải bắt đầu bằng..." | ✅ |
| "0901234567" | ✅ Valid | ✅ |
| "+84901234567" | ✅ Valid | ✅ |

### Test Case 4: Mật Khẩu
| Input | Expected | Pass/Fail |
|-------|----------|-----------|
| "" | ❌ "Trường này bắt buộc" | ✅ |
| "abc" | ❌ "Mật khẩu phải có ít nhất 6 ký tự" | ✅ |
| "abcdef" | ❌ "Mật khẩu phải chứa cả chữ và số" | ✅ |
| "Test123" | ✅ Valid | ✅ |
| "Pass 123" | ❌ "Mật khẩu không được chứa khoảng trắng" | ✅ |

---

## 🎯 Lợi Ích

### 1. Trải Nghiệm Người Dùng (UX)
- ✅ Thông báo lỗi **rõ ràng**, dễ hiểu
- ✅ Gợi ý format **ngay dưới trường nhập**
- ✅ Lỗi **tự động biến mất** khi sửa
- ✅ Focus **tự động** vào trường lỗi

### 2. Bảo Mật
- ✅ Password phải **đủ mạnh** (chữ + số)
- ✅ Email **không trùng** trong hệ thống
- ✅ Format **chặt chẽ** cho phone

### 3. Data Quality
- ✅ Họ tên **chuẩn hóa** (không có số/ký tự đặc biệt)
- ✅ SĐT **đúng format** Việt Nam
- ✅ Email **hợp lệ**

---

## 🚀 Cách Test

### 1. Sync Project
```bash
File > Sync Project with Gradle Files
```

### 2. Run App
```bash
./gradlew installDebug
# hoặc click Run trong Android Studio
```

### 3. Test Validation

#### Test Họ Tên:
1. Để trống → Xem lỗi "Trường này bắt buộc"
2. Gõ "A" → Xem lỗi "Tên phải có ít nhất 2 ký tự"
3. Gõ "Test123" → Xem lỗi "Tên chỉ được chứa chữ cái..."
4. Gõ "Nguyễn Văn A" → OK ✅

#### Test Email:
1. Để trống → Lỗi
2. Gõ "test" → Lỗi "Email không hợp lệ"
3. Gõ "test@example.com" → OK ✅

#### Test SĐT:
1. Để trống → Lỗi
2. Gõ "123" → Lỗi format
3. Gõ "0901234567" → OK ✅

#### Test Password:
1. Để trống → Lỗi
2. Gõ "abc" → Lỗi "quá ngắn"
3. Gõ "abcdef" → Lỗi "phải có số"
4. Gõ "Test123" → OK ✅

#### Test Confirm Password:
1. Password: "Test123"
2. Confirm: "Test456" → Lỗi "không khớp"
3. Confirm: "Test123" → OK ✅

---

## 🎉 Kết Quả

**Validation hoàn chỉnh** với:
- ✅ 6 trường được validate
- ✅ 15+ quy tắc validation
- ✅ Real-time error clearing
- ✅ Helper text hướng dẫn
- ✅ Thông báo lỗi chi tiết
- ✅ Pattern matching cho VN phone
- ✅ Password strength check
- ✅ Email uniqueness check

**Sẵn sàng production! 🚀**







