package com.sms.smsbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailResponse {
    private String studentCode;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String className;
    private String course;
    private String email;
    private String phoneNumber;
    private String address;
    private Integer totalSubjectsRegistered;
    private Integer totalSubjectsPassed;
    private Integer totalSubjectsFailed;
    private Double averageScore;
    private List<ScoreResponse> scores;
}
