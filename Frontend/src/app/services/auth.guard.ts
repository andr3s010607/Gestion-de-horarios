import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.obtenerToken() ? true : router.createUrlTree(['/login']);
};

export const rolGuard = (roles: string[]): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.tieneAlgunoDeLosRoles(...roles) ? true : router.createUrlTree(['/inicio']);
};
