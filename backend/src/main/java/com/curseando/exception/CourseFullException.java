package com.curseando.exception;

public class CourseFullException extends RuntimeException {

    public CourseFullException(Long courseId) {
        super("Course with ID " + courseId + " is full");
    }
}
