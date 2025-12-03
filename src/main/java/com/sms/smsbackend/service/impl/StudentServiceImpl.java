package com.sms.smsbackend.service.impl;

import com.sms.smsbackend.dto.request.StudentRequest;
import com.sms.smsbackend.dto.response.ScoreResponse;
import com.sms.smsbackend.dto.response.StudentDetailResponse;
import com.sms.smsbackend.dto.response.StudentResponse;
import com.sms.smsbackend.entity.Student;
import com.sms.smsbackend.entity.StudentSubject;
import com.sms.smsbackend.exception.DuplicateResourceException;
import com.sms.smsbackend.exception.ResourceNotFoundException;
import com.sms.smsbackend.repository.StudentRepository;
import com.sms.smsbackend.repository.StudentSubjectRepository;
import com.sms.smsbackend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByStudentCode(request.getStudentCode())) {
            throw new DuplicateResourceException("Sinh viên", "mã sinh viên", request.getStudentCode());
        }

        Student student = Student.builder()
                .studentCode(request.getStudentCode())
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .className(request.getClassName())
                .course(request.getCourse())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToStudentResponse(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(String studentCode, StudentRequest request) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã sinh viên", studentCode));

        // Check if new student code already exists (if changed)
        if (!studentCode.equals(request.getStudentCode()) && 
            studentRepository.existsByStudentCode(request.getStudentCode())) {
            throw new DuplicateResourceException("Sinh viên", "mã sinh viên", request.getStudentCode());
        }

        student.setStudentCode(request.getStudentCode());
        student.setFullName(request.getFullName());
        student.setGender(request.getGender());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setClassName(request.getClassName());
        student.setCourse(request.getCourse());
        student.setEmail(request.getEmail());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setAddress(request.getAddress());

        Student updatedStudent = studentRepository.save(student);
        return mapToStudentResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã sinh viên", studentCode));
        
        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã sinh viên", studentCode));
        
        return mapToStudentResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDetailResponse getStudentDetail(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã sinh viên", studentCode));

        List<StudentSubject> studentSubjects = studentSubjectRepository.findByStudentStudentCode(studentCode);
        
        Integer totalPassed = (int) studentSubjects.stream()
                .filter(ss -> "ĐẠT".equals(ss.getStatus()))
                .count();
        
        Integer totalFailed = (int) studentSubjects.stream()
                .filter(ss -> "TRƯỢT".equals(ss.getStatus()))
                .count();
        
        Double averageScore = studentSubjectRepository.getAverageScoreByStudentCode(studentCode);
        if (averageScore != null) {
            averageScore = Math.round(averageScore * 100.0) / 100.0;
        }

        List<ScoreResponse> scores = studentSubjects.stream()
                .map(this::mapToScoreResponse)
                .collect(Collectors.toList());

        return StudentDetailResponse.builder()
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth() != null ? student.getDateOfBirth().format(DATE_FORMATTER) : null)
                .className(student.getClassName())
                .course(student.getCourse())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .totalSubjectsRegistered(studentSubjects.size())
                .totalSubjectsPassed(totalPassed)
                .totalSubjectsFailed(totalFailed)
                .averageScore(averageScore)
                .scores(scores)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> searchStudents(String keyword) {
        return studentRepository.searchStudents(keyword).stream()
                .map(this::mapToStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className).stream()
                .map(this::mapToStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course).stream()
                .map(this::mapToStudentResponse)
                .collect(Collectors.toList());
    }

    private StudentResponse mapToStudentResponse(Student student) {
        Integer totalSubjects = studentSubjectRepository.countByStudentCode(student.getStudentCode());
        
        return StudentResponse.builder()
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth())
                .className(student.getClassName())
                .course(student.getCourse())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .totalSubjectsRegistered(totalSubjects != null ? totalSubjects : 0)
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
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
}
