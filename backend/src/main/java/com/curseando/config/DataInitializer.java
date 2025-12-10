package com.curseando.config;

import com.curseando.model.Course;
import com.curseando.model.DifficultyLevel;
import com.curseando.repository.CourseRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer {

    private final CourseRepository courseRepository;

    public DataInitializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (courseRepository.count() == 0) {
            seedCourses();
        }
    }

    private void seedCourses() {
        // Beginner course with plenty of spots
        Course course1 = new Course(
                "Introduction to Java Programming",
                "John Doe",
                "40 hours",
                DifficultyLevel.BEGINNER,
                "Learn the fundamentals of Java programming language. This course covers basic syntax, object-oriented programming concepts, and hands-on projects to build your first Java applications.",
                50);
        course1.setEnrolledCount(15);
        courseRepository.save(course1);

        // Intermediate course with some spots left
        Course course2 = new Course(
                "Advanced Spring Boot Development",
                "Jane Smith",
                "60 hours",
                DifficultyLevel.INTERMEDIATE,
                "Master Spring Boot framework for building enterprise applications. Topics include REST APIs, database integration, security, and microservices architecture.",
                30);
        course2.setEnrolledCount(28);
        courseRepository.save(course2);

        // Advanced course with spots available
        Course course3 = new Course(
                "Microservices Architecture with Docker",
                "Robert Johnson",
                "80 hours",
                DifficultyLevel.ADVANCED,
                "Deep dive into microservices architecture patterns, containerization with Docker, orchestration with Kubernetes, and distributed system design principles.",
                25);
        course3.setEnrolledCount(8);
        courseRepository.save(course3);

        // Beginner course that's almost full
        Course course4 = new Course(
                "Web Development with Angular",
                "Sarah Williams",
                "50 hours",
                DifficultyLevel.BEGINNER,
                "Build modern web applications using Angular framework. Learn components, services, routing, forms, and state management. Perfect for beginners starting their web development journey.",
                40);
        course4.setEnrolledCount(38);
        courseRepository.save(course4);

        // Intermediate course with good availability
        Course course5 = new Course(
                "Full-Stack Development with Spring Boot and React",
                "Michael Brown",
                "70 hours",
                DifficultyLevel.INTERMEDIATE,
                "Comprehensive course covering both backend (Spring Boot) and frontend (React) development. Build complete full-stack applications with modern best practices.",
                35);
        course5.setEnrolledCount(12);
        courseRepository.save(course5);
    }
}
