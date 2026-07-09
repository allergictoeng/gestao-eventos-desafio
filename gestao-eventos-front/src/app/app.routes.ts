import { Routes } from '@angular/router';
import { ListComponent } from './features/events/list/list';
import { FormComponent } from './features/events/form/form';

export const routes: Routes = [
  { path: '', redirectTo: 'events', pathMatch: 'full' },
  { path: 'events', component: ListComponent },
  { path: 'events/new', component: FormComponent },
  { path: 'events/:id/edit', component: FormComponent },
];
