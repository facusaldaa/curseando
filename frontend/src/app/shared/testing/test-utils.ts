import { TestBed, ComponentFixture } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideRouter } from '@angular/router';

export const createTestBedConfig = (component: any, routes: any[] = []) => {
  return {
    imports: [
      component,
      HttpClientTestingModule,
      RouterTestingModule
    ],
    providers: [
      provideRouter(routes)
    ]
  };
};

export const flushPromises = () => new Promise(resolve => setTimeout(resolve, 0));
