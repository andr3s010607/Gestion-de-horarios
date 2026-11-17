import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RolUsuario } from '../models/enums';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const rolGuard: CanActivateFn = ruta => {
  const roles = (ruta.data['roles'] ?? []) as RolUsuario[];
  if (roles.length === 0 || inject(AuthService).tieneRol(roles)) {
    return true;
  }
  inject(ToastService).error('Acceso denegado', 'Tu rol no tiene permiso para entrar a esta sección.');
  return inject(Router).createUrlTree(['/dashboard']);
};

/** Un docente solo puede abrir el detalle de su propio perfil; el administrador, el de cualquiera. */
export const docentePropioGuard: CanActivateFn = ruta => {
  const auth = inject(AuthService);
  if (auth.esAdministrador()) {
    return true;
  }
  const propio = auth.sesion()?.docenteId;
  if (propio !== null && propio !== undefined && String(propio) === ruta.paramMap.get('id')) {
    return true;
  }
  inject(ToastService).error('Acceso denegado', 'Solo puedes consultar la información de tu propio perfil docente.');
  return inject(Router).createUrlTree(['/docentes']);
};
