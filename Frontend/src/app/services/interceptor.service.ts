import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environment/environment';
import { ErrorApi } from '../models/error-api';
import { AuthService } from './auth.service';
import { MANEJO_ERROR_PROPIO } from './contexto-http';
import { ToastService } from './toast.service';

export function leerCuerpo(error: HttpErrorResponse): ErrorApi | null {
  const cuerpo = error.error;
  if (cuerpo && typeof cuerpo === 'object' && 'mensaje' in cuerpo) {
    return cuerpo as ErrorApi;
  }
  if (typeof cuerpo === 'string') {
    try {
      const interpretado = JSON.parse(cuerpo);
      return interpretado && typeof interpretado === 'object' ? (interpretado as ErrorApi) : null;
    } catch {
      return null;
    }
  }
  return null;
}

export function mensajeDeError(error: HttpErrorResponse): string {
  const cuerpo = leerCuerpo(error);
  if (cuerpo?.mensaje) {
    return cuerpo.mensaje;
  }
  if (error.status === 0) {
    return 'No hay conexión con el servidor. Verifica que el backend esté encendido.';
  }
  if (error.status === 403) {
    return 'No tienes permiso para realizar esta acción.';
  }
  if (error.status === 404) {
    return 'El recurso solicitado no existe.';
  }
  return 'Ocurrió un error inesperado en el servidor.';
}

export function tituloDeError(estado: number): string {
  switch (estado) {
    case 0:
      return 'Sin conexión';
    case 400:
      return 'Datos inválidos';
    case 403:
      return 'Acceso denegado';
    case 404:
      return 'No encontrado';
    case 409:
      return 'Operación rechazada';
    case 429:
      return 'Demasiados intentos';
    default:
      return 'Error del servidor';
  }
}

export const authInterceptor: HttpInterceptorFn = (peticion, siguiente) => {
  const auth = inject(AuthService);
  const toast = inject(ToastService);
  const esLogin = peticion.url.endsWith('/auth/login');
  const esBackend = peticion.url.startsWith(environment.apiUrl);
  const token = esBackend ? auth.token() : null;

  if (token && !esLogin && !auth.sesionVigente()) {
    auth.cerrarSesion('expirada');
    return throwError(() => new HttpErrorResponse({ status: 401, statusText: 'Sesión expirada' }));
  }

  const peticionAutenticada = token && !esLogin
    ? peticion.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : peticion;

  return siguiente(peticionAutenticada).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !esLogin) {
        auth.cerrarSesion('expirada');
      } else if (!peticion.context.get(MANEJO_ERROR_PROPIO)) {
        const detalles = leerCuerpo(error)?.incidencias?.map(incidencia => incidencia.mensaje);
        toast.error(tituloDeError(error.status), mensajeDeError(error), detalles?.length ? detalles : undefined);
      }
      return throwError(() => error);
    })
  );
};
