# Hệ Thống Quản Lý Điểm Sinh Viên (Student Management System)

## Mô tả
Hệ thống quản lý điểm sinh viên với các tính năng:
- Quản lý thông tin sinh viên
- Quản lý môn học với tỷ lệ điểm linh hoạt
- Nhập và quản lý điểm số
- Tự động tính điểm tổng kết và xác định đậu/rớt
- Custom exceptions và response format chuẩn

## Công nghệ sử dụng
- Java 17
- Spring Boot 4.0.0
- Spring Data JPA
- Spring Security (disabled for testing)
- H2 Database (in-memory)
- Lombok
- Maven

## Cài đặt và chạy

### 1. Clone project
```bash
cd d:\SDS_Practice\sms-backend
```

### 2. Build project
```bash
mvnw clean install
```

### 3. Chạy ứng dụng
```bash
mvnw spring-boot:run
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

### 4. Truy cập H2 Console
URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:studentdb`
- Username: `sa`
- Password: `password`

## Cấu trúc Database

### Bảng Students
- `student_code` (PK): Mã sinh viên
- `full_name`: Tên sinh viên
- `gender`: Giới tính
- `date_of_birth`: Ngày sinh
- `class_name`: Lớp
- `course`: Khóa học
- `email`: Email
- `phone_number`: Số điện thoại
- `address`: Địa chỉ

### Bảng Subjects
- `id` (PK): ID môn học
- `subject_code`: Mã môn học
- `subject_name`: Tên môn học
- `credit_hours`: Số tiết học
- `process_score_ratio`: Tỷ lệ điểm quá trình (0-1)
- `component_score_ratio`: Tỷ lệ điểm thành phần (0-1)
- `description`: Mô tả

### Bảng Student_Subjects
- `id` (PK): ID
- `student_code` (FK): Mã sinh viên
- `subject_id` (FK): ID môn học
- `process_score`: Điểm quá trình (0-10)
- `component_score`: Điểm thành phần (0-10)
- `final_score`: Điểm tổng kết (tự động tính)
- `status`: Trạng thái (ĐẠT/TRƯỢT)
- `semester`: Học kỳ
- `academic_year`: Năm học

## API Endpoints

### 1. Quản lý Sinh viên

#### Xem danh sách sinh viên
```http
GET /api/students
```

#### Xem chi tiết sinh viên
```http
GET /api/students/{studentCode}
```

#### Xem chi tiết đầy đủ sinh viên (bao gồm điểm và thống kê)
```http
GET /api/students/{studentCode}/detail
```

#### Tạo sinh viên mới
```http
POST /api/students
Content-Type: application/json

{
  "studentCode": "SV006",
  "fullName": "Nguyễn Văn A",
  "gender": "Nam",
  "dateOfBirth": "2003-01-15",
  "className": "CNTT-K15",
  "course": "K15",
  "email": "nguyenvana@email.com",
  "phoneNumber": "0901234567",
  "address": "Hà Nội"
}
```

#### Cập nhật sinh viên
```http
PUT /api/students/{studentCode}
Content-Type: application/json

{
  "studentCode": "SV006",
  "fullName": "Nguyễn Văn A Updated",
  "gender": "Nam",
  "dateOfBirth": "2003-01-15",
  "className": "CNTT-K16",
  "course": "K16",
  "email": "nguyenvana.updated@email.com",
  "phoneNumber": "0901234568",
  "address": "Hồ Chí Minh"
}
```

#### Xóa sinh viên
```http
DELETE /api/students/{studentCode}
```

#### Tìm kiếm sinh viên
```http
GET /api/students/search?keyword=Nguyễn
```

#### Lọc sinh viên theo lớp
```http
GET /api/students/class/{className}
```

#### Lọc sinh viên theo khóa
```http
GET /api/students/course/{course}
```

### 2. Quản lý Môn học

#### Xem danh sách môn học
```http
GET /api/subjects
```

#### Xem chi tiết môn học
```http
GET /api/subjects/{id}
```

#### Xem môn học theo mã
```http
GET /api/subjects/code/{subjectCode}
```

#### Tạo môn học mới
```http
POST /api/subjects
Content-Type: application/json

{
  "subjectCode": "JAVA101",
  "subjectName": "Lập trình Java",
  "creditHours": 60,
  "processScoreRatio": 0.3,
  "componentScoreRatio": 0.7,
  "description": "Học lập trình Java cơ bản"
}
```

**Lưu ý:** `processScoreRatio + componentScoreRatio` phải bằng 1.0 (100%)

#### Cập nhật môn học
```http
PUT /api/subjects/{id}
Content-Type: application/json

{
  "subjectCode": "JAVA101",
  "subjectName": "Lập trình Java Nâng cao",
  "creditHours": 75,
  "processScoreRatio": 0.4,
  "componentScoreRatio": 0.6,
  "description": "Học lập trình Java nâng cao"
}
```

#### Xóa môn học
```http
DELETE /api/subjects/{id}
```

#### Tìm kiếm môn học
```http
GET /api/subjects/search?keyword=Lập trình
```

### 3. Quản lý Điểm

#### Nhập điểm cho sinh viên
```http
POST /api/scores
Content-Type: application/json

{
  "studentCode": "SV001",
  "subjectId": 1,
  "processScore": 8.5,
  "componentScore": 7.5,
  "semester": "HK1",
  "academicYear": "2023-2024"
}
```

**Điểm tổng kết và trạng thái sẽ được tự động tính:**
- `finalScore = (processScore × processScoreRatio) + (componentScore × componentScoreRatio)`
- `status = finalScore >= 4.0 ? "ĐẠT" : "TRƯỢT"`

#### Cập nhật điểm
```http
PUT /api/scores/{id}
Content-Type: application/json

{
  "studentCode": "SV001",
  "subjectId": 1,
  "processScore": 9.0,
  "componentScore": 8.0,
  "semester": "HK1",
  "academicYear": "2023-2024"
}
```

#### Xóa điểm
```http
DELETE /api/scores/{id}
```

#### Xem điểm theo ID
```http
GET /api/scores/{id}
```

#### Xem tất cả điểm của sinh viên
```http
GET /api/scores/student/{studentCode}
```

#### Xem điểm của sinh viên trong một môn học
```http
GET /api/scores/student/{studentCode}/subject/{subjectId}
```

#### Xem danh sách môn ĐẠT của sinh viên
```http
GET /api/scores/student/{studentCode}/passed
```

#### Xem danh sách môn TRƯỢT của sinh viên
```http
GET /api/scores/student/{studentCode}/failed
```

#### Xem danh sách sinh viên đăng ký môn học
```http
GET /api/scores/subject/{subjectId}
```

## Response Format

Tất cả API đều trả về response theo format chuẩn:

### Success Response
```json
{
  "success": true,
  "message": "Thành công",
  "data": { ... },
  "error": null,
  "timestamp": "2024-12-01T21:44:22"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Sinh viên không tìm thấy với mã sinh viên : 'SV999'",
  "data": null,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "field": "mã sinh viên",
    "rejectedValue": "SV999",
    "details": "Sinh viên không tìm thấy với mã sinh viên : 'SV999'"
  },
  "timestamp": "2024-12-01T21:44:22"
}
```

## Custom Exceptions

Hệ thống có 3 loại custom exception:

1. **ResourceNotFoundException** (404)
   - Khi không tìm thấy resource
   
2. **DuplicateResourceException** (409)
   - Khi tạo resource đã tồn tại
   
3. **BadRequestException** (400)
   - Khi dữ liệu không hợp lệ

## Validation Rules

### Student
- `studentCode`: Bắt buộc, tối đa 20 ký tự
- `fullName`: Bắt buộc, tối đa 100 ký tự
- `gender`: Phải là "Nam", "Nữ" hoặc "Khác"
- `dateOfBirth`: Phải là ngày trong quá khứ
- `email`: Phải đúng format email
- `phoneNumber`: Phải từ 10-15 chữ số

### Subject
- `subjectCode`: Bắt buộc, tối đa 20 ký tự
- `subjectName`: Bắt buộc, tối đa 100 ký tự
- `creditHours`: Bắt buộc, từ 1-200
- `processScoreRatio`: Bắt buộc, từ 0.0-1.0
- `componentScoreRatio`: Bắt buộc, từ 0.0-1.0
- **Tổng 2 tỷ lệ phải bằng 1.0**

### Score
- `studentCode`: Bắt buộc
- `subjectId`: Bắt buộc
- `processScore`: Bắt buộc, từ 0.0-10.0
- `componentScore`: Bắt buộc, từ 0.0-10.0

## Dữ liệu mẫu

Hệ thống đã có sẵn dữ liệu mẫu:
- 5 sinh viên (SV001 - SV005)
- 6 môn học (MATH101, PHYS101, PROG101, DATA101, WEB101, DB101)
- Nhiều bản ghi điểm với cả trường hợp đạt và trượt

## Testing với Postman/Thunder Client

1. Import các endpoint trên vào Postman
2. Test các chức năng theo thứ tự:
   - Xem danh sách sinh viên
   - Xem chi tiết sinh viên
   - Xem điểm của sinh viên
   - Nhập điểm mới
   - Xem kết quả đậu/rớt

## Lưu ý
- Database là in-memory H2, dữ liệu sẽ mất khi restart ứng dụng
- Security đã được disable để test dễ dàng
- Tất cả endpoint đều có CORS enabled
- Timezone mặc định: Asia/Ho_Chi_Minh
