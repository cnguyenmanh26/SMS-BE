package com.sms.smsbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponse {
    private Long id;
    private String studentCode;
    private String studentName;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private Double processScore;
    private Double componentScore;
    private Double finalScore;
    private String status;
    private String semester;
    private String academicYear;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createdAt;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate updatedAt;
}
