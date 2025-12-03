package com.sms.smsbackend.controller;

import com.sms.smsbackend.dto.request.RegistrationRequest;
import com.sms.smsbackend.dto.request.ScoreRequest;
import com.sms.smsbackend.dto.response.ApiResponse;
import com.sms.smsbackend.dto.response.ScoreResponse;
import com.sms.smsbackend.service.ScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScoreResponse>> enterScore(
            @Valid @RequestBody ScoreRequest request) {
        ScoreResponse response = scoreService.enterScore(request);
        return new ResponseEntity<>(
                ApiResponse.success("Nhập điểm thành công", response),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ScoreResponse>> registerSubject(
            @Valid @RequestBody RegistrationRequest request) {
        ScoreResponse response = scoreService.registerSubject(request);
        return new ResponseEntity<>(
                ApiResponse.success("Đăng ký môn học thành công", response),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScoreResponse>> updateScore(
            @PathVariable Long id,
            @Valid @RequestBody ScoreRequest request) {
        ScoreResponse response = scoreService.updateScore(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật điểm thành công", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteScore(@PathVariable Long id) {
        scoreService.deleteScore(id);
        return ResponseEntity.ok(
                ApiResponse.success("Xóa điểm thành công")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScoreResponse>> getScoreById(@PathVariable Long id) {
        ScoreResponse response = scoreService.getScoreById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping("/student/{studentCode}")
    public ResponseEntity<ApiResponse<List<ScoreResponse>>> getScoresByStudent(
            @PathVariable String studentCode) {
        List<ScoreResponse> responses = scoreService.getScoresByStudent(studentCode);
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<ScoreResponse>>> getScoresBySubject(
            @PathVariable Long subjectId) {
        List<ScoreResponse> responses = scoreService.getScoresBySubject(subjectId);
        return ResponseEntity.ok(
                ApiResponse.success(responses)
        );
    }

    @GetMapping("/student/{studentCode}/passed")
    public ResponseEntity<ApiResponse<List<ScoreResponse>>> getPassedScoresByStudent(
            @PathVariable String studentCode) {
        List<ScoreResponse> responses = scoreService.getPassedScoresByStudent(studentCode);
        return ResponseEntity.ok(
                ApiResponse.success("Danh sách môn đạt", responses)
        );
    }

    @GetMapping("/student/{studentCode}/failed")
    public ResponseEntity<ApiResponse<List<ScoreResponse>>> getFailedScoresByStudent(
            @PathVariable String studentCode) {
        List<ScoreResponse> responses = scoreService.getFailedScoresByStudent(studentCode);
        return ResponseEntity.ok(
                ApiResponse.success("Danh sách môn trượt", responses)
        );
    }

    @GetMapping("/student/{studentCode}/subject/{subjectId}")
    public ResponseEntity<ApiResponse<ScoreResponse>> getStudentScoreInSubject(
            @PathVariable String studentCode,
            @PathVariable Long subjectId) {
        ScoreResponse response = scoreService.getStudentScoreInSubject(studentCode, subjectId);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }
}
