package com.sms.smsbackend.service.impl;

import com.sms.smsbackend.dto.request.ScoreRequest;
import com.sms.smsbackend.dto.response.ScoreResponse;
import com.sms.smsbackend.entity.Student;
import com.sms.smsbackend.entity.StudentSubject;
import com.sms.smsbackend.entity.Subject;
import com.sms.smsbackend.exception.BadRequestException;
import com.sms.smsbackend.exception.DuplicateResourceException;
import com.sms.smsbackend.exception.ResourceNotFoundException;
import com.sms.smsbackend.repository.StudentRepository;
import com.sms.smsbackend.repository.StudentSubjectRepository;
import com.sms.smsbackend.repository.SubjectRepository;
import com.sms.smsbackend.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public ScoreResponse enterScore(ScoreRequest request) {
        // Validate student exists
        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã sinh viên", request.getStudentCode()));

        // Validate subject exists
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", request.getSubjectId()));

        // Check if score already exists
        if (studentSubjectRepository.existsByStudentStudentCodeAndSubjectId(
                request.getStudentCode(), request.getSubjectId())) {
            throw new DuplicateResourceException(
                    String.format("Điểm của sinh viên %s cho môn %s đã tồn tại", 
                            request.getStudentCode(), subject.getSubjectName()));
        }

        // Validate score ranges
        validateScores(request.getProcessScore(), request.getComponentScore());

        StudentSubject studentSubject = StudentSubject.builder()
                .student(student)
                .subject(subject)
                .processScore(request.getProcessScore())
                .componentScore(request.getComponentScore())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .build();

        // Final score and status will be calculated automatically in @PrePersist
        StudentSubject savedStudentSubject = studentSubjectRepository.save(studentSubject);
        return mapToScoreResponse(savedStudentSubject);
    }

    @Override
    @Transactional
    public ScoreResponse updateScore(Long id, ScoreRequest request) {
        StudentSubject studentSubject = studentSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Điểm", "id", id));

        // Validate new student if changed
        if (!studentSubject.getStudent().getStudentCode().equals(request.getStudentCode())) {
            Student newStudent = studentRepository.findByStudentCode(request.getStudentCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã sinh viên", request.getStudentCode()));
            studentSubject.setStudent(newStudent);
        }

        // Validate new subject if changed
        if (!studentSubject.getSubject().getId().equals(request.getSubjectId())) {
            Subject newSubject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", request.getSubjectId()));
            studentSubject.setSubject(newSubject);
        }

        // Validate score ranges
        validateScores(request.getProcessScore(), request.getComponentScore());

        studentSubject.setProcessScore(request.getProcessScore());
        studentSubject.setComponentScore(request.getComponentScore());
        studentSubject.setSemester(request.getSemester());
        studentSubject.setAcademicYear(request.getAcademicYear());

        // Final score and status will be recalculated in @PreUpdate
        StudentSubject updatedStudentSubject = studentSubjectRepository.save(studentSubject);
        return mapToScoreResponse(updatedStudentSubject);
    }

    @Override
    @Transactional
    public void deleteScore(Long id) {
        StudentSubject studentSubject = studentSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Điểm", "id", id));
        
        studentSubjectRepository.delete(studentSubject);
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreResponse getScoreById(Long id) {
        StudentSubject studentSubject = studentSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Điểm", "id", id));
        
        return mapToScoreResponse(studentSubject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResponse> getScoresByStudent(String studentCode) {
        // Validate student exists
        if (!studentRepository.existsByStudentCode(studentCode)) {
            throw new ResourceNotFoundException("Sinh viên", "mã sinh viên", studentCode);
        }

        return studentSubjectRepository.findByStudentStudentCodeWithDetails(studentCode).stream()
                .map(this::mapToScoreResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResponse> getScoresBySubject(Long subjectId) {
        // Validate subject exists
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Môn học", "id", subjectId);
        }

        return studentSubjectRepository.findBySubjectIdWithDetails(subjectId).stream()
                .map(this::mapToScoreResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResponse> getPassedScoresByStudent(String studentCode) {
        // Validate student exists
        if (!studentRepository.existsByStudentCode(studentCode)) {
            throw new ResourceNotFoundException("Sinh viên", "mã sinh viên", studentCode);
        }

        return studentSubjectRepository.findByStudentCodeAndStatus(studentCode, "ĐẠT").stream()
                .map(this::mapToScoreResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResponse> getFailedScoresByStudent(String studentCode) {
        // Validate student exists
        if (!studentRepository.existsByStudentCode(studentCode)) {
            throw new ResourceNotFoundException("Sinh viên", "mã sinh viên", studentCode);
        }

        return studentSubjectRepository.findByStudentCodeAndStatus(studentCode, "TRƯỢT").stream()
                .map(this::mapToScoreResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreResponse getStudentScoreInSubject(String studentCode, Long subjectId) {
        StudentSubject studentSubject = studentSubjectRepository
                .findByStudentStudentCodeAndSubjectId(studentCode, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Không tìm thấy điểm của sinh viên %s cho môn học id %d", 
                                studentCode, subjectId)));
        
        return mapToScoreResponse(studentSubject);
    }

    private void validateScores(Double processScore, Double componentScore) {
        if (processScore < 0 || processScore > 10) {
            throw new BadRequestException("Điểm quá trình phải từ 0 đến 10", "processScore", processScore);
        }
        if (componentScore < 0 || componentScore > 10) {
            throw new BadRequestException("Điểm thành phần phải từ 0 đến 10", "componentScore", componentScore);
        }
    }

    private ScoreResponse mapToScoreResponse(StudentSubject ss) {
        return ScoreResponse.builder()
                .id(ss.getId())
                .studentCode(ss.getStudent().getStudentCode())
                .studentName(ss.getStudent().getFullName())
                .subjectId(ss.getSubject().getId())
                .subjectCode(ss.getSubject().getSubjectCode())
                .subjectName(ss.getSubject().getSubjectName())
                .processScore(ss.getProcessScore())
                .componentScore(ss.getComponentScore())
                .finalScore(ss.getFinalScore())
                .status(ss.getStatus())
                .semester(ss.getSemester())
                .academicYear(ss.getAcademicYear())
                .createdAt(ss.getCreatedAt())
                .updatedAt(ss.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public ScoreResponse registerSubject(com.sms.smsbackend.dto.request.RegistrationRequest request) {
        // Validate student exists
        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã sinh viên", request.getStudentCode()));

        // Validate subject exists
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", request.getSubjectId()));

        // Check if already registered
        if (studentSubjectRepository.existsByStudentStudentCodeAndSubjectId(
                request.getStudentCode(), request.getSubjectId())) {
            throw new DuplicateResourceException(
                    String.format("Sinh viên %s đã đăng ký môn %s", 
                            request.getStudentCode(), subject.getSubjectName()));
        }

        StudentSubject studentSubject = StudentSubject.builder()
                .student(student)
                .subject(subject)
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .status("CHƯA CÓ ĐIỂM") // Initial status
                .build();

        StudentSubject savedStudentSubject = studentSubjectRepository.save(studentSubject);
        return mapToScoreResponse(savedStudentSubject);
    }
}
