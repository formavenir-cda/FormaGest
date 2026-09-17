import { Routes } from '@angular/router';
import { Sectors } from './pages/sectors/sectors';
import { Login } from './pages/login/login';
import { Layout } from './shared/layout/layout';
import { authGuard } from './guards/auth.guard';
import { Tracks } from './pages/tracks/tracks';
import { Formation } from './pages/formation/formation';
import { Courses } from './pages/courses/courses';
import { Users } from './pages/users/users';
import { Students } from './pages/users/students/students';
import { Teachers } from './pages/users/teachers/teachers';
import { Students as StudentsDirectory } from './pages/students/students';
import { Teachers as TeachersDirectory } from './pages/teachers/teachers';
import { AdministrativeManagers } from './pages/users/administrative-managers/administrative-managers';
import { Administrators } from './pages/users/administrators/administrators';
import { Home } from './pages/home/home';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'students',
        component: StudentsDirectory,
        canActivate: [authGuard],
        data: {roles: ['ADMINISTRATIVE_MANAGER'] },
        title: "Élèves"
      },
      { path: 'teachers',
        component: TeachersDirectory,
        canActivate: [authGuard],
        data: {roles: ['ADMINISTRATIVE_MANAGER'] },
        title: "Formateurs"
      },
      {
        path: '', component: Home, pathMatch: 'full'
      },
      {
        path: 'formation',
        component: Formation,
        canActivate: [authGuard],
        data: { roles: ['ADMINISTRATIVE_MANAGER'] },
        title: 'Formation',
        children: [
          { path: '', redirectTo: 'filieres', pathMatch: 'full' },
          { path: 'filieres', component: Sectors },
          { path: 'cursus', component: Tracks },
          { path: 'cours', component: Courses },
        ],
      },
      { path: 'filieres', redirectTo: 'formation/filieres' },
      { path: 'cursus', redirectTo: 'formation/cursus' },
      { path: 'cours', redirectTo: 'formation/cours' },
      {
        path: 'users',
        component: Users,
        canActivate: [authGuard],
        data: { roles: ['ADMINISTRATOR'] },
        title: 'Utilisateurs',
        children: [
          { path: '', redirectTo: 'students', pathMatch: 'full' },
          { path: 'students', component: Students },
          { path: 'teachers', component: Teachers },
          { path: 'administrative-managers', component: AdministrativeManagers },
          { path: 'administrators', component: Administrators },
        ],
      },
      { path: 'administrative-managers', redirectTo: 'users/administrative-managers' },
      { path: 'administrators', redirectTo: 'users/administrators' },
      {
        path: 'home', component: Home,
      }
    ],
  },
];
