import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  if (auth.sesionVigente()) {
    return true;
  }
  if (auth.token()) {
    auth.cerrarSesion('expirada');
    return false;
  }
  return inject(Router).createUrlTree(['/login']);
};

export const invitadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.sesionVigente() ? inject(Router).createUrlTree(['/dashboard']) : true;
};
