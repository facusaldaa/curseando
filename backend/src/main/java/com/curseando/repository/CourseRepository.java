package com.curseando.repository;

import com.curseando.model.Course;
import com.curseando.model.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByDifficulty(DifficultyLevel difficulty);

    @Query("SELECT c FROM Course c WHERE c.enrolledCount < c.maxCapacity")
    List<Course> findAvailableCourses();

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    long countEnrollmentsByCourseId(Long courseId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.enrollments WHERE c.id = :id")
    Optional<Course> findByIdWithEnrollments(Long id);
}
