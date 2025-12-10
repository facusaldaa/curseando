package com.curseando.controller;

import com.curseando.dto.CourseDTO;
import com.curseando.model.DifficultyLevel;
import com.curseando.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course management API")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieve a list of all courses. Optionally filter by difficulty level.", tags = {
            "Courses" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved courses", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = com.curseando.exception.ErrorResponse.class)))
    })
    public ResponseEntity<List<CourseDTO>> getAllCourses(
            @Parameter(description = "Filter by difficulty level", example = "BEGINNER") @RequestParam(required = false) DifficultyLevel difficulty) {
        List<CourseDTO> courses = difficulty != null
                ? courseService.findByDifficulty(difficulty)
                : courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieve a specific course by its unique identifier", tags = {
            "Courses" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course found", content = @Content(schema = @Schema(implementation = CourseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content(schema = @Schema(implementation = com.curseando.exception.ErrorResponse.class)))
    })
    public ResponseEntity<CourseDTO> getCourseById(
            @Parameter(description = "Course ID", required = true, example = "1") @PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
