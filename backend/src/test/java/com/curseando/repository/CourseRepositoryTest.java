package com.curseando.repository;

import com.curseando.fixtures.CourseTestFixtures;
import com.curseando.fixtures.EnrollmentTestFixtures;
import com.curseando.fixtures.StudentTestFixtures;
import com.curseando.model.Course;
import com.curseando.model.DifficultyLevel;
import com.curseando.model.Enrollment;
import com.curseando.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("CourseRepository Tests")
class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course beginnerCourse;
    private Course intermediateCourse;
    private Course advancedCourse;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        beginnerCourse = CourseTestFixtures.builder()
                .withId(null)
                .withTitle("Beginner Course")
                .withDifficulty(DifficultyLevel.BEGINNER)
                .withMaxCapacity(50)
                .withEnrolledCount(10)
                .build();
        beginnerCourse = entityManager.persistAndFlush(beginnerCourse);

        intermediateCourse = CourseTestFixtures.builder()
                .withId(null)
                .withTitle("Intermediate Course")
                .withDifficulty(DifficultyLevel.INTERMEDIATE)
                .withMaxCapacity(30)
                .withEnrolledCount(20)
                .build();
        intermediateCourse = entityManager.persistAndFlush(intermediateCourse);

        advancedCourse = CourseTestFixtures.builder()
                .withId(null)
                .withTitle("Advanced Course")
                .withDifficulty(DifficultyLevel.ADVANCED)
                .withMaxCapacity(20)
                .withEnrolledCount(20) // Full course
                .build();
        advancedCourse = entityManager.persistAndFlush(advancedCourse);
    }

    @Test
    @DisplayName("findByDifficulty() should filter correctly")
    void findByDifficulty_ShouldFilterCorrectly() {
        // When
        List<Course> beginnerCourses = courseRepository.findByDifficulty(DifficultyLevel.BEGINNER);
        List<Course> intermediateCourses = courseRepository.findByDifficulty(DifficultyLevel.INTERMEDIATE);
        List<Course> advancedCourses = courseRepository.findByDifficulty(DifficultyLevel.ADVANCED);

        // Then
        assertThat(beginnerCourses).hasSize(1);
        assertThat(beginnerCourses.get(0).getDifficulty()).isEqualTo(DifficultyLevel.BEGINNER);
        assertThat(beginnerCourses.get(0).getTitle()).isEqualTo("Beginner Course");

        assertThat(intermediateCourses).hasSize(1);
        assertThat(intermediateCourses.get(0).getDifficulty()).isEqualTo(DifficultyLevel.INTERMEDIATE);

        assertThat(advancedCourses).hasSize(1);
        assertThat(advancedCourses.get(0).getDifficulty()).isEqualTo(DifficultyLevel.ADVANCED);
    }

    @Test
    @DisplayName("findAvailableCourses() should return only courses with available spots")
    void findAvailableCourses_ShouldReturnOnlyCoursesWithAvailableSpots() {
        // When
        List<Course> availableCourses = courseRepository.findAvailableCourses();

        // Then
        assertThat(availableCourses).hasSize(2); // beginner and intermediate, not advanced (full)
        assertThat(availableCourses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Beginner Course", "Intermediate Course");
        assertThat(availableCourses).allMatch(course -> course.getEnrolledCount() < course.getMaxCapacity());
    }

    @Test
    @DisplayName("countEnrollmentsByCourseId() should count correctly")
    void countEnrollmentsByCourseId_ShouldCountCorrectly() {
        // Given
        Student student1 = StudentTestFixtures.builder()
                .withId(null)
                .withFullName("Student 1")
                .withEmail("student1@example.com")
                .build();
        student1 = entityManager.persistAndFlush(student1);

        Student student2 = StudentTestFixtures.builder()
                .withId(null)
                .withFullName("Student 2")
                .withEmail("student2@example.com")
                .build();
        student2 = entityManager.persistAndFlush(student2);

        Enrollment enrollment1 = EnrollmentTestFixtures.builder()
                .withId(null)
                .withCourse(beginnerCourse)
                .withStudent(student1)
                .build();
        entityManager.persistAndFlush(enrollment1);

        Enrollment enrollment2 = EnrollmentTestFixtures.builder()
                .withId(null)
                .withCourse(beginnerCourse)
                .withStudent(student2)
                .build();
        entityManager.persistAndFlush(enrollment2);

        // When
        long count = courseRepository.countEnrollmentsByCourseId(beginnerCourse.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("countEnrollmentsByCourseId() should return 0 when no enrollments")
    void countEnrollmentsByCourseId_ShouldReturnZeroWhenNoEnrollments() {
        // When
        long count = courseRepository.countEnrollmentsByCourseId(intermediateCourse.getId());

        // Then
        assertThat(count).isEqualTo(0);
    }
}
