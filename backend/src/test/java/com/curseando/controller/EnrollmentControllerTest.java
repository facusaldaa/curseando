package com.curseando.controller;

import com.curseando.dto.EnrollmentRequest;
import com.curseando.dto.EnrollmentResponse;
import com.curseando.exception.CourseFullException;
import com.curseando.exception.CourseNotFoundException;
import com.curseando.exception.DuplicateEnrollmentException;
import com.curseando.service.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
@DisplayName("EnrollmentController Tests")
class EnrollmentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private EnrollmentService enrollmentService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /api/enrollments should create enrollment (201)")
        void enroll_ShouldCreateEnrollment() throws Exception {
                // Given
                EnrollmentRequest request = new EnrollmentRequest(1L, "John Doe", "john.doe@example.com");
                EnrollmentResponse response = new EnrollmentResponse(1L, 1L, "Test Course",
                                "John Doe", "john.doe@example.com", LocalDateTime.now(), 24);

                when(enrollmentService.enroll(any(EnrollmentRequest.class))).thenReturn(response);

                // When/Then
                mockMvc.perform(post("/api/enrollments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.courseId").value(1L))
                                .andExpect(jsonPath("$.studentName").value("John Doe"))
                                .andExpect(jsonPath("$.studentEmail").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("POST /api/enrollments should return 400 for invalid request")
        void enroll_ShouldReturn400ForInvalidRequest() throws Exception {
                // Given - missing required fields
                EnrollmentRequest invalidRequest = new EnrollmentRequest(1L, "", "");

                // When/Then
                mockMvc.perform(post("/api/enrollments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/enrollments should return 404 when course not found")
        void enroll_ShouldReturn404WhenCourseNotFound() throws Exception {
                // Given
                EnrollmentRequest request = new EnrollmentRequest(999L, "John Doe", "john.doe@example.com");

                when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                                .thenThrow(new CourseNotFoundException(999L));

                // When/Then
                mockMvc.perform(post("/api/enrollments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("CourseNotFoundException"));
        }

        @Test
        @DisplayName("POST /api/enrollments should return 409 for duplicate enrollment")
        void enroll_ShouldReturn409ForDuplicateEnrollment() throws Exception {
                // Given
                EnrollmentRequest request = new EnrollmentRequest(1L, "John Doe", "john.doe@example.com");

                when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                                .thenThrow(new DuplicateEnrollmentException("john.doe@example.com"));

                // When/Then
                mockMvc.perform(post("/api/enrollments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.error").value("DuplicateEnrollmentException"));
        }

        @Test
        @DisplayName("POST /api/enrollments should return 409 when course is full")
        void enroll_ShouldReturn409WhenCourseIsFull() throws Exception {
                // Given
                EnrollmentRequest request = new EnrollmentRequest(1L, "John Doe", "john.doe@example.com");

                when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                                .thenThrow(new CourseFullException(1L));

                // When/Then
                mockMvc.perform(post("/api/enrollments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.error").value("CourseFullException"));
        }

        @Test
        @DisplayName("POST /api/enrollments should return 400 for invalid email format")
        void enroll_ShouldReturn400ForInvalidEmailFormat() throws Exception {
                // Given - invalid email format
                EnrollmentRequest request = new EnrollmentRequest(1L, "John Doe", "invalid-email");

                // When/Then
                mockMvc.perform(post("/api/enrollments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }
}
