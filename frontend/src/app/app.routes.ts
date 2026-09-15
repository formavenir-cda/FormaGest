import { Routes } from '@angular/router';
import { Sectors } from './pages/sectors/sectors';
import { Login } from './pages/login/login';
import { Layout } from './shared/layout/layout';
import { authGuard } from './guards/auth.guard';
import { Tracks } from './pages/tracks/tracks';
import { Formation } from './pages/formation/formation';

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
        ],
      },
      { path: 'filieres', redirectTo: 'formation/filieres' },
      { path: 'cursus', redirectTo: 'formation/cursus' },
    ],
  },
];
