import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { EnrollmentRequest, EnrollmentResponse } from '../../shared/models/enrollment.interface';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EnrollmentService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  // State signals
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  success = signal<boolean>(false);

  enroll(request: EnrollmentRequest): Observable<EnrollmentResponse> {
    this.loading.set(true);
    this.error.set(null);
    this.success.set(false);

    return this.http.post<EnrollmentResponse>(`${this.apiUrl}/enrollments`, request).pipe(
      catchError(err => {
        const message = this.getErrorMessage(err);
        this.error.set(message);
        this.loading.set(false);
        this.success.set(false);
        return throwError(() => err);
      })
    );
  }

  private getErrorMessage(error: any): string {
    if (error.status === 409) {
      return error.error?.message || 'You are already enrolled in this course or the course is full';
    }
    if (error.status === 400) {
      return error.error?.message || 'Invalid enrollment data';
    }
    if (error.status === 404) {
      return error.error?.message || 'Course not found';
    }
    return error.error?.message || 'An error occurred. Please try again.';
  }
}

