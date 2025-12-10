import { Component } from "@angular/core";

@Component({
  selector: 'app-root',
  standalone: false,
  template: `
    <div class="min-h-screen bg-gray-50">
      <header class="bg-white shadow-sm">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <h1 class="text-3xl font-bold text-primary-600">Curseando</h1>
          <p class="text-gray-600 mt-1">Online Course Platform</p>
        </div>
      </header>
      <main>
        <router-outlet></router-outlet>
      </main>
    </div>
  `
})
export class AppComponent {
  title = 'Curseando';
}

