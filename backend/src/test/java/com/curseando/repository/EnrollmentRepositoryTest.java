package com.curseando.repository;

import com.curseando.fixtures.CourseTestFixtures;
import com.curseando.fixtures.EnrollmentTestFixtures;
import com.curseando.fixtures.StudentTestFixtures;
import com.curseando.model.Course;
import com.curseando.model.Enrollment;
import com.curseando.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("EnrollmentRepository Tests")
class EnrollmentRepositoryTest {

        @Autowired
        private TestEntityManager entityManager;

        @Autowired
        private EnrollmentRepository enrollmentRepository;

        private Course testCourse;
        private Student testStudent;
        private Enrollment enrollment;

        @BeforeEach
        void setUp() {
                entityManager.clear();

                testCourse = CourseTestFixtures.builder()
                                .withId(null)
                                .build();
                testCourse = entityManager.persistAndFlush(testCourse);

                testStudent = StudentTestFixtures.builder()
                                .withId(null)
                                .withFullName("John Doe")
                                .withEmail("john.doe@example.com")
                                .build();
                testStudent = entityManager.persistAndFlush(testStudent);

                enrollment = EnrollmentTestFixtures.builder()
                                .withId(null)
                                .withCourse(testCourse)
                                .withStudent(testStudent)
                                .build();
                enrollment = entityManager.persistAndFlush(enrollment);
        }

        @Test
        @DisplayName("existsByCourseIdAndStudentEmail() should return true when exists")
        void existsByCourseIdAndStudentEmail_ShouldReturnTrueWhenExists() {
                // When
                boolean exists = enrollmentRepository.existsByCourseIdAndStudentEmail(
                                testCourse.getId(), "john.doe@example.com");

                // Then
                assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("existsByCourseIdAndStudentEmail() should return false when not exists")
        void existsByCourseIdAndStudentEmail_ShouldReturnFalseWhenNotExists() {
                // When
                boolean exists = enrollmentRepository.existsByCourseIdAndStudentEmail(
                                testCourse.getId(), "nonexistent@example.com");

                // Then
                assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("existsByCourseIdAndStudentEmail() should return false for different course")
        void existsByCourseIdAndStudentEmail_ShouldReturnFalseForDifferentCourse() {
                // Given
                Course anotherCourse = CourseTestFixtures.builder()
                                .withId(null)
                                .withTitle("Another Course")
                                .build();
                anotherCourse = entityManager.persistAndFlush(anotherCourse);

                // When
                boolean exists = enrollmentRepository.existsByCourseIdAndStudentEmail(
                                anotherCourse.getId(), "john.doe@example.com");

                // Then
                assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("countByCourseId() should count enrollments correctly")
        void countByCourseId_ShouldCountEnrollmentsCorrectly() {
                // Given
                Student student2 = StudentTestFixtures.builder()
                                .withId(null)
                                .withFullName("Jane Doe")
                                .withEmail("jane.doe@example.com")
                                .build();
                student2 = entityManager.persistAndFlush(student2);

                Enrollment enrollment2 = EnrollmentTestFixtures.builder()
                                .withId(null)
                                .withCourse(testCourse)
                                .withStudent(student2)
                                .build();
                entityManager.persistAndFlush(enrollment2);

                // When
                long count = enrollmentRepository.countByCourseId(testCourse.getId());

                // Then
                assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("countByCourseId() should return 0 when no enrollments")
        void countByCourseId_ShouldReturnZeroWhenNoEnrollments() {
                // Given
                Course emptyCourse = CourseTestFixtures.builder()
                                .withId(null)
                                .withTitle("Empty Course")
                                .build();
                emptyCourse = entityManager.persistAndFlush(emptyCourse);

                // When
                long count = enrollmentRepository.countByCourseId(emptyCourse.getId());

                // Then
                assertThat(count).isEqualTo(0);
        }
}
