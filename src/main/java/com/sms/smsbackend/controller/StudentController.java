package com.sms.smsbackend.controller;

import com.sms.smsbackend.dto.request.StudentRequest;
import com.sms.smsbackend.dto.response.ApiResponse;
import com.sms.smsbackend.dto.response.StudentDetailResponse;
import com.sms.smsbackend.dto.response.StudentResponse;
import com.sms.smsbackend.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return new ResponseEntity<>(
                ApiResponse.success("Tạo sinh viên thành công", response),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{studentCode}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable String studentCode,
            @Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.updateStudent(studentCode, request);
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật sinh viên thành công", response)
        );
    }

    @DeleteMapping("/{studentCode}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable String studentCode) {
        studentService.deleteStudent(studentCode);
        return ResponseEntity.ok(
                ApiResponse.success("Xóa sinh viên thành công")
        );
    }

    @GetMapping("/{studentCode}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentByCode(
            @PathVariable String studentCode) {
        StudentResponse response = studentService.getStudentByCode(studentCode);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping("/{studentCode}/detail")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> getStudentDetail(
            @PathVariable String studentCode) {
        StudentDetailResponse response = studentService.getStudentDetail(studentCode);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        List<StudentResponse> responses = studentService.getAllStudents();
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> searchStudents(
            @RequestParam String keyword) {
        List<StudentResponse> responses = studentService.searchStudents(keyword);
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }

    @GetMapping("/class/{className}")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByClass(
            @PathVariable String className) {
        List<StudentResponse> responses = studentService.getStudentsByClass(className);
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }

    @GetMapping("/course/{course}")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByCourse(
            @PathVariable String course) {
        List<StudentResponse> responses = studentService.getStudentsByCourse(course);
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }
}
