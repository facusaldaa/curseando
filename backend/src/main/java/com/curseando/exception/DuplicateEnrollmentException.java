package com.curseando.exception;

public class DuplicateEnrollmentException extends RuntimeException {

    public DuplicateEnrollmentException(String email) {
        super("Student with email " + email + " is already enrolled in this course");
    }
}
