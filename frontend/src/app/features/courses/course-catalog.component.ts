import { Component, OnInit, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CourseService } from '../../core/services/course.service';
import { Course, DifficultyLevel } from '../../shared/models/course.interface';

@Component({
  selector: 'app-course-catalog',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h2 class="text-2xl font-bold text-gray-900 mb-6">Available Courses</h2>

      <!-- Filters -->
      <div class="mb-6 flex flex-wrap gap-2">
        <button
          (click)="selectDifficulty(null)"
          [class]="selectedDifficulty() === null 
            ? 'bg-primary-600 text-white' 
            : 'bg-white text-gray-700 hover:bg-gray-50'"
          class="px-4 py-2 rounded-md border border-gray-300 transition-colors">
          All
        </button>
        <button
          (click)="selectDifficulty(DifficultyLevel.BEGINNER)"
          [class]="selectedDifficulty() === DifficultyLevel.BEGINNER 
            ? 'bg-primary-600 text-white' 
            : 'bg-white text-gray-700 hover:bg-gray-50'"
          class="px-4 py-2 rounded-md border border-gray-300 transition-colors">
          Beginner
        </button>
        <button
          (click)="selectDifficulty(DifficultyLevel.INTERMEDIATE)"
          [class]="selectedDifficulty() === DifficultyLevel.INTERMEDIATE 
            ? 'bg-primary-600 text-white' 
            : 'bg-white text-gray-700 hover:bg-gray-50'"
          class="px-4 py-2 rounded-md border border-gray-300 transition-colors">
          Intermediate
        </button>
        <button
          (click)="selectDifficulty(DifficultyLevel.ADVANCED)"
          [class]="selectedDifficulty() === DifficultyLevel.ADVANCED 
            ? 'bg-primary-600 text-white' 
            : 'bg-white text-gray-700 hover:bg-gray-50'"
          class="px-4 py-2 rounded-md border border-gray-300 transition-colors">
          Advanced
        </button>
      </div>

      <!-- Loading State -->
      @if (courseService.loading()) {
        <div class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          <p class="mt-4 text-gray-600">Loading courses...</p>
        </div>
      }

      <!-- Error State -->
      @if (courseService.error(); as error) {
        <div class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded mb-6">
          {{ error }}
        </div>
      }

      <!-- Course Grid -->
      @if (!courseService.loading() && !courseService.error()) {
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          @for (course of filteredCourses(); track course.id) {
            <div
              (click)="navigateToCourse(course.id)"
              class="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow cursor-pointer">
              <h3 class="text-xl font-semibold text-gray-900 mb-2">{{ course.title }}</h3>
              <p class="text-gray-600 text-sm mb-2">Instructor: {{ course.instructor }}</p>
              <p class="text-gray-500 text-xs mb-4">{{ course.duration }}</p>
              
              <div class="flex items-center justify-between mb-4">
                <span
                  [class]="getDifficultyClass(course.difficulty)"
                  class="px-2 py-1 rounded text-xs font-medium">
                  {{ course.difficulty }}
                </span>
                <span class="text-sm text-gray-600">
                  {{ course.enrolledCount }}/{{ course.maxCapacity }} enrolled
                </span>
              </div>

              <div class="mt-4">
                @if (course.enrolledCount >= course.maxCapacity) {
                  <span class="text-red-600 text-sm font-medium">Course Full</span>
                } @else {
                  <span class="text-green-600 text-sm font-medium">
                    {{ course.maxCapacity - course.enrolledCount }} spots available
                  </span>
                }
              </div>
            </div>
          } @empty {
            <div class="col-span-full text-center py-12 text-gray-600">
              No courses found
            </div>
          }
        </div>
      }
    </div>
  `
})
export class CourseCatalogComponent implements OnInit {
  courseService = inject(CourseService);
  private router = inject(Router);

  // Component-level signals
  selectedDifficulty = signal<DifficultyLevel | null>(null);
  DifficultyLevel = DifficultyLevel;

  // Computed signal combining service and component state
  filteredCourses = computed(() => {
    const courses = this.courseService.courses();
    const difficulty = this.selectedDifficulty();

    if (!difficulty) return courses;
    return courses.filter(c => c.difficulty === difficulty);
  });

  ngOnInit(): void {
    this.courseService.loadCourses();
  }

  selectDifficulty(difficulty: DifficultyLevel | null): void {
    this.selectedDifficulty.set(difficulty);
    if (difficulty) {
      this.courseService.loadCourses(difficulty);
    } else {
      this.courseService.loadCourses();
    }
  }

  navigateToCourse(id: number): void {
    this.router.navigate(['/courses', id]);
  }

  getDifficultyClass(difficulty: DifficultyLevel): string {
    switch (difficulty) {
      case DifficultyLevel.BEGINNER:
        return 'bg-green-100 text-green-800';
      case DifficultyLevel.INTERMEDIATE:
        return 'bg-yellow-100 text-yellow-800';
      case DifficultyLevel.ADVANCED:
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }
}

