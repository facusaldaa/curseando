package com.curseando.fixtures;

import com.curseando.model.Course;
import com.curseando.model.DifficultyLevel;

public class CourseTestFixtures {

    public static Builder builder() {
        return new Builder();
    }

    public static Course createDefaultCourse() {
        return builder().build();
    }

    public static Course createBeginnerCourse() {
        return builder()
                .withDifficulty(DifficultyLevel.BEGINNER)
                .build();
    }

    public static Course createFullCourse() {
        return builder()
                .withMaxCapacity(10)
                .withEnrolledCount(10)
                .build();
    }

    public static Course createCourseWithSpots(int maxCapacity, int enrolledCount) {
        return builder()
                .withMaxCapacity(maxCapacity)
                .withEnrolledCount(enrolledCount)
                .build();
    }

    public static class Builder {
        private Long id = 1L;
        private String title = "Test Course";
        private String instructor = "Test Instructor";
        private String duration = "40 hours";
        private DifficultyLevel difficulty = DifficultyLevel.BEGINNER;
        private String description = "Test course description";
        private Integer maxCapacity = 50;
        private Integer enrolledCount = 0;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withInstructor(String instructor) {
            this.instructor = instructor;
            return this;
        }

        public Builder withDuration(String duration) {
            this.duration = duration;
            return this;
        }

        public Builder withDifficulty(DifficultyLevel difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withMaxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Builder withEnrolledCount(Integer enrolledCount) {
            this.enrolledCount = enrolledCount;
            return this;
        }

        public Course build() {
            Course course = new Course(title, instructor, duration, difficulty, description, maxCapacity);
            course.setId(id);
            course.setEnrolledCount(enrolledCount);
            return course;
        }
    }
}
