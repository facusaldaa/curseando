package com.curseando.service;

import com.curseando.dto.EnrollmentRequest;
import com.curseando.dto.EnrollmentResponse;
import com.curseando.exception.CourseFullException;
import com.curseando.exception.CourseNotFoundException;
import com.curseando.exception.DuplicateEnrollmentException;
import com.curseando.fixtures.CourseTestFixtures;
import com.curseando.fixtures.EnrollmentTestFixtures;
import com.curseando.fixtures.StudentTestFixtures;
import com.curseando.model.Course;
import com.curseando.model.Enrollment;
import com.curseando.model.Student;
import com.curseando.repository.CourseRepository;
import com.curseando.repository.EnrollmentRepository;
import com.curseando.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnrollmentService Tests")
class EnrollmentServiceTest {

        @Mock
        private CourseRepository courseRepository;

        @Mock
        private StudentRepository studentRepository;

        @Mock
        private EnrollmentRepository enrollmentRepository;

        @InjectMocks
        private EnrollmentService enrollmentService;

        private Course testCourse;
        private Student testStudent;
        private EnrollmentRequest enrollmentRequest;

        @BeforeEach
        void setUp() {
                testCourse = CourseTestFixtures.builder()
                                .withId(1L)
                                .withMaxCapacity(50)
                                .withEnrolledCount(25)
                                .build();

                testStudent = StudentTestFixtures.builder()
                                .withId(1L)
                                .withFullName("John Doe")
                                .withEmail("john.doe@example.com")
                                .build();

                enrollmentRequest = new EnrollmentRequest(1L, "John Doe", "john.doe@example.com");
        }

        @Test
        @DisplayName("enroll() should successfully enroll new student")
        void enroll_ShouldSuccessfullyEnrollNewStudent() {
                // Given
                when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
                when(enrollmentRepository.existsByCourseIdAndStudentEmail(1L, "john.doe@example.com"))
                                .thenReturn(false);
                when(studentRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
                when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

                Enrollment enrollment = EnrollmentTestFixtures.createDefaultEnrollment(testCourse, testStudent);
                enrollment.setId(1L);
                when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
                when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

                // When
                EnrollmentResponse response = enrollmentService.enroll(enrollmentRequest);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getCourseId()).isEqualTo(1L);
                assertThat(response.getStudentEmail()).isEqualTo("john.doe@example.com");
                assertThat(response.getStudentName()).isEqualTo("John Doe");

                // Verify interactions
                verify(courseRepository).findById(1L);
                verify(enrollmentRepository).existsByCourseIdAndStudentEmail(1L, "john.doe@example.com");
                verify(studentRepository).findByEmail("john.doe@example.com");
                verify(studentRepository).save(any(Student.class));
                verify(enrollmentRepository).save(any(Enrollment.class));

                // Verify enrolled count was incremented
                ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
                verify(courseRepository).save(courseCaptor.capture());
                assertThat(courseCaptor.getValue().getEnrolledCount()).isEqualTo(26);
        }

        @Test
        @DisplayName("enroll() should successfully enroll existing student")
        void enroll_ShouldSuccessfullyEnrollExistingStudent() {
                // Given
                when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
                when(enrollmentRepository.existsByCourseIdAndStudentEmail(1L, "john.doe@example.com"))
                                .thenReturn(false);
                when(studentRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testStudent));

                Enrollment enrollment = EnrollmentTestFixtures.createDefaultEnrollment(testCourse, testStudent);
                enrollment.setId(1L);
                when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
                when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

                // When
                EnrollmentResponse response = enrollmentService.enroll(enrollmentRequest);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getStudentEmail()).isEqualTo("john.doe@example.com");

                // Verify student was not created
                verify(studentRepository, never()).save(any(Student.class));
                verify(enrollmentRepository).save(any(Enrollment.class));
        }

        @Test
        @DisplayName("enroll() should throw CourseFullException when course is full")
        void enroll_ShouldThrowCourseFullExceptionWhenCourseIsFull() {
                // Given
                Course fullCourse = CourseTestFixtures.createFullCourse();
                when(courseRepository.findById(1L)).thenReturn(Optional.of(fullCourse));

                // When/Then
                assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                                .isInstanceOf(CourseFullException.class)
                                .hasMessageContaining("1");

                verify(enrollmentRepository, never()).save(any(Enrollment.class));
                verify(studentRepository, never()).save(any(Student.class));
        }

        @Test
        @DisplayName("enroll() should throw DuplicateEnrollmentException for duplicate email")
        void enroll_ShouldThrowDuplicateEnrollmentExceptionForDuplicateEmail() {
                // Given
                when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
                when(enrollmentRepository.existsByCourseIdAndStudentEmail(1L, "john.doe@example.com"))
                                .thenReturn(true);

                // When/Then
                assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                                .isInstanceOf(DuplicateEnrollmentException.class)
                                .hasMessageContaining("john.doe@example.com");

                verify(enrollmentRepository, never()).save(any(Enrollment.class));
                verify(studentRepository, never()).save(any(Student.class));
        }

        @Test
        @DisplayName("enroll() should throw CourseNotFoundException when course doesn't exist")
        void enroll_ShouldThrowCourseNotFoundExceptionWhenCourseDoesNotExist() {
                // Given
                when(courseRepository.findById(999L)).thenReturn(Optional.empty());
                EnrollmentRequest request = new EnrollmentRequest(999L, "John Doe", "john.doe@example.com");

                // When/Then
                assertThatThrownBy(() -> enrollmentService.enroll(request))
                                .isInstanceOf(CourseNotFoundException.class)
                                .hasMessageContaining("999");

                verify(enrollmentRepository, never()).save(any(Enrollment.class));
                verify(studentRepository, never()).save(any(Student.class));
        }

        @Test
        @DisplayName("enroll() should increment enrolled count after enrollment")
        void enroll_ShouldIncrementEnrolledCountAfterEnrollment() {
                // Given
                Course course = CourseTestFixtures.builder()
                                .withId(1L)
                                .withMaxCapacity(50)
                                .withEnrolledCount(10)
                                .build();

                when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
                when(enrollmentRepository.existsByCourseIdAndStudentEmail(1L, "john.doe@example.com"))
                                .thenReturn(false);
                when(studentRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
                when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

                Enrollment enrollment = EnrollmentTestFixtures.createDefaultEnrollment(course, testStudent);
                when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
                when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // When
                enrollmentService.enroll(enrollmentRequest);

                // Then
                ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
                verify(courseRepository).save(courseCaptor.capture());
                assertThat(courseCaptor.getValue().getEnrolledCount()).isEqualTo(11);
        }
}
