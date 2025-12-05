package com.sms.smsbackend.controller;

import com.sms.smsbackend.dto.request.ApiCredentialRequest;
import com.sms.smsbackend.dto.response.ApiCredentialResponse;
import com.sms.smsbackend.dto.response.ApiResponse;
import com.sms.smsbackend.entity.ApiCredential;
import com.sms.smsbackend.entity.User;
import com.sms.smsbackend.repository.ApiCredentialRepository;
import com.sms.smsbackend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ApiCredentialController {
    
    private final ApiCredentialRepository apiCredentialRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ApiCredentialResponse>> createCredential(
            @Valid @RequestBody ApiCredentialRequest request) {
        
        // Generate App-ID and Secret Key
        String appId = "app_" + UUID.randomUUID().toString().replace("-", "");
        String secretKey = UUID.randomUUID().toString().replace("-", "") + 
                          UUID.randomUUID().toString().replace("-", "");

        ApiCredential credential = ApiCredential.builder()
                .appId(appId)
                .secretKey(secretKey)
                .description(request.getDescription())
                .enabled(true)
                .build();

        // Associate with user if provided
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            credential.setUser(user);
        }

        ApiCredential saved = apiCredentialRepository.save(credential);

        ApiCredentialResponse response = ApiCredentialResponse.builder()
                .id(saved.getId())
                .appId(saved.getAppId())
                .secretKey(saved.getSecretKey()) // Only shown once!
                .description(saved.getDescription())
                .enabled(saved.getEnabled())
                .createdAt(saved.getCreatedAt())
                .build();

        return new ResponseEntity<>(
                ApiResponse.success("API credential created successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiCredentialResponse>>> getAllCredentials() {
        List<ApiCredentialResponse> credentials = apiCredentialRepository.findAll().stream()
                .map(c -> ApiCredentialResponse.builder()
                        .id(c.getId())
                        .appId(c.getAppId())
                        .secretKey("***HIDDEN***") // Don't expose secret key
                        .description(c.getDescription())
                        .enabled(c.getEnabled())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("API credentials retrieved", credentials));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCredential(@PathVariable Long id) {
        ApiCredential credential = apiCredentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

        apiCredentialRepository.delete(credential);

        return ResponseEntity.ok(ApiResponse.success("API credential deleted", null));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleCredential(@PathVariable Long id) {
        ApiCredential credential = apiCredentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

        credential.setEnabled(!credential.getEnabled());
        apiCredentialRepository.save(credential);

        return ResponseEntity.ok(
                ApiResponse.success("API credential " + (credential.getEnabled() ? "enabled" : "disabled"), null)
        );
    }
}
