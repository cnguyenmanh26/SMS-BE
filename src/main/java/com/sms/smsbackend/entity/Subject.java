package com.sms.smsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", unique = true, nullable = false, length = 20)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @Column(name = "credit_hours", nullable = false)
    private Integer creditHours;

    @Column(name = "process_score_ratio", nullable = false)
    private Double processScoreRatio; // Tỷ lệ điểm quá trình (0-1)

    @Column(name = "component_score_ratio", nullable = false)
    private Double componentScoreRatio; // Tỷ lệ điểm thành phần (0-1)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudentSubject> studentSubjects = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
