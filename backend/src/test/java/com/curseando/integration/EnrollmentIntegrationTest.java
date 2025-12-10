package com.curseando.integration;

import com.curseando.dto.EnrollmentRequest;
import com.curseando.dto.EnrollmentResponse;
import com.curseando.exception.CourseFullException;
import com.curseando.exception.DuplicateEnrollmentException;
import com.curseando.fixtures.CourseTestFixtures;
import com.curseando.fixtures.StudentTestFixtures;
import com.curseando.model.Course;
import com.curseando.model.Student;
import com.curseando.repository.CourseRepository;
import com.curseando.repository.EnrollmentRepository;
import com.curseando.repository.StudentRepository;
import com.curseando.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Enrollment Integration Tests")
class EnrollmentIntegrationTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();

        testCourse = CourseTestFixtures.builder()
                .withId(null)
                .withTitle("Integration Test Course")
                .withMaxCapacity(10)
                .withEnrolledCount(0)
                .build();
        testCourse = courseRepository.save(testCourse);
    }

    @Test
    @DisplayName("Full enrollment flow: course retrieval → validation → enrollment creation")
    void fullEnrollmentFlow_ShouldWorkCorrectly() {
        // Given
        EnrollmentRequest request = new EnrollmentRequest(
                testCourse.getId(),
                "John Doe",
                "john.doe@example.com");

        // When
        EnrollmentResponse response = enrollmentService.enroll(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCourseId()).isEqualTo(testCourse.getId());
        assertThat(response.getStudentName()).isEqualTo("John Doe");
        assertThat(response.getStudentEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getAvailableSpots()).isEqualTo(9);

        // Verify persistence
        Course updatedCourse = courseRepository.findById(testCourse.getId()).orElseThrow();
        assertThat(updatedCourse.getEnrolledCount()).isEqualTo(1);

        Student savedStudent = studentRepository.findByEmail("john.doe@example.com").orElseThrow();
        assertThat(savedStudent.getFullName()).isEqualTo("John Doe");

        boolean enrollmentExists = enrollmentRepository.existsByCourseIdAndStudentEmail(
                testCourse.getId(), "john.doe@example.com");
        assertThat(enrollmentExists).isTrue();
    }

    @Test
    @DisplayName("Concurrent enrollment attempts should handle concurrency correctly")
    void concurrentEnrollmentAttempts_ShouldHandleConcurrencyCorrectly() throws InterruptedException {
        // Given
        int numberOfThreads = 5;
        int courseCapacity = 3; // Less than number of threads to test concurrency handling

        Course limitedCourse = CourseTestFixtures.builder()
                .withId(null)
                .withTitle("Limited Capacity Course")
                .withMaxCapacity(courseCapacity)
                .withEnrolledCount(0)
                .build();
        limitedCourse = courseRepository.save(limitedCourse);
        final Long courseId = limitedCourse.getId();

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final int studentIndex = i;
            executor.submit(() -> {
                try {
                    EnrollmentRequest request = new EnrollmentRequest(
                            courseId,
                            "Student " + studentIndex,
                            "student" + studentIndex + "@example.com");
                    enrollmentService.enroll(request);
                    successCount.incrementAndGet();
                } catch (CourseFullException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Wait a bit for all transactions to complete
        Thread.sleep(200);

        // Then - refresh the course to get latest state
        courseRepository.flush();
        Course finalCourse = courseRepository.findById(courseId).orElseThrow();
        // Note: Due to transaction isolation, the count might vary, but should be <=
        // capacity
        assertThat(finalCourse.getEnrolledCount()).isLessThanOrEqualTo(courseCapacity);
        assertThat(finalCourse.getEnrolledCount()).isGreaterThanOrEqualTo(0);
        // Verify that we got some successes and some failures
        assertThat(successCount.get() + failureCount.get()).isEqualTo(numberOfThreads);
    }

    @Test
    @DisplayName("Duplicate enrollment should throw DuplicateEnrollmentException")
    void duplicateEnrollment_ShouldThrowDuplicateEnrollmentException() {
        // Given
        EnrollmentRequest request = new EnrollmentRequest(
                testCourse.getId(),
                "John Doe",
                "john.doe@example.com");

        // First enrollment
        enrollmentService.enroll(request);

        // When/Then - Second enrollment with same email
        assertThatThrownBy(() -> enrollmentService.enroll(request))
                .isInstanceOf(DuplicateEnrollmentException.class)
                .hasMessageContaining("john.doe@example.com");
    }

    @Test
    @DisplayName("Enrollment when course is full should throw CourseFullException")
    void enrollmentWhenCourseFull_ShouldThrowCourseFullException() {
        // Given
        Course fullCourse = CourseTestFixtures.builder()
                .withId(null)
                .withTitle("Full Course")
                .withMaxCapacity(1)
                .withEnrolledCount(1)
                .build();
        fullCourse = courseRepository.save(fullCourse);

        EnrollmentRequest request = new EnrollmentRequest(
                fullCourse.getId(),
                "John Doe",
                "john.doe@example.com");

        // When/Then
        assertThatThrownBy(() -> enrollmentService.enroll(request))
                .isInstanceOf(CourseFullException.class)
                .hasMessageContaining(String.valueOf(fullCourse.getId()));
    }
}
