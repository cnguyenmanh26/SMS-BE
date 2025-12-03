package com.sms.smsbackend.exception;

import com.sms.smsbackend.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        ApiResponse.ErrorDetails errorDetails = ApiResponse.ErrorDetails.builder()
                .code("RESOURCE_NOT_FOUND")
                .field(ex.getFieldName())
                .rejectedValue(ex.getFieldValue())
                .details(ex.getMessage())
                .build();

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        
        ApiResponse.ErrorDetails errorDetails = ApiResponse.ErrorDetails.builder()
                .code("BAD_REQUEST")
                .field(ex.getField())
                .rejectedValue(ex.getRejectedValue())
                .details(ex.getMessage())
                .build();

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        
        ApiResponse.ErrorDetails errorDetails = ApiResponse.ErrorDetails.builder()
                .code("DUPLICATE_RESOURCE")
                .field(ex.getFieldName())
                .rejectedValue(ex.getFieldValue())
                .details(ex.getMessage())
                .build();

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Dữ liệu không hợp lệ")
                .data(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ApiResponse.ErrorDetails errorDetails = ApiResponse.ErrorDetails.builder()
                .code("INTERNAL_SERVER_ERROR")
                .details(ex.getMessage())
                .build();

        ApiResponse<Object> response = ApiResponse.error(
                "Đã xảy ra lỗi không mong muốn", errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
