package com.sms.smsbackend.service;

import com.sms.smsbackend.dto.request.SubjectRequest;
import com.sms.smsbackend.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {
    
    SubjectResponse createSubject(SubjectRequest request);
    
    SubjectResponse updateSubject(Long id, SubjectRequest request);
    
    void deleteSubject(Long id);
    
    SubjectResponse getSubjectById(Long id);
    
    SubjectResponse getSubjectByCode(String subjectCode);
    
    List<SubjectResponse> getAllSubjects();
    
    List<SubjectResponse> searchSubjects(String keyword);
}
