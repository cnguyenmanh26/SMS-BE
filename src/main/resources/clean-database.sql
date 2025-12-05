-- Clean all data from Student Management System database
-- Run this script to reset the database to empty state

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Delete all data from tables (in correct order to avoid FK constraints)
TRUNCATE TABLE student_subjects;
TRUNCATE TABLE students;
TRUNCATE TABLE subjects;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Verify tables are empty
SELECT 'student_subjects' as table_name, COUNT(*) as record_count FROM student_subjects
UNION ALL
SELECT 'students', COUNT(*) FROM students
UNION ALL
SELECT 'subjects', COUNT(*) FROM subjects;
