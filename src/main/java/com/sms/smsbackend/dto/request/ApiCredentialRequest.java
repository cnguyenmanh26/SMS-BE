package com.sms.smsbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiCredentialRequest {
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private Long userId; // Optional: associate with a user
}
