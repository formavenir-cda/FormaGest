import { Routes } from '@angular/router';
import {Sectors} from './pages/sectors/sectors';
import {Login} from './pages/login/login';
import {Layout} from './shared/layout/layout';
import {authGuard} from './guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'filieres', component: Sectors }
    ]
  },

];
