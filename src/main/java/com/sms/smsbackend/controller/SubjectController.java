package com.sms.smsbackend.controller;

import com.sms.smsbackend.dto.request.SubjectRequest;
import com.sms.smsbackend.dto.response.ApiResponse;
import com.sms.smsbackend.dto.response.SubjectResponse;
import com.sms.smsbackend.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(
            @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return new ResponseEntity<>(
                ApiResponse.success("Tạo môn học thành công", response),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật môn học thành công", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(
                ApiResponse.success("Xóa môn học thành công")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectById(@PathVariable Long id) {
        SubjectResponse response = subjectService.getSubjectById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping("/code/{subjectCode}")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectByCode(
            @PathVariable String subjectCode) {
        SubjectResponse response = subjectService.getSubjectByCode(subjectCode);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAllSubjects() {
        List<SubjectResponse> responses = subjectService.getAllSubjects();
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> searchSubjects(
            @RequestParam String keyword) {
        List<SubjectResponse> responses = subjectService.searchSubjects(keyword);
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }
}
