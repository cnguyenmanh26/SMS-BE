package com.sms.smsbackend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {
    
    @NotBlank(message = "Mã sinh viên không được để trống")
    @Size(max = 20, message = "Mã sinh viên không được vượt quá 20 ký tự")
    private String studentCode;

    @NotBlank(message = "Tên sinh viên không được để trống")
    @Size(max = 100, message = "Tên sinh viên không được vượt quá 100 ký tự")
    private String fullName;

    @Pattern(regexp = "Nam|Nữ|Khác", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    private String gender;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @Size(max = 50, message = "Tên lớp không được vượt quá 50 ký tự")
    private String className;

    @Size(max = 20, message = "Khóa học không được vượt quá 20 ký tự")
    private String course;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại phải từ 10-15 chữ số")
    private String phoneNumber;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;
}
