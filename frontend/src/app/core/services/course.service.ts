import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';
import { Course, DifficultyLevel } from '../../shared/models/course.interface';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  // State signals
  courses = signal<Course[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  // Computed signals
  availableCourses = computed(() =>
    this.courses().filter(c => c.enrolledCount < c.maxCapacity)
  );

  loadCourses(difficulty?: DifficultyLevel): void {
    this.loading.set(true);
    this.error.set(null);

    const url = difficulty
      ? `${this.apiUrl}/courses?difficulty=${difficulty}`
      : `${this.apiUrl}/courses`;

    this.http.get<Course[]>(url).subscribe({
      next: (courses) => {
        this.courses.set(courses);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.message || 'Failed to load courses');
        this.loading.set(false);
      }
    });
  }

  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/courses/${id}`).pipe(
      catchError(err => {
        this.error.set(err.message || 'Failed to load course');
        throw err;
      })
    );
  }

  updateCourse(courseId: number, updates: Partial<Course>): void {
    this.courses.update(courses =>
      courses.map(c => c.id === courseId ? { ...c, ...updates } : c)
    );
  }
}

