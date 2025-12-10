package com.curseando.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleValidationErrors() should return 400 with validation errors")
    void handleValidationErrors_ShouldReturn400WithValidationErrors() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "email", "Email is required"));
        bindingResult.addError(new FieldError("object", "fullName", "Full name is required"));

        when(ex.getBindingResult()).thenReturn(bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationErrors(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Validation failed");
        assertThat(response.getBody().getMessage()).contains("email");
        assertThat(response.getBody().getMessage()).contains("fullName");
    }

    @Test
    @DisplayName("handleCourseNotFound() should return 404")
    void handleCourseNotFound_ShouldReturn404() {
        // Given
        CourseNotFoundException ex = new CourseNotFoundException(999L);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCourseNotFound(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("CourseNotFoundException");
        assertThat(response.getBody().getMessage()).contains("999");
    }

    @Test
    @DisplayName("handleCourseFull() should return 409")
    void handleCourseFull_ShouldReturn409() {
        // Given
        CourseFullException ex = new CourseFullException(1L);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCourseFull(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("CourseFullException");
        assertThat(response.getBody().getMessage()).contains("1");
    }

    @Test
    @DisplayName("handleDuplicateEnrollment() should return 409")
    void handleDuplicateEnrollment_ShouldReturn409() {
        // Given
        DuplicateEnrollmentException ex = new DuplicateEnrollmentException("test@example.com");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateEnrollment(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("DuplicateEnrollmentException");
        assertThat(response.getBody().getMessage()).contains("test@example.com");
    }

    @Test
    @DisplayName("handleGenericException() should return 500")
    void handleGenericException_ShouldReturn500() {
        // Given
        Exception ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}
