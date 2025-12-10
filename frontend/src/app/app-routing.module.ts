import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
	{ path: '', loadComponent: () => import('./features/courses/course-catalog.component').then(m => m.CourseCatalogComponent) },
	{ path: 'courses/:id', loadComponent: () => import('./features/courses/course-detail.component').then(m => m.CourseDetailComponent) },
	{ path: '**', redirectTo: '' }
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule {}
