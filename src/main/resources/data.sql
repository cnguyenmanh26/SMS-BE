-- Insert sample students
INSERT INTO students (student_code, full_name, gender, date_of_birth, class_name, course, email, phone_number, address, created_at, updated_at) VALUES
('SV001', 'Nguyễn Văn An', 'Nam', '2003-05-15', 'CNTT-K15', 'K15', 'nguyenvanan@email.com', '0901234567', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV002', 'Trần Thị Bình', 'Nữ', '2003-08-20', 'CNTT-K15', 'K15', 'tranthibinh@email.com', '0902234567', 'Hồ Chí Minh', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV003', 'Lê Văn Cường', 'Nam', '2003-03-10', 'CNTT-K15', 'K15', 'levancuong@email.com', '0903234567', 'Đà Nẵng', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV004', 'Phạm Thị Dung', 'Nữ', '2003-11-25', 'CNTT-K16', 'K16', 'phamthidung@email.com', '0904234567', 'Hải Phòng', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV005', 'Hoàng Văn Em', 'Nam', '2004-01-30', 'CNTT-K16', 'K16', 'hoangvanem@email.com', '0905234567', 'Cần Thơ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample subjects
INSERT INTO subjects (subject_code, subject_name, credit_hours, process_score_ratio, component_score_ratio, description, created_at, updated_at) VALUES
('MATH101', 'Toán cao cấp 1', 45, 0.3, 0.7, 'Môn toán cơ bản cho sinh viên năm nhất', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PHYS101', 'Vật lý đại cương', 45, 0.4, 0.6, 'Môn vật lý cơ bản', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROG101', 'Lập trình căn bản', 60, 0.3, 0.7, 'Học lập trình C/C++', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DATA101', 'Cấu trúc dữ liệu', 60, 0.4, 0.6, 'Học về cấu trúc dữ liệu và giải thuật', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('WEB101', 'Lập trình Web', 60, 0.3, 0.7, 'Học HTML, CSS, JavaScript', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DB101', 'Cơ sở dữ liệu', 45, 0.3, 0.7, 'Học về SQL và quản lý CSDL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample scores for students
-- Student SV001
INSERT INTO student_subjects (student_code, subject_id, process_score, component_score, semester, academic_year, created_at, updated_at) VALUES
('SV001', 1, 8.5, 7.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV001', 2, 7.0, 6.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV001', 3, 9.0, 8.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV001', 4, 6.5, 7.0, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Student SV002
INSERT INTO student_subjects (student_code, subject_id, process_score, component_score, semester, academic_year, created_at, updated_at) VALUES
('SV002', 1, 5.0, 4.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV002', 2, 6.0, 5.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV002', 3, 7.5, 8.0, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV002', 5, 8.0, 7.5, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Student SV003
INSERT INTO student_subjects (student_code, subject_id, process_score, component_score, semester, academic_year, created_at, updated_at) VALUES
('SV003', 1, 9.0, 8.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV003', 3, 8.5, 9.0, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV003', 4, 7.5, 8.0, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV003', 6, 9.0, 9.5, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Student SV004
INSERT INTO student_subjects (student_code, subject_id, process_score, component_score, semester, academic_year, created_at, updated_at) VALUES
('SV004', 2, 3.0, 3.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV004', 3, 5.5, 6.0, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV004', 5, 4.0, 4.5, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Student SV005
INSERT INTO student_subjects (student_code, subject_id, process_score, component_score, semester, academic_year, created_at, updated_at) VALUES
('SV005', 1, 7.0, 7.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV005', 3, 8.0, 8.5, 'HK1', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV005', 4, 6.0, 6.5, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV005', 5, 9.0, 8.5, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SV005', 6, 7.5, 8.0, 'HK2', '2023-2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
