package com.sms.smsbackend.repository;

import com.sms.smsbackend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    Optional<Subject> findBySubjectCode(String subjectCode);
    
    boolean existsBySubjectCode(String subjectCode);
    
    @Query("SELECT s FROM Subject s WHERE " +
           "LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Subject> searchSubjects(@Param("keyword") String keyword);
}
