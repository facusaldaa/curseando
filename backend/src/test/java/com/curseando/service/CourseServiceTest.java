package com.curseando.service;

import com.curseando.dto.CourseDTO;
import com.curseando.fixtures.CourseTestFixtures;
import com.curseando.model.Course;
import com.curseando.model.DifficultyLevel;
import com.curseando.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = CourseTestFixtures.createDefaultCourse();
    }

    @Test
    @DisplayName("findAll() should return all courses as DTOs")
    void findAll_ShouldReturnAllCoursesAsDTOs() {
        // Given
        Course course1 = CourseTestFixtures.builder()
                .withId(1L)
                .withTitle("Course 1")
                .build();
        Course course2 = CourseTestFixtures.builder()
                .withId(2L)
                .withTitle("Course 2")
                .build();
        List<Course> courses = Arrays.asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(courses);

        // When
        List<CourseDTO> result = courseService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("Course 1");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("Course 2");
    }

    @Test
    @DisplayName("findAll() should return empty list when no courses exist")
    void findAll_ShouldReturnEmptyListWhenNoCourses() {
        // Given
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CourseDTO> result = courseService.findAll();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById() should return course DTO when exists")
    void findById_ShouldReturnCourseDTOWhenExists() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        Optional<CourseDTO> result = courseService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testCourse.getId());
        assertThat(result.get().getTitle()).isEqualTo(testCourse.getTitle());
        assertThat(result.get().getInstructor()).isEqualTo(testCourse.getInstructor());
        assertThat(result.get().getDifficulty()).isEqualTo(testCourse.getDifficulty());
    }

    @Test
    @DisplayName("findById() should return empty when not found")
    void findById_ShouldReturnEmptyWhenNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<CourseDTO> result = courseService.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByDifficulty() should filter by difficulty level")
    void findByDifficulty_ShouldFilterByDifficultyLevel() {
        // Given
        Course beginnerCourse = CourseTestFixtures.createBeginnerCourse();
        List<Course> beginnerCourses = Collections.singletonList(beginnerCourse);

        when(courseRepository.findByDifficulty(DifficultyLevel.BEGINNER))
                .thenReturn(beginnerCourses);

        // When
        List<CourseDTO> result = courseService.findByDifficulty(DifficultyLevel.BEGINNER);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDifficulty()).isEqualTo(DifficultyLevel.BEGINNER);
    }

    @Test
    @DisplayName("getAvailableSpots() should calculate correctly")
    void getAvailableSpots_ShouldCalculateCorrectly() {
        // Given
        Course course = CourseTestFixtures.createCourseWithSpots(50, 25);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // When
        Integer result = courseService.getAvailableSpots(1L);

        // Then
        assertThat(result).isEqualTo(25);
    }

    @Test
    @DisplayName("getAvailableSpots() should return 0 when course not found")
    void getAvailableSpots_ShouldReturnZeroWhenCourseNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Integer result = courseService.getAvailableSpots(999L);

        // Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("findCourseEntityById() should throw exception when not found")
    void findCourseEntityById_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> courseService.findCourseEntityById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Course not found with id: 999");
    }

    @Test
    @DisplayName("findCourseEntityById() should return course when exists")
    void findCourseEntityById_ShouldReturnCourseWhenExists() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        Course result = courseService.findCourseEntityById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testCourse.getId());
        assertThat(result.getTitle()).isEqualTo(testCourse.getTitle());
    }
}
