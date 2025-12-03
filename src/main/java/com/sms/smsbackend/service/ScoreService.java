package com.sms.smsbackend.service;

import com.sms.smsbackend.dto.request.ScoreRequest;
import com.sms.smsbackend.dto.response.ScoreResponse;

import java.util.List;

public interface ScoreService {
    
    ScoreResponse enterScore(ScoreRequest request);
    
    ScoreResponse updateScore(Long id, ScoreRequest request);
    
    void deleteScore(Long id);
    
    ScoreResponse getScoreById(Long id);
    
    List<ScoreResponse> getScoresByStudent(String studentCode);
    
    List<ScoreResponse> getScoresBySubject(Long subjectId);
    
    List<ScoreResponse> getPassedScoresByStudent(String studentCode);
    
    List<ScoreResponse> getFailedScoresByStudent(String studentCode);
    
    ScoreResponse getStudentScoreInSubject(String studentCode, Long subjectId);

    ScoreResponse registerSubject(com.sms.smsbackend.dto.request.RegistrationRequest request);
}
