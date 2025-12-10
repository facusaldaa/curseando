import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/courses/course-catalog.component').then(m => m.CourseCatalogComponent) },
  { path: 'courses/:id', loadComponent: () => import('./features/courses/course-detail.component').then(m => m.CourseDetailComponent) },
  { path: '**', redirectTo: '' }
];

