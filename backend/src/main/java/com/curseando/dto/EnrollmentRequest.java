package com.curseando.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Enrollment request")
public class EnrollmentRequest {

    @Schema(description = "Course ID", example = "1", required = true)
    private Long courseId;

    @Schema(description = "Student full name", example = "Jane Smith", required = true, minLength = 2)
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 200, message = "Full name must be between 2 and 200 characters")
    private String fullName;

    @Schema(description = "Student email address", example = "jane.smith@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public EnrollmentRequest() {
    }

    public EnrollmentRequest(Long courseId, String fullName, String email) {
        this.courseId = courseId;
        this.fullName = fullName;
        this.email = email;
    }

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
