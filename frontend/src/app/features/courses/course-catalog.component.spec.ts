import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { CourseCatalogComponent } from './course-catalog.component';
import { CourseService } from '../../core/services/course.service';
import { Course, DifficultyLevel } from '../../shared/models/course.interface';
import { createMockCourses, mockBeginnerCourse, mockIntermediateCourse } from '../../shared/testing/course-test-data';
import { signal } from '@angular/core';

describe('CourseCatalogComponent', () => {
  let component: CourseCatalogComponent;
  let fixture: ComponentFixture<CourseCatalogComponent>;
  let courseService: Partial<CourseService>;
  let router: Partial<Router>;

  beforeEach(async () => {
    const loadCoursesSpy = jest.fn();
    courseService = {
      loadCourses: loadCoursesSpy,
      courses: signal<Course[]>([]),
      loading: signal<boolean>(false),
      error: signal<string | null>(null)
    };

    const navigateSpy = jest.fn();
    router = {
      navigate: navigateSpy
    };

    await TestBed.configureTestingModule({
      imports: [CourseCatalogComponent],
      providers: [
        { provide: CourseService, useValue: courseService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseCatalogComponent);
    component = fixture.componentInstance;
    courseService = TestBed.inject(CourseService);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadCourses on init', () => {
    component.ngOnInit();
    expect(courseService.loadCourses).toHaveBeenCalled();
  });

  it('should render course list', () => {
    const mockCourses = createMockCourses(3);
    courseService.courses = signal(mockCourses);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const courseCards = compiled.querySelectorAll('[class*="bg-white rounded-lg"]');
    expect(courseCards.length).toBeGreaterThan(0);
  });

  it('should update selectedDifficulty signal when filter button clicked', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement;
    const beginnerButton = Array.from(compiled.querySelectorAll('button'))
      .find((btn: any) => btn.textContent?.trim() === 'Beginner') as HTMLButtonElement;

    if (beginnerButton) {
      beginnerButton.click();
      fixture.detectChanges();
      expect(component.selectedDifficulty()).toBe(DifficultyLevel.BEGINNER);
    }
  });

  it('should call service with correct difficulty when selectDifficulty is called', () => {
    component.selectDifficulty(DifficultyLevel.BEGINNER);
    expect(courseService.loadCourses).toHaveBeenCalledWith(DifficultyLevel.BEGINNER);
  });

  it('should call service without filter when selectDifficulty(null) is called', () => {
    component.selectDifficulty(null);
    expect(courseService.loadCourses).toHaveBeenCalled();
  });

  it('should navigate to course detail when navigateToCourse is called', () => {
    component.navigateToCourse(1);
    expect(router.navigate).toHaveBeenCalledWith(['/courses', 1]);
  });

  it('should return correct CSS classes for difficulty levels', () => {
    expect(component.getDifficultyClass(DifficultyLevel.BEGINNER)).toContain('green');
    expect(component.getDifficultyClass(DifficultyLevel.INTERMEDIATE)).toContain('yellow');
    expect(component.getDifficultyClass(DifficultyLevel.ADVANCED)).toContain('red');
  });

  it('should display loading state', () => {
    courseService.loading = signal(true);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const loadingText = compiled.textContent;
    expect(loadingText).toContain('Loading courses');
  });

  it('should display error state', () => {
    courseService.error = signal('Test error message');
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const errorText = compiled.textContent;
    expect(errorText).toContain('Test error message');
  });

  it('should display empty state when no courses', () => {
    courseService.courses = signal([]);
    courseService.loading = signal(false);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const emptyText = compiled.textContent;
    expect(emptyText).toContain('No courses found');
  });

  it('should filter courses by selected difficulty', () => {
    const courses = [mockBeginnerCourse, mockIntermediateCourse];
    courseService.courses = signal(courses);
    component.selectedDifficulty.set(DifficultyLevel.BEGINNER);
    fixture.detectChanges();

    const filtered = component.filteredCourses();
    expect(filtered.length).toBe(1);
    expect(filtered[0].difficulty).toBe(DifficultyLevel.BEGINNER);
  });
});
