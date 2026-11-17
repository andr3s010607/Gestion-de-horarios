import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environment/environment';
import { Login, RespuestaAutenticacion } from '../models/auth';
import { RolUsuario } from '../models/enums';
import { conManejoDeErrorPropio } from './contexto-http';

const CLAVE_SESION = 'sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly sesionActual = signal<RespuestaAutenticacion | null>(this.leerSesionGuardada());

  readonly sesion = this.sesionActual.asReadonly();
  readonly rol = computed(() => this.sesionActual()?.rol ?? null);
  readonly esAdministrador = computed(() => this.rol() === 'ADMINISTRADOR');
  readonly esDocente = computed(() => this.rol() === 'DOCENTE');

  iniciarSesion(credenciales: Login): Observable<RespuestaAutenticacion> {
    return this.http
      .post<RespuestaAutenticacion>(`${environment.apiUrl}/auth/login`, credenciales, conManejoDeErrorPropio())
      .pipe(tap(respuesta => this.guardarSesion(respuesta)));
  }

  cerrarSesion(motivo?: 'expirada'): void {
    localStorage.removeItem(CLAVE_SESION);
    this.sesionActual.set(null);
    this.router.navigate(['/login'], motivo ? { queryParams: { motivo } } : {});
  }

  token(): string | null {
    return this.sesionActual()?.token ?? null;
  }

  sesionVigente(): boolean {
    const token = this.token();
    if (!token) {
      return false;
    }
    const expiracion = this.leerExpiracion(token);
    return expiracion === null || expiracion > Date.now();
  }

  tieneRol(roles: RolUsuario[]): boolean {
    const rol = this.rol();
    return rol !== null && roles.includes(rol);
  }

  private guardarSesion(respuesta: RespuestaAutenticacion): void {
    localStorage.setItem(CLAVE_SESION, JSON.stringify(respuesta));
    this.sesionActual.set(respuesta);
  }

  private leerSesionGuardada(): RespuestaAutenticacion | null {
    try {
      const guardada = localStorage.getItem(CLAVE_SESION);
      return guardada ? (JSON.parse(guardada) as RespuestaAutenticacion) : null;
    } catch {
      return null;
    }
  }

  private leerExpiracion(token: string): number | null {
    try {
      const carga = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const datos = JSON.parse(atob(carga));
      return typeof datos.exp === 'number' ? datos.exp * 1000 : null;
    } catch {
      return null;
    }
  }
}
