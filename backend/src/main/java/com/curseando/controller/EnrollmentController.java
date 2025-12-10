package com.curseando.controller;

import com.curseando.dto.EnrollmentRequest;
import com.curseando.dto.EnrollmentResponse;
import com.curseando.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Enrollment management API")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @Operation(summary = "Enroll in a course", description = "Enroll a student in a specific course. Validates course capacity and duplicate enrollment.", tags = {
            "Enrollments" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully enrolled", content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = com.curseando.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content(schema = @Schema(implementation = com.curseando.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Course full or duplicate enrollment", content = @Content(schema = @Schema(implementation = com.curseando.exception.ErrorResponse.class)))
    })
    public ResponseEntity<EnrollmentResponse> enroll(
            @RequestBody(description = "Enrollment request", required = true, content = @Content(schema = @Schema(implementation = EnrollmentRequest.class))) @Valid @org.springframework.web.bind.annotation.RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enroll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
