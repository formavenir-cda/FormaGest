import { Routes } from '@angular/router';
import {Sectors} from './pages/sectors/sectors';
import {Login} from './pages/login/login';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'filieres',
    component: Sectors,
  },

];
