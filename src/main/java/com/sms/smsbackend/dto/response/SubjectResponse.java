package com.sms.smsbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {
    private Long id;
    private String subjectCode;
    private String subjectName;
    private Integer creditHours;
    private Double processScoreRatio;
    private Double componentScoreRatio;
    private String description;
    private Integer totalStudentsEnrolled;
}
