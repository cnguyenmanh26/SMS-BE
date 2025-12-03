# API Testing Guide - Student Management System

## Base URL
```
http://localhost:8080
```

## 1. Test Xem danh sách sinh viên

### Request
```bash
curl -X GET http://localhost:8080/api/students
```

### Expected Response
```json
{
  "success": true,
  "message": "Thành công",
  "data": [
    {
      "studentCode": "SV001",
      "fullName": "Nguyễn Văn An",
      "gender": "Nam",
      "dateOfBirth": "15/05/2003",
      "className": "CNTT-K15",
      "course": "K15",
      "email": "nguyenvanan@email.com",
      "phoneNumber": "0901234567",
      "address": "Hà Nội",
      "totalSubjectsRegistered": 4
    }
    // ... more students
  ],
  "error": null,
  "timestamp": "2024-12-01T21:44:22"
}
```

## 2. Test Xem chi tiết sinh viên

### Request
```bash
curl -X GET http://localhost:8080/api/students/SV001/detail
```

### Expected Response
```json
{
  "success": true,
  "message": "Thành công",
  "data": {
    "studentCode": "SV001",
    "fullName": "Nguyễn Văn An",
    "gender": "Nam",
    "dateOfBirth": "15/05/2003",
    "className": "CNTT-K15",
    "course": "K15",
    "email": "nguyenvanan@email.com",
    "phoneNumber": "0901234567",
    "address": "Hà Nội",
    "totalSubjectsRegistered": 4,
    "totalSubjectsPassed": 4,
    "totalSubjectsFailed": 0,
    "averageScore": 7.73,
    "scores": [
      {
        "id": 1,
        "studentCode": "SV001",
        "studentName": "Nguyễn Văn An",
        "subjectId": 1,
        "subjectCode": "MATH101",
        "subjectName": "Toán cao cấp 1",
        "processScore": 8.5,
        "componentScore": 7.5,
        "finalScore": 7.8,
        "status": "ĐẠT",
        "semester": "HK1",
        "academicYear": "2023-2024"
      }
      // ... more scores
    ]
  }
}
```

## 3. Test Xem số môn học sinh viên đăng ký

### Request
```bash
curl -X GET http://localhost:8080/api/scores/student/SV001
```

## 4. Test Xem điểm môn học của sinh viên

### Request - Xem tất cả điểm
```bash
curl -X GET http://localhost:8080/api/scores/student/SV001
```

### Request - Xem điểm một môn cụ thể
```bash
curl -X GET http://localhost:8080/api/scores/student/SV001/subject/1
```

## 5. Test Nhập điểm của sinh viên

### Request
```bash
curl -X POST http://localhost:8080/api/scores \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "SV001",
    "subjectId": 5,
    "processScore": 8.0,
    "componentScore": 7.5,
    "semester": "HK2",
    "academicYear": "2023-2024"
  }'
```

### Expected Response
```json
{
  "success": true,
  "message": "Nhập điểm thành công",
  "data": {
    "id": 21,
    "studentCode": "SV001",
    "studentName": "Nguyễn Văn An",
    "subjectId": 5,
    "subjectCode": "WEB101",
    "subjectName": "Lập trình Web",
    "processScore": 8.0,
    "componentScore": 7.5,
    "finalScore": 7.65,
    "status": "ĐẠT",
    "semester": "HK2",
    "academicYear": "2023-2024"
  }
}
```

**Lưu ý:** 
- Điểm tổng kết được tính tự động: `finalScore = (8.0 × 0.3) + (7.5 × 0.7) = 7.65`
- Trạng thái tự động: `finalScore >= 4.0` → "ĐẠT"

## 6. Test Xem kết quả trượt đỗ của sinh viên

### Request - Xem môn ĐẠT
```bash
curl -X GET http://localhost:8080/api/scores/student/SV001/passed
```

### Request - Xem môn TRƯỢT
```bash
curl -X GET http://localhost:8080/api/scores/student/SV002/failed
```

### Expected Response (Môn trượt)
```json
{
  "success": true,
  "message": "Danh sách môn trượt",
  "data": [
    {
      "id": 5,
      "studentCode": "SV002",
      "studentName": "Trần Thị Bình",
      "subjectId": 1,
      "subjectCode": "MATH101",
      "subjectName": "Toán cao cấp 1",
      "processScore": 5.0,
      "componentScore": 4.5,
      "finalScore": 3.65,
      "status": "TRƯỢT",
      "semester": "HK1",
      "academicYear": "2023-2024"
    }
  ]
}
```

## 7. Test Tạo sinh viên mới

### Request
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "SV006",
    "fullName": "Nguyễn Thị Lan",
    "gender": "Nữ",
    "dateOfBirth": "2003-06-15",
    "className": "CNTT-K15",
    "course": "K15",
    "email": "nguyenthilan@email.com",
    "phoneNumber": "0906234567",
    "address": "Hà Nội"
  }'
```

## 8. Test Cập nhật điểm

### Request
```bash
curl -X PUT http://localhost:8080/api/scores/1 \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "SV001",
    "subjectId": 1,
    "processScore": 9.0,
    "componentScore": 8.5,
    "semester": "HK1",
    "academicYear": "2023-2024"
  }'
```

## 9. Test Error Handling

### Test 1: Sinh viên không tồn tại
```bash
curl -X GET http://localhost:8080/api/students/SV999
```

**Expected Response:**
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

### Test 2: Nhập điểm trùng
```bash
curl -X POST http://localhost:8080/api/scores \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "SV001",
    "subjectId": 1,
    "processScore": 8.0,
    "componentScore": 7.5,
    "semester": "HK1",
    "academicYear": "2023-2024"
  }'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Điểm của sinh viên SV001 cho môn Toán cao cấp 1 đã tồn tại",
  "data": null,
  "error": {
    "code": "DUPLICATE_RESOURCE",
    "details": "Điểm của sinh viên SV001 cho môn Toán cao cấp 1 đã tồn tại"
  },
  "timestamp": "2024-12-01T21:44:22"
}
```

### Test 3: Validation Error
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "",
    "fullName": "Test",
    "email": "invalid-email"
  }'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Dữ liệu không hợp lệ",
  "data": {
    "studentCode": "Mã sinh viên không được để trống",
    "email": "Email không hợp lệ"
  },
  "error": null,
  "timestamp": "2024-12-01T21:44:22"
}
```

## 10. Test Quản lý Môn học

### Xem danh sách môn học
```bash
curl -X GET http://localhost:8080/api/subjects
```

### Tạo môn học mới
```bash
curl -X POST http://localhost:8080/api/subjects \
  -H "Content-Type: application/json" \
  -d '{
    "subjectCode": "JAVA101",
    "subjectName": "Lập trình Java",
    "creditHours": 60,
    "processScoreRatio": 0.4,
    "componentScoreRatio": 0.6,
    "description": "Học lập trình Java cơ bản"
  }'
```

### Tìm kiếm môn học
```bash
curl -X GET "http://localhost:8080/api/subjects/search?keyword=Lập trình"
```

## Postman Collection

Nếu sử dụng Postman, import các request sau:

1. **GET** All Students: `http://localhost:8080/api/students`
2. **GET** Student Detail: `http://localhost:8080/api/students/SV001/detail`
3. **GET** Student Scores: `http://localhost:8080/api/scores/student/SV001`
4. **GET** Passed Scores: `http://localhost:8080/api/scores/student/SV001/passed`
5. **GET** Failed Scores: `http://localhost:8080/api/scores/student/SV002/failed`
6. **POST** Create Score: `http://localhost:8080/api/scores` (với body JSON)
7. **PUT** Update Score: `http://localhost:8080/api/scores/1` (với body JSON)
8. **GET** All Subjects: `http://localhost:8080/api/subjects`

## H2 Console

Truy cập database trực tiếp:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:studentdb`
- Username: `sa`
- Password: `password`

### Useful SQL Queries

```sql
-- Xem tất cả sinh viên
SELECT * FROM students;

-- Xem tất cả môn học
SELECT * FROM subjects;

-- Xem tất cả điểm
SELECT * FROM student_subjects;

-- Xem điểm của sinh viên SV001
SELECT ss.*, s.full_name, sub.subject_name 
FROM student_subjects ss
JOIN students s ON ss.student_code = s.student_code
JOIN subjects sub ON ss.subject_id = sub.id
WHERE ss.student_code = 'SV001';

-- Xem sinh viên có điểm trung bình cao nhất
SELECT s.student_code, s.full_name, AVG(ss.final_score) as avg_score
FROM students s
JOIN student_subjects ss ON s.student_code = ss.student_code
GROUP BY s.student_code, s.full_name
ORDER BY avg_score DESC;
```

## Summary of Features

✅ **Chức năng 1:** Xem danh sách sinh viên - `GET /api/students`
✅ **Chức năng 2:** Xem chi tiết sinh viên - `GET /api/students/{code}/detail`
✅ **Chức năng 3:** Xem số môn học sinh viên đăng ký - Included in detail response
✅ **Chức năng 4:** Xem điểm môn học của sinh viên - `GET /api/scores/student/{code}`
✅ **Chức năng 5:** Nhập điểm của sinh viên - `POST /api/scores`
✅ **Chức năng 6:** Xem kết quả trượt đỗ của sinh viên - `GET /api/scores/student/{code}/passed` và `/failed`

✅ **Custom Exceptions:** ResourceNotFoundException, DuplicateResourceException, BadRequestException
✅ **Response Format:** Standardized ApiResponse wrapper for all endpoints
