package com.sms.smsbackend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {
    
    @NotBlank(message = "Mã môn học không được để trống")
    @Size(max = 20, message = "Mã môn học không được vượt quá 20 ký tự")
    private String subjectCode;

    @NotBlank(message = "Tên môn học không được để trống")
    @Size(max = 100, message = "Tên môn học không được vượt quá 100 ký tự")
    private String subjectName;

    @NotNull(message = "Số tiết học không được để trống")
    @Min(value = 1, message = "Số tiết học phải lớn hơn 0")
    @Max(value = 200, message = "Số tiết học không được vượt quá 200")
    private Integer creditHours;

    @NotNull(message = "Tỷ lệ điểm quá trình không được để trống")
    @DecimalMin(value = "0.0", message = "Tỷ lệ điểm quá trình phải từ 0 đến 1")
    @DecimalMax(value = "1.0", message = "Tỷ lệ điểm quá trình phải từ 0 đến 1")
    private Double processScoreRatio;

    @NotNull(message = "Tỷ lệ điểm thành phần không được để trống")
    @DecimalMin(value = "0.0", message = "Tỷ lệ điểm thành phần phải từ 0 đến 1")
    @DecimalMax(value = "1.0", message = "Tỷ lệ điểm thành phần phải từ 0 đến 1")
    private Double componentScoreRatio;

    private String description;
}
