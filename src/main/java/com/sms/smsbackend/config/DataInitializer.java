package com.sms.smsbackend.config;

import com.sms.smsbackend.entity.Role;
import com.sms.smsbackend.entity.Student;
import com.sms.smsbackend.entity.StudentSubject;
import com.sms.smsbackend.entity.Subject;
import com.sms.smsbackend.entity.User;
import com.sms.smsbackend.repository.RoleRepository;
import com.sms.smsbackend.repository.StudentRepository;
import com.sms.smsbackend.repository.StudentSubjectRepository;
import com.sms.smsbackend.repository.SubjectRepository;
import com.sms.smsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            StudentSubjectRepository studentSubjectRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // Initialize roles first
            createRoleIfNotExists(roleRepository, "ROLE_ADMIN");
            createRoleIfNotExists(roleRepository, "ROLE_USER");

            // Create default admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

                User admin = User.builder()
                        .username("admin")
                        .email("admin@sms.com")
                        .password(passwordEncoder.encode("admin123"))
                        .enabled(true)
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(admin);
                log.info("✅ Default admin user created: username=admin, password=admin123");
            }

            // Get USER role for student accounts
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

            // Create USER accounts for existing students if they don't have accounts yet
            List<Student> existingStudents = studentRepository.findAll();
            if (!existingStudents.isEmpty()) {
                for (Student student : existingStudents) {
                    createUserForStudent(userRepository, passwordEncoder, userRole, student);
                }
                log.info("✅ Checked and created USER accounts for {} existing students", existingStudents.size());
            }

            // Check if sample data already exists
            if (studentRepository.count() > 0) {
                log.info("Sample data already initialized");
                return;
            }

            log.info("Initializing database with sample data...");

            // Create students
            Student sv001 = Student.builder()
                    .studentCode("SV001")
                    .fullName("Nguyễn Văn An")
                    .gender("Nam")
                    .dateOfBirth(LocalDate.of(2003, 5, 15))
                    .className("CNTT-K15")
                    .course("K15")
                    .email("nguyenvanan@email.com")
                    .phoneNumber("0901234567")
                    .address("Hà Nội")
                    .build();

            Student sv002 = Student.builder()
                    .studentCode("SV002")
                    .fullName("Trần Thị Bình")
                    .gender("Nữ")
                    .dateOfBirth(LocalDate.of(2003, 8, 20))
                    .className("CNTT-K15")
                    .course("K15")
                    .email("tranthibinh@email.com")
                    .phoneNumber("0902234567")
                    .address("Hồ Chí Minh")
                    .build();

            Student sv003 = Student.builder()
                    .studentCode("SV003")
                    .fullName("Lê Văn Cường")
                    .gender("Nam")
                    .dateOfBirth(LocalDate.of(2003, 3, 10))
                    .className("CNTT-K15")
                    .course("K15")
                    .email("levancuong@email.com")
                    .phoneNumber("0903234567")
                    .address("Đà Nẵng")
                    .build();

            Student sv004 = Student.builder()
                    .studentCode("SV004")
                    .fullName("Phạm Thị Dung")
                    .gender("Nữ")
                    .dateOfBirth(LocalDate.of(2003, 11, 25))
                    .className("CNTT-K16")
                    .course("K16")
                    .email("phamthidung@email.com")
                    .phoneNumber("0904234567")
                    .address("Hải Phòng")
                    .build();

            Student sv005 = Student.builder()
                    .studentCode("SV005")
                    .fullName("Hoàng Văn Em")
                    .gender("Nam")
                    .dateOfBirth(LocalDate.of(2004, 1, 30))
                    .className("CNTT-K16")
                    .course("K16")
                    .email("hoangvanem@email.com")
                    .phoneNumber("0905234567")
                    .address("Cần Thơ")
                    .build();

            studentRepository.save(sv001);
            studentRepository.save(sv002);
            studentRepository.save(sv003);
            studentRepository.save(sv004);
            studentRepository.save(sv005);

            log.info("Created 5 students");

            // User accounts will be created on next restart by the logic above
            // (checking existing students and creating accounts)

            // Create subjects
            Subject math101 = Subject.builder()
                    .subjectCode("MATH101")
                    .subjectName("Toán cao cấp 1")
                    .creditHours(45)
                    .processScoreRatio(0.3)
                    .componentScoreRatio(0.7)
                    .description("Môn toán cơ bản cho sinh viên năm nhất")
                    .build();

            Subject phys101 = Subject.builder()
                    .subjectCode("PHYS101")
                    .subjectName("Vật lý đại cương")
                    .creditHours(45)
                    .processScoreRatio(0.4)
                    .componentScoreRatio(0.6)
                    .description("Môn vật lý cơ bản")
                    .build();

            Subject prog101 = Subject.builder()
                    .subjectCode("PROG101")
                    .subjectName("Lập trình căn bản")
                    .creditHours(60)
                    .processScoreRatio(0.3)
                    .componentScoreRatio(0.7)
                    .description("Học lập trình C/C++")
                    .build();

            Subject data101 = Subject.builder()
                    .subjectCode("DATA101")
                    .subjectName("Cấu trúc dữ liệu")
                    .creditHours(60)
                    .processScoreRatio(0.4)
                    .componentScoreRatio(0.6)
                    .description("Học về cấu trúc dữ liệu và giải thuật")
                    .build();

            Subject web101 = Subject.builder()
                    .subjectCode("WEB101")
                    .subjectName("Lập trình Web")
                    .creditHours(60)
                    .processScoreRatio(0.3)
                    .componentScoreRatio(0.7)
                    .description("Học HTML, CSS, JavaScript")
                    .build();

            Subject db101 = Subject.builder()
                    .subjectCode("DB101")
                    .subjectName("Cơ sở dữ liệu")
                    .creditHours(45)
                    .processScoreRatio(0.3)
                    .componentScoreRatio(0.7)
                    .description("Học về SQL và quản lý CSDL")
                    .build();

            subjectRepository.save(math101);
            subjectRepository.save(phys101);
            subjectRepository.save(prog101);
            subjectRepository.save(data101);
            subjectRepository.save(web101);
            subjectRepository.save(db101);

            log.info("Created 6 subjects");

            // Create student-subject scores
            // Student SV001
            createScore(studentSubjectRepository, sv001, math101, 8.5, 7.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv001, phys101, 7.0, 6.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv001, prog101, 9.0, 8.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv001, data101, 6.5, 7.0, "HK2", "2023-2024");

            // Student SV002
            createScore(studentSubjectRepository, sv002, math101, 5.0, 4.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv002, phys101, 6.0, 5.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv002, prog101, 7.5, 8.0, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv002, web101, 8.0, 7.5, "HK2", "2023-2024");

            // Student SV003
            createScore(studentSubjectRepository, sv003, math101, 9.0, 8.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv003, prog101, 8.5, 9.0, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv003, data101, 7.5, 8.0, "HK2", "2023-2024");
            createScore(studentSubjectRepository, sv003, db101, 9.0, 9.5, "HK2", "2023-2024");

            // Student SV004
            createScore(studentSubjectRepository, sv004, phys101, 3.0, 3.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv004, prog101, 5.5, 6.0, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv004, web101, 4.0, 4.5, "HK2", "2023-2024");

            // Student SV005
            createScore(studentSubjectRepository, sv005, math101, 7.0, 7.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv005, prog101, 8.0, 8.5, "HK1", "2023-2024");
            createScore(studentSubjectRepository, sv005, data101, 6.0, 6.5, "HK2", "2023-2024");
            createScore(studentSubjectRepository, sv005, web101, 9.0, 8.5, "HK2", "2023-2024");
            createScore(studentSubjectRepository, sv005, db101, 7.5, 8.0, "HK2", "2023-2024");

            log.info("Created sample scores for all students");
            log.info("Database initialization completed!");
        };
    }

    private void createScore(StudentSubjectRepository repository, Student student, Subject subject,
                           double processScore, double componentScore, String semester, String academicYear) {
        StudentSubject ss = StudentSubject.builder()
                .student(student)
                .subject(subject)
                .processScore(processScore)
                .componentScore(componentScore)
                .semester(semester)
                .academicYear(academicYear)
                .build();
        repository.save(ss);
    }

    private void createRoleIfNotExists(RoleRepository roleRepository, String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            log.info("✅ Role created: {}", roleName);
        }
    }

    private void createUserForStudent(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                                     Role userRole, Student student) {
        String username = student.getStudentCode();
        String password = student.getStudentCode().toLowerCase(); // sv001, sv002, etc.
        
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .email(student.getEmail())
                    .password(passwordEncoder.encode(password))
                    .studentCode(student.getStudentCode())
                    .enabled(true)
                    .roles(Set.of(userRole))
                    .build();
            
            userRepository.save(user);
            log.info("✅ Created USER account: username={}, password={}", username, password);
        }
    }
}
