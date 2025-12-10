# Application Screenshots

This directory contains screenshots of the Curseando Online Course Platform application for documentation purposes.

## Screenshots Overview

The following screenshots have been captured to document the application's user interface and functionality:

### 1. Homepage - All Courses (`homepage-all-courses.png`)
- **Description**: Main course catalog page showing all available courses
- **Features**: 
  - Course filtering buttons (All, Beginner, Intermediate, Advanced)
  - Grid layout of course cards
  - Shows course details: instructor, duration, difficulty level, enrollment status, and availability
  - Displays 5 courses: Microservices Architecture, Web Development with Angular, Full-Stack Development, Advanced Spring Boot, and Introduction to Java

### 2. Beginner Filtered View (`beginner-filtered.png`)
- **Description**: Course catalog filtered to show only Beginner level courses
- **Features**: 
  - Shows courses with green "BEGINNER" difficulty badges
  - Demonstrates the filtering functionality
  - Displays: Web Development with Angular and Introduction to Java Programming

### 3. Intermediate Filtered View (`intermediate-filtered.png`)
- **Description**: Course catalog filtered to show only Intermediate level courses
- **Features**: 
  - Shows courses with yellow "INTERMEDIATE" difficulty badges
  - Demonstrates the filtering functionality
  - Displays: Full-Stack Development with Spring Boot and React, Advanced Spring Boot Development

### 4. Advanced Filtered View (`advanced-filtered.png`)
- **Description**: Course catalog filtered to show only Advanced level courses
- **Features**: 
  - Shows courses with red "ADVANCED" difficulty badges
  - Demonstrates the filtering functionality
  - Displays: Microservices Architecture with Docker

### 5. Course Detail & Enrollment Page (`course-detail-enrollment.png`)
- **Description**: Individual course detail page with enrollment form
- **Features**: 
  - Course information display
  - Enrollment form with Full Name and Email fields
  - "Enroll Now" button
  - Back to courses navigation button

### 6. Validation - Course Full (`validation-course-full.png`)
- **Description**: Error message displayed when attempting to enroll in a course that has reached maximum capacity
- **Features**: 
  - Shows "Course Full" message
  - Displays: "This course has reached its maximum capacity. Please check back later or explore other courses."
  - Shows enrollment status: 30/30 enrolled with 0 available spots
  - Example course: Advanced Spring Boot Development

### 7. Validation - Duplicate Enrollment (`validation-duplicate-enrollment.png`)
- **Description**: Error message displayed when attempting to enroll the same student (email) twice in a course
- **Features**: 
  - Shows error message: "Student with email {email} is already enrolled in this course"
  - Red error banner displayed above the enrollment form
  - Form fields remain filled with the attempted enrollment data
  - Demonstrates duplicate enrollment prevention

## Course Information Displayed

Each course card displays:
- **Course Title**: Name of the course
- **Instructor**: Name of the course instructor
- **Duration**: Total hours for the course
- **Difficulty Level**: Visual badge indicating BEGINNER (green), INTERMEDIATE (yellow), or ADVANCED (red)
- **Enrollment Status**: Current enrollment count vs. maximum capacity (e.g., "8/25 enrolled")
- **Availability**: Number of spots available or "Course Full" status

## Sample Courses Shown

1. **Microservices Architecture with Docker**
   - Instructor: Robert Johnson
   - Duration: 80 hours
   - Level: ADVANCED
   - Enrollment: 8/25 enrolled (17 spots available)

2. **Web Development with Angular**
   - Instructor: Sarah Williams
   - Duration: 50 hours
   - Level: BEGINNER
   - Enrollment: 38/40 enrolled (2 spots available)

3. **Full-Stack Development with Spring Boot and React**
   - Instructor: Michael Brown
   - Duration: 70 hours
   - Level: INTERMEDIATE
   - Enrollment: 12/35 enrolled (23 spots available)

4. **Advanced Spring Boot Development**
   - Instructor: Jane Smith
   - Duration: 60 hours
   - Level: INTERMEDIATE
   - Enrollment: 30/30 enrolled (Course Full)

5. **Introduction to Java Programming**
   - Instructor: John Doe
   - Duration: 40 hours
   - Level: BEGINNER
   - Enrollment: 17/50 enrolled (33 spots available)

## Application URL

All screenshots were captured from: `http://localhost:4200/`

## Validation Scenarios

The application includes robust validation to ensure data integrity:

1. **Course Full Validation**: Prevents enrollment when a course has reached its maximum capacity
2. **Duplicate Enrollment Validation**: Prevents the same student (identified by email) from enrolling multiple times in the same course
3. **Form Validation**: Client-side validation for required fields (Full Name and Email)

## Notes

- Screenshots are full-page captures showing the complete viewport
- The application uses a responsive design with a clean, modern UI
- Color-coded difficulty badges help users quickly identify course levels
- Real-time enrollment status helps users see course availability
- The "Course Full" status is displayed in red text for courses at capacity
- Error messages are displayed in red banners with clear, user-friendly messages
- Validation errors prevent invalid enrollments and maintain data integrity
