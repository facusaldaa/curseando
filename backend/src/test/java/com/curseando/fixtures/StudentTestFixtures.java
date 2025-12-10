package com.curseando.fixtures;

import com.curseando.model.Student;

public class StudentTestFixtures {

    public static Builder builder() {
        return new Builder();
    }

    public static Student createDefaultStudent() {
        return builder().build();
    }

    public static class Builder {
        private Long id = 1L;
        private String fullName = "John Doe";
        private String email = "john.doe@example.com";

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Student build() {
            Student student = new Student(fullName, email);
            student.setId(id);
            return student;
        }
    }
}
