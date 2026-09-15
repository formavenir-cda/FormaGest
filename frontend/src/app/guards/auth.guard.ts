import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, take } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';
import type { UserRole } from '../models/users/user-role';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const auth = inject(AuthService);
  const allowedRoles = route.data?.['roles'] as UserRole[] | undefined;

  return auth.user$.pipe(
    take(1),
    map((user) => {
      if (!user) {
        return router.createUrlTree(['/login']);
      }

      if (allowedRoles?.length && !allowedRoles.includes(user.role)) {
        return router.createUrlTree(['/']);
      }

      return true;
    }),
  );
};
