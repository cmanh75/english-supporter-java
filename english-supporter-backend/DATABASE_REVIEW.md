# Đánh giá Thiết kế Database - English Supporter

## 📊 Tổng quan

### Cấu trúc hiện tại:
```
Word (1) ──→ (N) EngDefinition ──→ (N) Example
  │
  ├──→ (N) Category ──→ (N) Meaning
  │
  └──→ (N) MyWord
```

## ✅ Điểm tốt

1. **Quan hệ rõ ràng**: Phân tách tốt giữa định nghĩa tiếng Anh và nghĩa tiếng Việt
2. **LAZY Loading**: Sử dụng `FetchType.LAZY` hợp lý để tránh N+1 queries
3. **Cascade Operations**: Có `CascadeType.ALL` và `orphanRemoval = true` để quản lý dữ liệu tự động
4. **Unique Constraint**: `Word.text` có unique constraint
5. **Tracking Logic**: MyWord có `lastShown` và `showCount` cho flashcard

## ⚠️ Vấn đề cần cải thiện

### 1. **Thiếu Index cho Performance**

**Vấn đề:**
- `mywords.word_id` được query thường xuyên nhưng không có index
- `mywords.last_shown` được sort trong `getFlashcards()` nhưng không có index
- `words.text` đã có unique constraint (tự động có index) ✅

**Ảnh hưởng:**
- Query `findByWordId()` sẽ chậm khi dữ liệu lớn
- Sort `last_shown` trong memory thay vì database (không hiệu quả)

**Giải pháp:**
```java
@Table(name = "mywords", indexes = {
    @Index(name = "idx_mywords_word_id", columnList = "word_id"),
    @Index(name = "idx_mywords_last_shown", columnList = "last_shown")
})
```

### 2. **Thiếu Unique Constraint cho MyWord**

**Vấn đề:**
- Hiện tại có thể tạo nhiều `MyWord` cho cùng 1 `word_id`
- Code đã check trong service nhưng không có ràng buộc ở database level

**Giải pháp:**
```java
@Table(name = "mywords", 
    uniqueConstraints = @UniqueConstraint(name = "uk_mywords_word_id", columnNames = "word_id"),
    indexes = {...}
)
```

### 3. **Thiếu Timestamp Fields (Audit Trail)**

**Vấn đề:**
- Không có `created_at`, `updated_at` để track thời gian
- Khó debug và audit

**Giải pháp:**
- Thêm `@CreatedDate` và `@LastModifiedDate` với JPA Auditing
- Hoặc thêm `LocalDateTime createdAt, updatedAt` manually

### 4. **Thiếu User Support (Multi-user)**

**Vấn đề:**
- `MyWord` không có `user_id` → chỉ support single user
- Nếu cần multi-user sau này sẽ phải refactor lớn

**Giải pháp:**
- Nếu dự định multi-user, nên thêm `User` entity và `user_id` vào `MyWord` ngay

### 5. **Performance Issue trong getFlashcards()**

**Vấn đề:**
```java
// Hiện tại: Load ALL vào memory rồi sort
List<MyWord> allMyWords = myWordRepository.findAll();
allMyWords.sort(...);
```

**Giải pháp:**
- Dùng query với `ORDER BY last_shown ASC NULLS FIRST LIMIT ?`
- Hoặc dùng `Pageable` với custom query

### 6. **Default Value cho showCount**

**Vấn đề:**
- Có `columnDefinition = "INTEGER DEFAULT 0"` nhưng cũng set `= 0` trong Java
- Nên dùng `@ColumnDefault` hoặc chỉ để database handle

### 7. **Thiếu Validation**

**Vấn đề:**
- `Word.type` và `Word.pronunciation` có `length` nhưng không có validation
- `Category.category` có `length = 500` nhưng không có validation

**Giải pháp:**
- Thêm `@Size`, `@NotBlank` nếu cần

## 🚀 Đề xuất Cải thiện

### Priority 1 (Quan trọng - nên làm ngay):

1. **Thêm Index cho MyWord**
2. **Thêm Unique Constraint cho MyWord.word_id**
3. **Cải thiện getFlashcards() query**

### Priority 2 (Nên làm sớm):

4. **Thêm Timestamp fields**
5. **Thêm User entity nếu cần multi-user**

### Priority 3 (Có thể làm sau):

6. **Validation annotations**
7. **Optimize default values**

## 📝 Code Examples

### Cải thiện MyWord Entity:

```java
@Entity
@Table(name = "mywords", 
    uniqueConstraints = @UniqueConstraint(
        name = "uk_mywords_word_id", 
        columnNames = "word_id"
    ),
    indexes = {
        @Index(name = "idx_mywords_word_id", columnList = "word_id"),
        @Index(name = "idx_mywords_last_shown", columnList = "last_shown")
    }
)
public class MyWord {
    // ... existing code ...
}
```

### Cải thiện MyWordRepository:

```java
@Repository
public interface MyWordRepository extends JpaRepository<MyWord, Integer> {
    Optional<MyWord> findByWordId(Integer wordId);
    
    // Thay vì load all rồi sort trong memory
    @Query("SELECT m FROM MyWord m ORDER BY m.lastShown ASC NULLS FIRST")
    List<MyWord> findAllOrderByLastShownAsc(Pageable pageable);
}
```

### Cải thiện MyWordService.getFlashcards():

```java
public List<MyWord> getFlashcards(int limit) {
    // Query với pagination và sort ở database level
    Pageable pageable = PageRequest.of(0, limit);
    List<MyWord> selected = myWordRepository.findAllOrderByLastShownAsc(pageable);
    
    // Shuffle nếu cần
    Collections.shuffle(selected);
    
    // Update tracking
    LocalDateTime now = LocalDateTime.now();
    selected.forEach(myWord -> {
        myWord.setLastShown(now);
        myWord.setShowCount((myWord.getShowCount() == null ? 0 : myWord.getShowCount()) + 1);
    });
    myWordRepository.saveAll(selected);
    
    return selected;
}
```

## 🎯 Kết luận

**Đánh giá tổng thể: 7/10**

- ✅ Cấu trúc cơ bản tốt, quan hệ rõ ràng
- ⚠️ Cần cải thiện performance và constraints
- 🚀 Nên thêm index và optimize queries ngay

Thiết kế hiện tại **ổn cho giai đoạn phát triển**, nhưng cần cải thiện trước khi scale lên production với dữ liệu lớn.

