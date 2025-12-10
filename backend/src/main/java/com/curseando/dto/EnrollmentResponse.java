package com.curseando.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Enrollment response")
public class EnrollmentResponse {

    @Schema(description = "Enrollment ID", example = "1")
    private Long id;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Course title", example = "Introduction to Java")
    private String courseTitle;

    @Schema(description = "Student name", example = "Jane Smith")
    private String studentName;

    @Schema(description = "Student email", example = "jane.smith@example.com")
    private String studentEmail;

    @Schema(description = "Enrollment date", example = "2024-01-15T10:30:00")
    private LocalDateTime enrollmentDate;

    @Schema(description = "Available spots remaining", example = "24")
    private Integer availableSpots;

    public EnrollmentResponse() {
    }

    public EnrollmentResponse(Long id, Long courseId, String courseTitle,
            String studentName, String studentEmail,
            LocalDateTime enrollmentDate, Integer availableSpots) {
        this.id = id;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.enrollmentDate = enrollmentDate;
        this.availableSpots = availableSpots;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }
}
