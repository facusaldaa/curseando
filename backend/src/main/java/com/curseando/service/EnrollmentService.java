package com.curseando.service;

import com.curseando.dto.EnrollmentRequest;
import com.curseando.dto.EnrollmentResponse;
import com.curseando.exception.CourseFullException;
import com.curseando.exception.CourseNotFoundException;
import com.curseando.exception.DuplicateEnrollmentException;
import com.curseando.model.Course;
import com.curseando.model.Enrollment;
import com.curseando.model.Student;
import com.curseando.repository.CourseRepository;
import com.curseando.repository.EnrollmentRepository;
import com.curseando.repository.StudentRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(CourseRepository courseRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        // Find course with lock for concurrency handling
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(request.getCourseId()));

        // Validate enrollment
        validateEnrollment(course, request);

        // Find or create student
        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    Student newStudent = new Student(request.getFullName(), request.getEmail());
                    return studentRepository.save(newStudent);
                });

        // Create enrollment
        Enrollment enrollment = new Enrollment(course, student);
        enrollment = enrollmentRepository.save(enrollment);

        // Update enrolled count
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseRepository.save(course);

        // Build response
        return new EnrollmentResponse(
                enrollment.getId(),
                course.getId(),
                course.getTitle(),
                student.getFullName(),
                student.getEmail(),
                enrollment.getEnrollmentDate(),
                course.getAvailableSpots());
    }

    private void validateEnrollment(Course course, EnrollmentRequest request) {
        // Check if course is full
        if (course.isFull()) {
            throw new CourseFullException(course.getId());
        }

        // Check if student is already enrolled
        if (enrollmentRepository.existsByCourseIdAndStudentEmail(
                course.getId(), request.getEmail())) {
            throw new DuplicateEnrollmentException(request.getEmail());
        }
    }
}
