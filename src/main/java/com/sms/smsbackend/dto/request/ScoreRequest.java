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
public class ScoreRequest {
    
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @NotNull(message = "ID môn học không được để trống")
    private Long subjectId;

    @NotNull(message = "Điểm quá trình không được để trống")
    @DecimalMin(value = "0.0", message = "Điểm quá trình phải từ 0 đến 10")
    @DecimalMax(value = "10.0", message = "Điểm quá trình phải từ 0 đến 10")
    private Double processScore;

    @NotNull(message = "Điểm thành phần không được để trống")
    @DecimalMin(value = "0.0", message = "Điểm thành phần phải từ 0 đến 10")
    @DecimalMax(value = "10.0", message = "Điểm thành phần phải từ 0 đến 10")
    private Double componentScore;

    @NotBlank(message = "Học kỳ không được để trống")
    @Size(max = 20, message = "Học kỳ không được vượt quá 20 ký tự")
    private String semester;

    @NotBlank(message = "Năm học không được để trống")
    @Size(max = 20, message = "Năm học không được vượt quá 20 ký tự")
    private String academicYear;
}
