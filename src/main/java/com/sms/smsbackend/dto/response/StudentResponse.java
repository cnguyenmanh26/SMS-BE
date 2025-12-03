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
public class StudentResponse {
    private String studentCode;
    private String fullName;
    private String gender;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;
    
    private String className;
    private String course;
    private String email;
    private String phoneNumber;
    private String address;
    private Integer totalSubjectsRegistered;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createdAt;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate updatedAt;
}
