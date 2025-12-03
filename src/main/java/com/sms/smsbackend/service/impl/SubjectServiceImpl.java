package com.sms.smsbackend.service.impl;

import com.sms.smsbackend.dto.request.SubjectRequest;
import com.sms.smsbackend.dto.response.SubjectResponse;
import com.sms.smsbackend.entity.Subject;
import com.sms.smsbackend.exception.BadRequestException;
import com.sms.smsbackend.exception.DuplicateResourceException;
import com.sms.smsbackend.exception.ResourceNotFoundException;
import com.sms.smsbackend.repository.StudentSubjectRepository;
import com.sms.smsbackend.repository.SubjectRepository;
import com.sms.smsbackend.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    @Override
    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        if (subjectRepository.existsBySubjectCode(request.getSubjectCode())) {
            throw new DuplicateResourceException("Môn học", "mã môn học", request.getSubjectCode());
        }

        // Validate score ratios sum to 1.0
        validateScoreRatios(request.getProcessScoreRatio(), request.getComponentScoreRatio());

        Subject subject = Subject.builder()
                .subjectCode(request.getSubjectCode())
                .subjectName(request.getSubjectName())
                .creditHours(request.getCreditHours())
                .processScoreRatio(request.getProcessScoreRatio())
                .componentScoreRatio(request.getComponentScoreRatio())
                .description(request.getDescription())
                .build();

        Subject savedSubject = subjectRepository.save(subject);
        return mapToSubjectResponse(savedSubject);
    }

    @Override
    @Transactional
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", id));

        // Check if new subject code already exists (if changed)
        if (!subject.getSubjectCode().equals(request.getSubjectCode()) && 
            subjectRepository.existsBySubjectCode(request.getSubjectCode())) {
            throw new DuplicateResourceException("Môn học", "mã môn học", request.getSubjectCode());
        }

        // Validate score ratios sum to 1.0
        validateScoreRatios(request.getProcessScoreRatio(), request.getComponentScoreRatio());

        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        subject.setCreditHours(request.getCreditHours());
        subject.setProcessScoreRatio(request.getProcessScoreRatio());
        subject.setComponentScoreRatio(request.getComponentScoreRatio());
        subject.setDescription(request.getDescription());

        Subject updatedSubject = subjectRepository.save(subject);
        return mapToSubjectResponse(updatedSubject);
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", id));
        
        subjectRepository.delete(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", id));
        
        return mapToSubjectResponse(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectByCode(String subjectCode) {
        Subject subject = subjectRepository.findBySubjectCode(subjectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "mã môn học", subjectCode));
        
        return mapToSubjectResponse(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToSubjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> searchSubjects(String keyword) {
        return subjectRepository.searchSubjects(keyword).stream()
                .map(this::mapToSubjectResponse)
                .collect(Collectors.toList());
    }

    private void validateScoreRatios(Double processRatio, Double componentRatio) {
        double sum = processRatio + componentRatio;
        // Allow small floating point error
        if (Math.abs(sum - 1.0) > 0.001) {
            throw new BadRequestException(
                    String.format("Tổng tỷ lệ điểm phải bằng 1.0 (100%%). Hiện tại: %.2f", sum));
        }
    }

    private SubjectResponse mapToSubjectResponse(Subject subject) {
        Integer totalStudents = studentSubjectRepository.countBySubjectId(subject.getId());
        
        return SubjectResponse.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .creditHours(subject.getCreditHours())
                .processScoreRatio(subject.getProcessScoreRatio())
                .componentScoreRatio(subject.getComponentScoreRatio())
                .description(subject.getDescription())
                .totalStudentsEnrolled(totalStudents != null ? totalStudents : 0)
                .build();
    }
}
