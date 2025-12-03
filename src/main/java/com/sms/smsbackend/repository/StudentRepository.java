package com.sms.smsbackend.repository;

import com.sms.smsbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    
    Optional<Student> findByStudentCode(String studentCode);
    
    boolean existsByStudentCode(String studentCode);
    
    List<Student> findByClassName(String className);
    
    List<Student> findByCourse(String course);
    
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.className) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchStudents(@Param("keyword") String keyword);
}
