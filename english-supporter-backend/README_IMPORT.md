# Hướng dẫn Import Từ Vựng

Có 3 cách để import danh sách từ vựng vào database:

## 1. Import qua API (JSON)

**Endpoint:** `POST /api/words/import`

**Request Body:**
```json
{
  "words": ["hello", "world", "test", "example"]
}
```

**Response:**
```json
{
  "totalWords": 4,
  "successCount": 3,
  "failedCount": 1,
  "skippedCount": 0,
  "successWords": ["hello", "world", "test"],
  "failedWords": ["example"],
  "skippedWords": []
}
```

**Example với curl:**
```bash
curl -X POST http://localhost:8082/api/words/import \
  -H "Content-Type: application/json" \
  -d '{"words": ["hello", "world", "test"]}'
```

## 2. Import qua API (Upload File)

**Endpoint:** `POST /api/words/import/file`

**Request:** multipart/form-data với field `file`

**File format:** Text file với một từ mỗi dòng
```
hello
world
test
example
```

**Example với curl:**
```bash
curl -X POST http://localhost:8082/api/words/import/file \
  -F "file=@words.txt"
```

## 3. Import tự động khi khởi động ứng dụng

### Cách 1: Sử dụng properties file

Thêm vào `application.properties`:
```properties
word.import.enabled=true
word.import.file=words.txt
```

### Cách 2: Sử dụng command line arguments

```bash
java -jar english-supporter-backend.jar \
  --word.import.enabled=true \
  --word.import.file=words.txt
```

### Cách 3: Đặt file trong thư mục hiện tại

Nếu không chỉ định file path, ứng dụng sẽ tự động tìm:
- `words.txt` trong thư mục hiện tại
- `word.csv` trong thư mục hiện tại

## Định dạng File

### Text File (.txt)
```
# Đây là comment, sẽ bị bỏ qua
hello
world
test
example
```

### CSV File (.csv)
```
word,definition,example
hello,chào,Hello world
world,thế giới,Hello world
test,kiểm tra,This is a test
```

**Lưu ý:** Chỉ cột đầu tiên (word) được sử dụng, các cột khác sẽ bị bỏ qua.

## Kết quả Import

Sau khi import, bạn sẽ nhận được thông tin:
- **totalWords**: Tổng số từ trong danh sách
- **successCount**: Số từ import thành công
- **failedCount**: Số từ import thất bại (không tìm thấy trên Cambridge Dictionary)
- **skippedCount**: Số từ đã tồn tại trong database (bị bỏ qua)
- **successWords**: Danh sách từ import thành công
- **failedWords**: Danh sách từ import thất bại
- **skippedWords**: Danh sách từ đã tồn tại

## Lưu ý

1. Tất cả từ sẽ được chuyển thành chữ thường (lowercase)
2. Từ trùng lặp trong file sẽ được xử lý (chỉ import một lần)
3. Từ đã tồn tại trong database sẽ bị bỏ qua (không scrape lại)
4. Quá trình import có thể mất thời gian vì mỗi từ cần scrape từ Cambridge Dictionary và tratu.soha.vn
5. Nên import từng batch nhỏ (50-100 từ) để tránh timeout

## Example Files

Xem file `words.txt.example` để biết định dạng mẫu.


