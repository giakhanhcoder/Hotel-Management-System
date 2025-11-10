# ⚡ QUICK FIX: Lỗi Login

## 🔴 Vấn đề
Đăng nhập đúng email/password nhưng báo sai!

## ✅ Nguyên nhân
Password trong database cũ khác với documentation.

## 🚀 Giải pháp (1 phút)

### Bước 1: Xóa database cũ

**Trên Emulator/Device:**
```
Settings → Apps → ProjectPRMT5 → Storage → Clear Data
```

**Hoặc dùng lệnh (nhanh hơn):**
```bash
adb shell pm clear com.example.projectprmt5
```

### Bước 2: Mở lại app

App sẽ tự động tạo database mới với password đúng!

### Bước 3: Login

```
📧 Email: admin@hotel.com
🔒 Password: Admin123!
```

## ✅ Done!

---

## 📋 Tài khoản test đầy đủ

| Role | Email | Password |
|------|-------|----------|
| 👨‍💼 Manager | `admin@hotel.com` | `Admin123!` |
| 👔 Receptionist | `receptionist@hotel.com` | `Receptionist123!` |
| 🏨 Guest | `guest@example.com` | `Guest123!` |

---

## ❓ Vẫn không được?

Xem chi tiết: `FIX_LOGIN_PASSWORD.md`












