package com.sms.smsbackend.service;

import com.sms.smsbackend.dto.request.StudentRequest;
import com.sms.smsbackend.dto.response.StudentDetailResponse;
import com.sms.smsbackend.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    
    StudentResponse createStudent(StudentRequest request);
    
    StudentResponse updateStudent(String studentCode, StudentRequest request);
    
    void deleteStudent(String studentCode);
    
    StudentResponse getStudentByCode(String studentCode);
    
    StudentDetailResponse getStudentDetail(String studentCode);
    
    List<StudentResponse> getAllStudents();
    
    List<StudentResponse> searchStudents(String keyword);
    
    List<StudentResponse> getStudentsByClass(String className);
    
    List<StudentResponse> getStudentsByCourse(String course);
}
