package com.curseando.fixtures;

import com.curseando.model.Course;
import com.curseando.model.Enrollment;
import com.curseando.model.Student;

public class EnrollmentTestFixtures {

    public static Builder builder() {
        return new Builder();
    }

    public static Enrollment createDefaultEnrollment(Course course, Student student) {
        return builder()
                .withCourse(course)
                .withStudent(student)
                .build();
    }

    public static class Builder {
        private Long id = 1L;
        private Course course;
        private Student student;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withCourse(Course course) {
            this.course = course;
            return this;
        }

        public Builder withStudent(Student student) {
            this.student = student;
            return this;
        }

        public Enrollment build() {
            Enrollment enrollment = new Enrollment(course, student);
            enrollment.setId(id);
            return enrollment;
        }
    }
}
