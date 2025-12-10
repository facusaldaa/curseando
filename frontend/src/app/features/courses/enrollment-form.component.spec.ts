import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { EnrollmentFormComponent } from './enrollment-form.component';
import { EnrollmentService } from '../../core/services/enrollment.service';
import { EnrollmentRequest, EnrollmentResponse } from '../../shared/models/enrollment.interface';
import { signal } from '@angular/core';

describe('EnrollmentFormComponent', () => {
  let component: EnrollmentFormComponent;
  let fixture: ComponentFixture<EnrollmentFormComponent>;
  let enrollmentService: Partial<EnrollmentService>;
  let enrollmentSuccessEmitted: boolean;

  const mockResponse: EnrollmentResponse = {
    id: 1,
    courseId: 1,
    courseTitle: 'Test Course',
    studentName: 'John Doe',
    studentEmail: 'john.doe@example.com',
    enrollmentDate: '2024-01-15T10:30:00',
    availableSpots: 24
  };

  beforeEach(async () => {
    const enrollSpy = jest.fn();
    enrollmentService = {
      enroll: enrollSpy,
      loading: signal<boolean>(false),
      error: signal<string | null>(null),
      success: signal<boolean>(false)
    };

    await TestBed.configureTestingModule({
      imports: [EnrollmentFormComponent, ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: EnrollmentService, useValue: enrollmentService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EnrollmentFormComponent);
    component = fixture.componentInstance;
    component.courseId = 1;
    enrollmentService = TestBed.inject(EnrollmentService);
    
    enrollmentSuccessEmitted = false;
    component.enrollmentSuccess.subscribe(() => {
      enrollmentSuccessEmitted = true;
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.enrollmentForm.get('fullName')?.value).toBe('');
    expect(component.enrollmentForm.get('email')?.value).toBe('');
  });

  it('should validate required fields', () => {
    const fullNameControl = component.enrollmentForm.get('fullName');
    const emailControl = component.enrollmentForm.get('email');

    expect(fullNameControl?.valid).toBeFalsy();
    expect(emailControl?.valid).toBeFalsy();

    fullNameControl?.setValue('John Doe');
    emailControl?.setValue('john.doe@example.com');

    expect(fullNameControl?.valid).toBeTruthy();
    expect(emailControl?.valid).toBeTruthy();
  });

  it('should validate email format', () => {
    const emailControl = component.enrollmentForm.get('email');
    
    emailControl?.setValue('invalid-email');
    expect(emailControl?.valid).toBeFalsy();

    emailControl?.setValue('valid@email.com');
    expect(emailControl?.valid).toBeTruthy();
  });

  it('should validate minimum length for fullName', () => {
    const fullNameControl = component.enrollmentForm.get('fullName');
    
    fullNameControl?.setValue('A');
    expect(fullNameControl?.valid).toBeFalsy();

    fullNameControl?.setValue('John Doe');
    expect(fullNameControl?.valid).toBeTruthy();
  });

  it('should call enrollment service with correct data on submit', () => {
    (enrollmentService.enroll as jest.Mock).mockReturnValue(of(mockResponse));
    
    component.enrollmentForm.patchValue({
      fullName: 'John Doe',
      email: 'john.doe@example.com'
    });

    component.onSubmit();

    const expectedRequest: EnrollmentRequest = {
      courseId: 1,
      fullName: 'John Doe',
      email: 'john.doe@example.com'
    };

    expect(enrollmentService.enroll).toHaveBeenCalledWith(expectedRequest);
  });

  it('should reset form on successful enrollment', () => {
    (enrollmentService.enroll as jest.Mock).mockReturnValue(of(mockResponse));
    
    component.enrollmentForm.patchValue({
      fullName: 'John Doe',
      email: 'john.doe@example.com'
    });

    component.onSubmit();

    expect(component.enrollmentForm.get('fullName')?.value).toBeNull();
    expect(component.enrollmentForm.get('email')?.value).toBeNull();
  });

  it('should emit enrollmentSuccess event on successful enrollment', () => {
    (enrollmentService.enroll as jest.Mock).mockReturnValue(of(mockResponse));
    
    component.enrollmentForm.patchValue({
      fullName: 'John Doe',
      email: 'john.doe@example.com'
    });

    component.onSubmit();

    expect(enrollmentSuccessEmitted).toBe(true);
  });

  it('should disable form during loading', () => {
    enrollmentService.loading = signal(true);
    fixture.detectChanges();

    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBe(true);
  });

  it('should display success message on successful enrollment', () => {
    enrollmentService.success = signal(true);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const successText = compiled.textContent;
    expect(successText).toContain('Enrollment successful');
  });

  it('should display error message on enrollment failure', () => {
    enrollmentService.error = signal('Course is full');
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const errorText = compiled.textContent;
    expect(errorText).toContain('Course is full');
  });

  it('should not submit if form is invalid', () => {
    component.enrollmentForm.patchValue({
      fullName: '',
      email: 'invalid-email'
    });

    component.onSubmit();

    expect(enrollmentService.enroll).not.toHaveBeenCalled();
  });

  it('should handle enrollment service errors', () => {
    const error = { status: 409, error: { message: 'Course is full' } };
    (enrollmentService.enroll as jest.Mock).mockReturnValue(throwError(() => error));
    
    component.enrollmentForm.patchValue({
      fullName: 'John Doe',
      email: 'john.doe@example.com'
    });

    component.onSubmit();

    // Error handling is done in the service
    expect(enrollmentService.enroll).toHaveBeenCalled();
  });
});
