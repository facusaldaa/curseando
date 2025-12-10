package com.curseando.repository;

import com.curseando.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.student.email = :email")
    Optional<Enrollment> findByCourseIdAndStudentEmail(@Param("courseId") Long courseId,
            @Param("email") String email);

    boolean existsByCourseIdAndStudentEmail(Long courseId, String email);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
}
