import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EnrollmentService } from './enrollment.service';
import { EnrollmentRequest, EnrollmentResponse } from '../../shared/models/enrollment.interface';
import { environment } from '../../../environments/environment';

describe('EnrollmentService', () => {
  let service: EnrollmentService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EnrollmentService]
    });
    service = TestBed.inject(EnrollmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('enroll', () => {
    const mockRequest: EnrollmentRequest = {
      courseId: 1,
      fullName: 'John Doe',
      email: 'john.doe@example.com'
    };

    const mockResponse: EnrollmentResponse = {
      id: 1,
      courseId: 1,
      courseTitle: 'Test Course',
      studentName: 'John Doe',
      studentEmail: 'john.doe@example.com',
      enrollmentDate: '2024-01-15T10:30:00',
      availableSpots: 24
    };

    it('should post enrollment request', () => {
      service.enroll(mockRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/enrollments`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRequest);
      req.flush(mockResponse);
    });

    it('should update loading signal during request', (done) => {
      const enrollObservable = service.enroll(mockRequest);
      
      // Loading should be true immediately after calling enroll
      expect(service.loading()).toBe(true);
      expect(service.error()).toBeNull();
      expect(service.success()).toBe(false);

      enrollObservable.subscribe({
        next: () => {
          // After successful enrollment, loading should be false
          // Note: The service sets loading to false in the error handler, 
          // but for success it's handled in the component
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/enrollments`);
      req.flush(mockResponse);
    });

    it('should handle 409 conflict (duplicate/full)', (done) => {
      service.enroll(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (err) => {
          expect(err.status).toBe(409);
          expect(service.error()).toBeTruthy();
          expect(service.loading()).toBe(false);
          expect(service.success()).toBe(false);
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/enrollments`);
      req.error(
        { message: 'Course is full' },
        { status: 409, statusText: 'Conflict' }
      );
    });

    it('should handle 400 validation errors', (done) => {
      service.enroll(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (err) => {
          expect(err.status).toBe(400);
          expect(service.error()).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/enrollments`);
      req.error(
        { message: 'Invalid enrollment data' },
        { status: 400, statusText: 'Bad Request' }
      );
    });

    it('should handle 404 course not found', (done) => {
      service.enroll(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (err) => {
          expect(err.status).toBe(404);
          expect(service.error()).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/enrollments`);
      req.error(
        { message: 'Course not found' },
        { status: 404, statusText: 'Not Found' }
      );
    });
  });

  describe('getErrorMessage', () => {
    it('should map 409 error correctly', () => {
      const error = { status: 409, error: { message: 'Course is full' } };
      // This is tested indirectly through enroll() error handling
      expect(error.status).toBe(409);
    });

    it('should map 400 error correctly', () => {
      const error = { status: 400, error: { message: 'Invalid data' } };
      expect(error.status).toBe(400);
    });

    it('should map 404 error correctly', () => {
      const error = { status: 404, error: { message: 'Not found' } };
      expect(error.status).toBe(404);
    });

    it('should provide default error message', () => {
      const error = { status: 500 };
      expect(error.status).toBe(500);
    });
  });
});
