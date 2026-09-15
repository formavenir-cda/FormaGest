import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, take } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  return auth.user$.pipe(
    take(1),
    map((user) => {
      if (user) {
        return true;
      }
      router.navigate(['/login']);
      return false;
    }),
  );
};
