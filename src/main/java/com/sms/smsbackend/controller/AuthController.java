package com.sms.smsbackend.controller;

import com.sms.smsbackend.dto.request.LoginRequest;
import com.sms.smsbackend.dto.request.RegisterRequest;
import com.sms.smsbackend.dto.response.ApiResponse;
import com.sms.smsbackend.dto.response.JwtResponse;
import com.sms.smsbackend.dto.response.MessageResponse;
import com.sms.smsbackend.entity.Role;
import com.sms.smsbackend.entity.User;
import com.sms.smsbackend.repository.RoleRepository;
import com.sms.smsbackend.repository.UserRepository;
import com.sms.smsbackend.security.jwt.JwtUtils;
import com.sms.smsbackend.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponse jwtResponse = JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .studentCode(userDetails.getStudentCode())
                .roles(roles)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", jwtResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MessageResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Username đã tồn tại"));
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Email đã tồn tại"));
        }

        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .studentCode(registerRequest.getStudentCode())
                .enabled(true)
                .build();

        // Set roles
        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role is USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
                        roles.add(adminRole);
                        break;
                    case "USER":
                        Role userRole = roleRepository.findByName("ROLE_USER")
                                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
                        roles.add(userRole);
                        break;
                    default:
                        throw new RuntimeException("Error: Role " + role + " is not found.");
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(
                ApiResponse.success("Đăng ký thành công", new MessageResponse("User registered successfully!")),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MessageResponse>> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(
                ApiResponse.success("Đăng xuất thành công", new MessageResponse("User logged out successfully!"))
        );
    }
}
