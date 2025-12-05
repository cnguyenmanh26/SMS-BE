package com.sms.smsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student_subjects", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_code", "subject_id", "semester", "academic_year"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_code", referencedColumnName = "student_code", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "process_score")
    private Double processScore; // Điểm quá trình

    @Column(name = "component_score")
    private Double componentScore; // Điểm thành phần

    @Column(name = "final_score")
    private Double finalScore; // Điểm tổng kết (tự động tính)

    @Column(name = "status", length = 20)
    private String status; // ĐẠT hoặc TRƯỢT

    @Column(name = "semester", length = 20, nullable = false)
    private String semester; // Học kỳ

    @Column(name = "academic_year", length = 20, nullable = false)
    private String academicYear; // Năm học

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        calculateFinalScore();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
        calculateFinalScore();
    }

    // Tính điểm tổng kết và trạng thái
    public void calculateFinalScore() {
        if (processScore != null && componentScore != null && subject != null) {
            this.finalScore = (processScore * subject.getProcessScoreRatio()) + 
                            (componentScore * subject.getComponentScoreRatio());
            
            // Làm tròn đến 2 chữ số thập phân
            this.finalScore = Math.round(this.finalScore * 100.0) / 100.0;
            
            // Xác định trạng thái đậu/rớt
            this.status = this.finalScore >= 4.0 ? "ĐẠT" : "TRƯỢT";
        }
    }

    // Helper method để kiểm tra đã nhập đủ điểm chưa
    public boolean hasAllScores() {
        return processScore != null && componentScore != null;
    }
}
