# Database Migration Guide

## Vấn đề: Data too long for column

Nếu bạn gặp lỗi "Data too long for column", cần thay đổi kiểu dữ liệu của các cột từ VARCHAR sang TEXT.

## Giải pháp

### Cách 1: Xóa database và tạo lại (Chỉ dùng cho development)

1. Xóa database hiện tại:
```sql
DROP DATABASE englishsupporter;
CREATE DATABASE englishsupporter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Khởi động lại ứng dụng - Hibernate sẽ tự động tạo lại schema với kiểu TEXT.

### Cách 2: Chạy migration script (Giữ lại dữ liệu)

1. Kết nối đến MariaDB:
```bash
mysql -u root -p englishsupporter
```

2. Chạy script migration:
```sql
-- Alter meanings table
ALTER TABLE meanings MODIFY COLUMN meaning TEXT NOT NULL;

-- Alter engdefs table
ALTER TABLE engdefs MODIFY COLUMN definition TEXT NOT NULL;

-- Alter examples table
ALTER TABLE examples MODIFY COLUMN example TEXT NOT NULL;

-- Alter categories table
ALTER TABLE categories MODIFY COLUMN category VARCHAR(500) NOT NULL;

-- Alter words table
ALTER TABLE words MODIFY COLUMN text VARCHAR(255) NOT NULL;
ALTER TABLE words MODIFY COLUMN type VARCHAR(100) NOT NULL;
ALTER TABLE words MODIFY COLUMN pronunciation VARCHAR(255) NOT NULL;
```

Hoặc chạy file script:
```bash
mysql -u root -p englishsupporter < src/main/resources/db/migration/alter_columns_to_text.sql
```

### Cách 3: Sử dụng Flyway hoặc Liquibase (Recommended cho production)

Nếu bạn muốn quản lý migration tốt hơn, có thể thêm Flyway hoặc Liquibase vào project.

## Kiểm tra sau khi migration

```sql
-- Kiểm tra kiểu dữ liệu của các cột
DESCRIBE meanings;
DESCRIBE engdefs;
DESCRIBE examples;
DESCRIBE categories;
DESCRIBE words;
```

Các cột `meaning`, `definition`, `example` nên có kiểu `text`, không phải `varchar`.

