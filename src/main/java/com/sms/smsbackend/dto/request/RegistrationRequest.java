package com.sms.smsbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @NotNull(message = "ID môn học không được để trống")
    private Long subjectId;

    @NotBlank(message = "Học kỳ không được để trống")
    @Size(max = 20, message = "Học kỳ không được vượt quá 20 ký tự")
    private String semester;

    @NotBlank(message = "Năm học không được để trống")
    @Size(max = 20, message = "Năm học không được vượt quá 20 ký tự")
    private String academicYear;
}
