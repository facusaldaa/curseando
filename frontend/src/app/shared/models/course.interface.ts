export interface Course {
  id: number;
  title: string;
  instructor: string;
  duration: string;
  difficulty: DifficultyLevel;
  description: string;
  maxCapacity: number;
  enrolledCount: number;
  availableSpots?: number;
}

export enum DifficultyLevel {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

