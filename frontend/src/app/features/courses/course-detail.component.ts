import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CourseService } from '../../core/services/course.service';
import { EnrollmentFormComponent } from './enrollment-form.component';
import { Course, DifficultyLevel } from '../../shared/models/course.interface';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, EnrollmentFormComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <button
        (click)="goBack()"
        class="mb-6 text-primary-600 hover:text-primary-700 flex items-center">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
        </svg>
        Back to Courses
      </button>

      @if (loading()) {
        <div class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          <p class="mt-4 text-gray-600">Loading course details...</p>
        </div>
      }

      @if (error()) {
        <div class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded mb-6">
          {{ error() }}
        </div>
      }

      @if (course(); as courseData) {
        <div class="bg-white rounded-lg shadow-md p-8">
          <div class="flex items-start justify-between mb-6">
            <div>
              <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ courseData.title }}</h1>
              <p class="text-gray-600 text-lg">Instructor: {{ courseData.instructor }}</p>
            </div>
            <span
              [class]="getDifficultyClass(courseData.difficulty)"
              class="px-3 py-1 rounded text-sm font-medium">
              {{ courseData.difficulty }}
            </span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="bg-gray-50 rounded-lg p-4">
              <p class="text-sm text-gray-600 mb-1">Duration</p>
              <p class="text-lg font-semibold text-gray-900">{{ courseData.duration }}</p>
            </div>
            <div class="bg-gray-50 rounded-lg p-4">
              <p class="text-sm text-gray-600 mb-1">Enrolled</p>
              <p class="text-lg font-semibold text-gray-900">
                {{ courseData.enrolledCount }} / {{ courseData.maxCapacity }}
              </p>
            </div>
            <div class="bg-gray-50 rounded-lg p-4">
              <p class="text-sm text-gray-600 mb-1">Available Spots</p>
              <p class="text-lg font-semibold" [class.text-green-600]="courseData.enrolledCount < courseData.maxCapacity" [class.text-red-600]="courseData.enrolledCount >= courseData.maxCapacity">
                {{ courseData.maxCapacity - courseData.enrolledCount }}
              </p>
            </div>
          </div>

          <div class="mb-8">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Course Description</h2>
            <p class="text-gray-700 leading-relaxed">{{ courseData.description }}</p>
          </div>

          <div class="border-t pt-8">
            @if (courseData.enrolledCount >= courseData.maxCapacity) {
              <div class="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded">
                <p class="font-semibold">Course Full</p>
                <p class="text-sm mt-1">This course has reached its maximum capacity. Please check back later or explore other courses.</p>
              </div>
            } @else {
              <app-enrollment-form
                [courseId]="courseData.id"
                (enrollmentSuccess)="onEnrollmentSuccess()">
              </app-enrollment-form>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class CourseDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  courseService = inject(CourseService);

  course = signal<Course | null>(null);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  DifficultyLevel = DifficultyLevel;

  ngOnInit(): void {
    const courseId = this.route.snapshot.paramMap.get('id');
    if (courseId) {
      this.loadCourse(Number(courseId));
    }
  }

  loadCourse(id: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.courseService.getCourseById(id).subscribe({
      next: (course) => {
        this.course.set(course);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Failed to load course details');
        this.loading.set(false);
      }
    });
  }

  onEnrollmentSuccess(): void {
    const courseId = this.course()?.id;
    if (courseId) {
      this.loadCourse(courseId);
    }
  }

  goBack(): void {
    this.router.navigate(['/']);
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

