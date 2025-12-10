import { Course, DifficultyLevel } from '../models/course.interface';

export const createMockCourse = (overrides?: Partial<Course>): Course => ({
  id: 1,
  title: 'Test Course',
  instructor: 'Test Instructor',
  duration: '40 hours',
  difficulty: DifficultyLevel.BEGINNER,
  description: 'Test course description',
  maxCapacity: 50,
  enrolledCount: 25,
  availableSpots: 25,
  ...overrides
});

export const createMockCourses = (count: number): Course[] => {
  return Array.from({ length: count }, (_, i) => 
    createMockCourse({
      id: i + 1,
      title: `Course ${i + 1}`,
      enrolledCount: i * 5
    })
  );
};

export const mockBeginnerCourse = createMockCourse({
  id: 1,
  title: 'Beginner Course',
  difficulty: DifficultyLevel.BEGINNER
});

export const mockIntermediateCourse = createMockCourse({
  id: 2,
  title: 'Intermediate Course',
  difficulty: DifficultyLevel.INTERMEDIATE
});

export const mockAdvancedCourse = createMockCourse({
  id: 3,
  title: 'Advanced Course',
  difficulty: DifficultyLevel.ADVANCED
});

export const mockFullCourse = createMockCourse({
  id: 4,
  title: 'Full Course',
  maxCapacity: 10,
  enrolledCount: 10,
  availableSpots: 0
});
