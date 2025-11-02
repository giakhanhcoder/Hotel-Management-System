# 📚 HƯỚNG DẪN GIT WORKFLOW CHO TEAM

## ✅ QUY TRÌNH ĐÚNG - STEP BY STEP

### 🔄 **BƯỚC 1: Pull code mới nhất từ master**
```bash
git checkout master           # Chuyển về master
git pull origin master        # Lấy code mới nhất từ remote
```

### 🌿 **BƯỚC 2: Tạo branch mới từ master**
```bash
git checkout -b tien         # Tạo và chuyển sang branch "tien"
```
**Lưu ý:** Branch name nên có format rõ ràng hơn:
- `feature/tien-authentication` (nếu làm feature)
- `fix/tien-login-bug` (nếu fix bug)
- Hoặc giữ `tien` nếu team quy ước dùng tên

### 💻 **BƯỚC 3: Code xong thì commit**
```bash
# Xem thay đổi
git status

# Add files cần commit
git add app/src/main/java/com/example/projectprmt5/*.java
git add app/src/main/res/layout/*.xml
# hoặc add tất cả
git add .

# Commit với message rõ ràng
git commit -m "feat: Complete authentication screens by tien"
```

### 🚀 **BƯỚC 4: Push branch lên remote**
```bash
git push -u origin tien       # Lần đầu push (set upstream)
```

**Lần sau chỉ cần:**
```bash
git push                     # Push lên branch "tien"
```

---

## 🎯 QUY TRÌNH HOÀN CHỈNH (COPY PASTE)

```bash
# 1. Chuyển về master và pull mới nhất
git checkout master
git pull origin master

# 2. Tạo branch mới
git checkout -b tien

# 3. Code... code... code...
# (Làm việc trên branch tien)

# 4. Khi code xong, commit
git add .
git commit -m "feat: Add authentication screens by tien"

# 5. Push branch lên remote
git push -u origin tien
```

---

## ✅ QUY TRÌNH CỦA BẠN - ĐÚNG NHƯNG THIẾU VÀI BƯỚC

**Bạn bảo:**
1. ✅ Pull code mới nhất từ master → **ĐÚNG**
2. ✅ `git checkout -b tien` → **ĐÚNG**
3. ✅ Code xong push → **THIẾU: Cần commit trước khi push**

**Workflow đúng đầy đủ:**
```
1. git checkout master
2. git pull origin master
3. git checkout -b tien
4. [CODE...]
5. git add .
6. git commit -m "message"
7. git push -u origin tien
```

---

## 🔀 KHI PUSH XONG - LÀM GÌ TIẾP?

### **Option 1: Merge vào master trực tiếp (nếu bạn là người chủ dự án)**
```bash
# Trên GitHub: Tạo Pull Request
# Hoặc merge local:
git checkout master
git merge tien
git push origin master
```

### **Option 2: Để team review (khuyến nghị)**
1. Push branch `tien` lên GitHub
2. Tạo Pull Request trên GitHub
3. Team review và merge vào master

---

## 🚨 LƯU Ý QUAN TRỌNG

### ✅ **LUÔN:**
- Pull master trước khi tạo branch mới
- Commit với message rõ ràng
- Push branch riêng, KHÔNG push trực tiếp lên master

### ❌ **KHÔNG NÊN:**
- Code trực tiếp trên master
- Push trực tiếp lên master (trừ khi bạn merge từ branch)
- Quên commit trước khi push

---

## 📝 VÍ DỤ CỤ THỂ CHO PHẦN AUTHENTICATION

```bash
# Bước 1: Pull master mới nhất
git checkout master
git pull origin master

# Bước 2: Tạo branch
git checkout -b tien-authentication

# Bước 3: Code (đã code xong)
# Giờ commit:

# Bước 4: Add files
git add app/src/main/java/com/example/projectprmt5/SplashActivity.java
git add app/src/main/java/com/example/projectprmt5/WelcomeActivity.java
git add app/src/main/java/com/example/projectprmt5/LoginActivity.java
git add app/src/main/java/com/example/projectprmt5/RegisterActivity.java
git add app/src/main/java/com/example/projectprmt5/ForgotPasswordActivity.java
git add app/src/main/java/com/example/projectprmt5/ProfileActivity.java
git add app/src/main/java/com/example/projectprmt5/ChangePasswordActivity.java
git add app/src/main/res/layout/activity_*.xml
git add app/src/main/res/layout/item_welcome_slide.xml
git add app/src/main/AndroidManifest.xml
git add app/src/main/res/values/dimens.xml

# Hoặc đơn giản:
git add .

# Bước 5: Commit
git commit -m "feat: Complete 7 authentication screens with 8+ data items by tien

- Add SplashActivity with statistics
- Add WelcomeActivity with onboarding
- Add ForgotPasswordActivity
- Add ProfileActivity with address and dates
- Add ChangePasswordActivity
- Update LoginActivity and RegisterActivity navigation
- All screens have 8+ data items as required"

# Bước 6: Push
git push -u origin tien-authentication
```

---

## 🎯 SUMMARY

**Quy trình của bạn BẠN ĐÚNG nhưng thiếu COMMIT:**

```bash
# Đúng:
git checkout master
git pull origin master
git checkout -b tien

# Thiếu:
git add .                    # ← Cần add files
git commit -m "message"      # ← Cần commit

# Đúng:
git push -u origin tien
```

**QUY TRÌNH ĐẦY ĐỦ:**
```
master → pull → checkout -b tien → CODE → add → commit → push
```

---

## 💡 TIP: Tạo script tự động

Có thể tạo file `git-workflow.sh` để tự động hóa:

```bash
#!/bin/bash
# Tạo branch mới và setup
git checkout master
git pull origin master
git checkout -b $1
echo "✅ Đã tạo branch $1, sẵn sàng code!"
```

**Chạy:** `./git-workflow.sh tien`

---

**Tóm lại: Quy trình của bạn ĐÚNG nhưng nhớ COMMIT trước khi PUSH!** ✅


