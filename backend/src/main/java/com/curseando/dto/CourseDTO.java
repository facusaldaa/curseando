package com.curseando.dto;

import com.curseando.model.DifficultyLevel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Course information")
public class CourseDTO {

    @Schema(description = "Course unique identifier", example = "1")
    private Long id;

    @Schema(description = "Course title", example = "Introduction to Java", required = true)
    private String title;

    @Schema(description = "Instructor name", example = "John Doe", required = true)
    private String instructor;

    @Schema(description = "Course duration", example = "40 hours", required = true)
    private String duration;

    @Schema(description = "Difficulty level", example = "BEGINNER", allowableValues = { "BEGINNER", "INTERMEDIATE",
            "ADVANCED" })
    private DifficultyLevel difficulty;

    @Schema(description = "Course description", example = "Learn Java programming fundamentals")
    private String description;

    @Schema(description = "Maximum enrollment capacity", example = "50", minimum = "1")
    private Integer maxCapacity;

    @Schema(description = "Current number of enrolled students", example = "25", minimum = "0")
    private Integer enrolledCount;

    public CourseDTO() {
    }

    public CourseDTO(Long id, String title, String instructor, String duration,
            DifficultyLevel difficulty, String description,
            Integer maxCapacity, Integer enrolledCount) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.duration = duration;
        this.difficulty = difficulty;
        this.description = description;
        this.maxCapacity = maxCapacity;
        this.enrolledCount = enrolledCount;
    }

    @Schema(description = "Available spots", example = "25", readOnly = true)
    public Integer getAvailableSpots() {
        return maxCapacity - enrolledCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(Integer enrolledCount) {
        this.enrolledCount = enrolledCount;
    }
}
