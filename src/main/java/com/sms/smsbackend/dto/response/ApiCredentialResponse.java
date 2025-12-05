package com.sms.smsbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCredentialResponse {
    private Long id;
    private String appId;
    private String secretKey; // Only shown once during creation
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
