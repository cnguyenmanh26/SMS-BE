package com.sms.smsbackend.repository;

import com.sms.smsbackend.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {
    
    // Original methods - không dùng nữa, deprecated để tránh lỗi
    @Deprecated
    List<StudentSubject> findByStudentStudentCode(String studentCode);
    
    // Optimized method with JOIN FETCH to avoid N+1 queries
    @Query("SELECT DISTINCT ss FROM StudentSubject ss " +
           "LEFT JOIN FETCH ss.student " +
           "LEFT JOIN FETCH ss.subject " +
           "WHERE ss.student.studentCode = :studentCode " +
           "ORDER BY ss.createdAt DESC")
    List<StudentSubject> findByStudentStudentCodeWithDetails(@Param("studentCode") String studentCode);
    
    @Deprecated
    List<StudentSubject> findBySubjectId(Long subjectId);
    
    // Optimized method with JOIN FETCH for subject queries
    @Query("SELECT DISTINCT ss FROM StudentSubject ss " +
           "LEFT JOIN FETCH ss.student " +
           "LEFT JOIN FETCH ss.subject " +
           "WHERE ss.subject.id = :subjectId " +
           "ORDER BY ss.student.studentCode")
    List<StudentSubject> findBySubjectIdWithDetails(@Param("subjectId") Long subjectId);
    
    Optional<StudentSubject> findByStudentStudentCodeAndSubjectId(String studentCode, Long subjectId);
    
    boolean existsByStudentStudentCodeAndSubjectId(String studentCode, Long subjectId);
    
    @Query("SELECT DISTINCT ss FROM StudentSubject ss " +
           "LEFT JOIN FETCH ss.student " +
           "LEFT JOIN FETCH ss.subject " +
           "WHERE ss.student.studentCode = :studentCode AND ss.status = :status " +
           "ORDER BY ss.createdAt DESC")
    List<StudentSubject> findByStudentCodeAndStatus(@Param("studentCode") String studentCode, 
                                                     @Param("status") String status);
    
    @Query("SELECT COUNT(ss) FROM StudentSubject ss WHERE ss.student.studentCode = :studentCode")
    Integer countByStudentCode(@Param("studentCode") String studentCode);
    
    @Query("SELECT COUNT(ss) FROM StudentSubject ss WHERE ss.subject.id = :subjectId")
    Integer countBySubjectId(@Param("subjectId") Long subjectId);
    
    @Query("SELECT AVG(ss.finalScore) FROM StudentSubject ss WHERE ss.student.studentCode = :studentCode AND ss.finalScore IS NOT NULL")
    Double getAverageScoreByStudentCode(@Param("studentCode") String studentCode);
}
