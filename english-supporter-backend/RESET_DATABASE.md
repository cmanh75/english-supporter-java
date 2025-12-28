# Hướng dẫn Reset Database

## Cách 1: Sử dụng Script SQL (Recommended)

### Windows:
```bash
reset_db.bat
```

### Linux/macOS:
```bash
chmod +x reset_db.sh
./reset_db.sh
```

### Hoặc chạy trực tiếp:
```bash
mysql -u root -p < src/main/resources/db/migration/reset_database.sql
```

## Cách 2: Chạy SQL trực tiếp trong MySQL

1. Kết nối đến MySQL:
```bash
mysql -u root -p
```

2. Chạy các lệnh:
```sql
DROP DATABASE IF EXISTS englishsupporter;
CREATE DATABASE englishsupporter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Thoát MySQL:
```sql
EXIT;
```

## Cách 3: Sử dụng Hibernate auto-create

1. Đảm bảo trong `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=create
```

2. Khởi động ứng dụng - Hibernate sẽ tự động:
   - Xóa tất cả bảng cũ
   - Tạo lại schema mới với kiểu TEXT đúng

3. Sau khi chạy xong, đổi lại thành:
```properties
spring.jpa.hibernate.ddl-auto=update
```

## Lưu ý

⚠️ **CẢNH BÁO**: Tất cả dữ liệu sẽ bị xóa!

- Backup database trước nếu có dữ liệu quan trọng
- Chỉ dùng trong môi trường development
- Không dùng trong production

## Sau khi reset

1. Khởi động lại ứng dụng
2. Database sẽ được tạo lại với schema mới
3. Các cột `meaning`, `definition`, `example` sẽ là kiểu TEXT
4. Có thể import lại từ vựng từ file `words.txt`

