import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { CourseDetailComponent } from './course-detail.component';
import { CourseService } from '../../core/services/course.service';
import { Course, DifficultyLevel } from '../../shared/models/course.interface';
import { createMockCourse, mockFullCourse } from '../../shared/testing/course-test-data';
import { signal } from '@angular/core';

describe('CourseDetailComponent', () => {
  let component: CourseDetailComponent;
  let fixture: ComponentFixture<CourseDetailComponent>;
  let courseService: Partial<CourseService>;
  let router: Partial<Router>;
  let activatedRoute: any;

  beforeEach(async () => {
    const getCourseByIdSpy = jest.fn().mockReturnValue(of(createMockCourse()));
    courseService = {
      getCourseById: getCourseByIdSpy,
      error: signal<string | null>(null),
      courses: signal<Course[]>([]),
      loading: signal<boolean>(false)
    };

    const navigateSpy = jest.fn();
    router = {
      navigate: navigateSpy
    };

    const paramMapGetSpy = jest.fn().mockReturnValue('1');
    activatedRoute = {
      snapshot: {
        paramMap: {
          get: paramMapGetSpy
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [CourseDetailComponent, HttpClientTestingModule],
      providers: [
        { provide: CourseService, useValue: courseService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: activatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseDetailComponent);
    component = fixture.componentInstance;
    // Get the injected service (which is our mock)
    const injectedService = TestBed.inject(CourseService);
    courseService = injectedService as Partial<CourseService>;
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load course on init from route params', (done) => {
    const mockCourse = createMockCourse();
    (courseService.getCourseById as jest.Mock).mockReturnValue(of(mockCourse));

    component.ngOnInit();

    expect(courseService.getCourseById).toHaveBeenCalledWith(1);
    // Wait for the observable to complete
    setTimeout(() => {
      expect(component.course()).toEqual(mockCourse);
      done();
    }, 0);
  });

  it('should fetch course and update signal when loadCourse is called', () => {
    const mockCourse = createMockCourse();
    (courseService.getCourseById as jest.Mock).mockReturnValue(of(mockCourse));

    component.loadCourse(1);

    expect(courseService.getCourseById).toHaveBeenCalledWith(1);
    expect(component.course()).toEqual(mockCourse);
    expect(component.loading()).toBe(false);
  });

  it('should handle 404 errors when loading course', () => {
    const error = { status: 404, error: { message: 'Course not found' } };
    (courseService.getCourseById as jest.Mock).mockReturnValue(throwError(() => error));

    component.loadCourse(999);

    expect(component.error()).toBeTruthy();
    expect(component.loading()).toBe(false);
  });

  it('should reload course on enrollment success', () => {
    const mockCourse = createMockCourse();
    component.course.set(mockCourse);
    (courseService.getCourseById as jest.Mock).mockReturnValue(of(mockCourse));

    component.onEnrollmentSuccess();

    expect(courseService.getCourseById).toHaveBeenCalledWith(mockCourse.id);
  });

  it('should navigate to home when goBack is called', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should return correct CSS classes for difficulty levels', () => {
    expect(component.getDifficultyClass(DifficultyLevel.BEGINNER)).toContain('green');
    expect(component.getDifficultyClass(DifficultyLevel.INTERMEDIATE)).toContain('yellow');
    expect(component.getDifficultyClass(DifficultyLevel.ADVANCED)).toContain('red');
  });

  it('should display loading state', () => {
    // Mock route to return null so ngOnInit doesn't load a course
    activatedRoute.snapshot.paramMap.get = jest.fn().mockReturnValue(null);
    const newFixture = TestBed.createComponent(CourseDetailComponent);
    const newComponent = newFixture.componentInstance;
    newComponent.course.set(null);
    newComponent.loading.set(true);
    newComponent.error.set(null);
    newFixture.detectChanges();

    const compiled = newFixture.nativeElement;
    const loadingText = compiled.textContent || '';
    expect(loadingText).toContain('Loading course details');
  });

  it('should display course full message when enrolledCount >= maxCapacity', () => {
    component.course.set(mockFullCourse);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const fullText = compiled.textContent;
    expect(fullText).toContain('Course Full');
  });

  it('should display enrollment form when course has available spots', () => {
    const mockCourse = createMockCourse({ enrolledCount: 5, maxCapacity: 50 });
    component.course.set(mockCourse);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('app-enrollment-form')).toBeTruthy();
  });
});
