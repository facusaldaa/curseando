package com.curseando.controller;

import com.curseando.dto.CourseDTO;
import com.curseando.model.DifficultyLevel;
import com.curseando.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@DisplayName("CourseController Tests")
class CourseControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CourseService courseService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("GET /api/courses should return all courses")
        void getAllCourses_ShouldReturnAllCourses() throws Exception {
                // Given
                CourseDTO course1 = new CourseDTO(1L, "Course 1", "Instructor 1", "40 hours",
                                DifficultyLevel.BEGINNER, "Description 1", 50, 25);
                CourseDTO course2 = new CourseDTO(2L, "Course 2", "Instructor 2", "60 hours",
                                DifficultyLevel.INTERMEDIATE, "Description 2", 30, 15);
                List<CourseDTO> courses = Arrays.asList(course1, course2);

                when(courseService.findAll()).thenReturn(courses);

                // When/Then
                mockMvc.perform(get("/api/courses"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].id").value(1L))
                                .andExpect(jsonPath("$[0].title").value("Course 1"))
                                .andExpect(jsonPath("$[1].id").value(2L))
                                .andExpect(jsonPath("$[1].title").value("Course 2"));
        }

        @Test
        @DisplayName("GET /api/courses?difficulty=BEGINNER should filter correctly")
        void getAllCourses_WithDifficultyFilter_ShouldFilterCorrectly() throws Exception {
                // Given
                CourseDTO beginnerCourse = new CourseDTO(1L, "Beginner Course", "Instructor",
                                "40 hours", DifficultyLevel.BEGINNER, "Description", 50, 25);
                List<CourseDTO> courses = Collections.singletonList(beginnerCourse);

                when(courseService.findByDifficulty(DifficultyLevel.BEGINNER)).thenReturn(courses);

                // When/Then
                mockMvc.perform(get("/api/courses")
                                .param("difficulty", "BEGINNER"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].difficulty").value("BEGINNER"));
        }

        @Test
        @DisplayName("GET /api/courses/{id} should return course when exists")
        void getCourseById_ShouldReturnCourseWhenExists() throws Exception {
                // Given
                CourseDTO course = new CourseDTO(1L, "Course 1", "Instructor 1", "40 hours",
                                DifficultyLevel.BEGINNER, "Description 1", 50, 25);

                when(courseService.findById(1L)).thenReturn(Optional.of(course));

                // When/Then
                mockMvc.perform(get("/api/courses/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("Course 1"))
                                .andExpect(jsonPath("$.instructor").value("Instructor 1"));
        }

        @Test
        @DisplayName("GET /api/courses/{id} should return 404 when not found")
        void getCourseById_ShouldReturn404WhenNotFound() throws Exception {
                // Given
                when(courseService.findById(999L)).thenReturn(Optional.empty());

                // When/Then
                mockMvc.perform(get("/api/courses/999"))
                                .andExpect(status().isNotFound());
        }
}
