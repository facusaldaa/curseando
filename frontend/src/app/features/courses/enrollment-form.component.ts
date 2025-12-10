import { Component, Input, Output, EventEmitter, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { EnrollmentService } from '../../core/services/enrollment.service';
import { EnrollmentRequest } from '../../shared/models/enrollment.interface';

@Component({
  selector: 'app-enrollment-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="bg-white rounded-lg shadow-md p-6">
      <h3 class="text-xl font-semibold text-gray-900 mb-4">Enroll in this Course</h3>

      <form [formGroup]="enrollmentForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div>
          <label for="fullName" class="block text-sm font-medium text-gray-700 mb-1">
            Full Name *
          </label>
          <input
            id="fullName"
            type="text"
            formControlName="fullName"
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
            [class.border-red-300]="enrollmentForm.get('fullName')?.invalid && enrollmentForm.get('fullName')?.touched">
          @if (enrollmentForm.get('fullName')?.invalid && enrollmentForm.get('fullName')?.touched) {
            <p class="mt-1 text-sm text-red-600">Full name is required</p>
          }
        </div>

        <div>
          <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
            Email *
          </label>
          <input
            id="email"
            type="email"
            formControlName="email"
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
            [class.border-red-300]="enrollmentForm.get('email')?.invalid && enrollmentForm.get('email')?.touched">
          @if (enrollmentForm.get('email')?.invalid && enrollmentForm.get('email')?.touched) {
            <p class="mt-1 text-sm text-red-600">
              @if (enrollmentForm.get('email')?.errors?.['required']) {
                Email is required
              } @else if (enrollmentForm.get('email')?.errors?.['email']) {
                Please enter a valid email address
              }
            </p>
          }
        </div>

        <!-- Success Message -->
        @if (enrollmentService.success()) {
          <div class="bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded">
            Enrollment successful! You have been enrolled in this course.
          </div>
        }

        <!-- Error Message -->
        @if (enrollmentService.error(); as error) {
          <div class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded">
            {{ error }}
          </div>
        }

        <button
          type="submit"
          [disabled]="enrollmentForm.invalid || enrollmentService.loading()"
          class="w-full bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-md 
                 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
          @if (enrollmentService.loading()) {
            <span class="inline-block animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></span>
            Enrolling...
          } @else {
            Enroll Now
          }
        </button>
      </form>
    </div>
  `
})
export class EnrollmentFormComponent {
  @Input({ required: true }) courseId!: number;
  @Output() enrollmentSuccess = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  enrollmentService = inject(EnrollmentService);

  enrollmentForm = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]]
  });

  onSubmit(): void {
    if (this.enrollmentForm.valid) {
      const request: EnrollmentRequest = {
        courseId: this.courseId,
        fullName: this.enrollmentForm.value.fullName!,
        email: this.enrollmentForm.value.email!
      };

      this.enrollmentService.enroll(request).subscribe({
        next: () => {
          this.enrollmentService.success.set(true);
          this.enrollmentService.loading.set(false);
          this.enrollmentForm.reset();
          this.enrollmentSuccess.emit();
        },
        error: () => {
          // Error handling is done in the service
        }
      });
    }
  }
}

