package com.curseando.service;

import com.curseando.dto.CourseDTO;
import com.curseando.model.Course;
import com.curseando.model.DifficultyLevel;
import com.curseando.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDTO> findAll() {
        return courseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CourseDTO> findById(Long id) {
        return courseRepository.findById(id)
                .map(this::toDTO);
    }

    public List<CourseDTO> findByDifficulty(DifficultyLevel difficulty) {
        return courseRepository.findByDifficulty(difficulty).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Integer getAvailableSpots(Long courseId) {
        return courseRepository.findById(courseId)
                .map(Course::getAvailableSpots)
                .orElse(0);
    }

    public Course findCourseEntityById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    private CourseDTO toDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTitle(),
                course.getInstructor(),
                course.getDuration(),
                course.getDifficulty(),
                course.getDescription(),
                course.getMaxCapacity(),
                course.getEnrolledCount());
    }
}
