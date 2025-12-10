import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CourseService } from './course.service';
import { Course, DifficultyLevel } from '../../shared/models/course.interface';
import { createMockCourse, createMockCourses } from '../../shared/testing/course-test-data';
import { environment } from '../../../environments/environment';

describe('CourseService', () => {
  let service: CourseService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CourseService]
    });
    service = TestBed.inject(CourseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('loadCourses', () => {
    it('should fetch all courses and update signal', () => {
      const mockCourses = createMockCourses(3);

      service.loadCourses();

      expect(service.loading()).toBe(true);
      expect(service.error()).toBeNull();

      const req = httpMock.expectOne(`${environment.apiUrl}/courses`);
      expect(req.request.method).toBe('GET');
      req.flush(mockCourses);

      expect(service.courses()).toEqual(mockCourses);
      expect(service.loading()).toBe(false);
      expect(service.error()).toBeNull();
    });

    it('should filter by difficulty when provided', () => {
      const mockCourses = [createMockCourse({ difficulty: DifficultyLevel.BEGINNER })];

      service.loadCourses(DifficultyLevel.BEGINNER);

      const req = httpMock.expectOne(`${environment.apiUrl}/courses?difficulty=BEGINNER`);
      expect(req.request.method).toBe('GET');
      req.flush(mockCourses);

      expect(service.courses()).toEqual(mockCourses);
    });

    it('should handle errors and update error signal', () => {
      service.loadCourses();

      const req = httpMock.expectOne(`${environment.apiUrl}/courses`);
      req.error(new ProgressEvent('Network error'), { status: 500 });

      expect(service.loading()).toBe(false);
      expect(service.error()).toBeTruthy();
      expect(service.courses().length).toBe(0);
    });
  });

  describe('getCourseById', () => {
    it('should return course Observable', (done) => {
      const mockCourse = createMockCourse();

      service.getCourseById(1).subscribe(course => {
        expect(course).toEqual(mockCourse);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/courses/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockCourse);
    });

    it('should handle 404 errors', (done) => {
      service.getCourseById(999).subscribe({
        next: () => fail('should have failed'),
        error: (err) => {
          expect(err).toBeTruthy();
          expect(service.error()).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/courses/999`);
      req.error(new ProgressEvent('Not found'), { status: 404 });
    });
  });

  describe('updateCourse', () => {
    it('should update course in signal', () => {
      const initialCourses = createMockCourses(2);
      service.courses.set(initialCourses);

      service.updateCourse(1, { title: 'Updated Title' });

      const updatedCourse = service.courses().find(c => c.id === 1);
      expect(updatedCourse?.title).toBe('Updated Title');
      expect(service.courses().length).toBe(2);
    });
  });

  describe('availableCourses computed signal', () => {
    it('should filter courses with available spots', () => {
      const courses = [
        createMockCourse({ id: 1, enrolledCount: 10, maxCapacity: 50 }),
        createMockCourse({ id: 2, enrolledCount: 50, maxCapacity: 50 }), // Full
        createMockCourse({ id: 3, enrolledCount: 5, maxCapacity: 20 })
      ];

      service.courses.set(courses);

      const available = service.availableCourses();
      expect(available.length).toBe(2);
      expect(available.map(c => c.id)).toEqual([1, 3]);
    });
  });
});
