import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { LoginRequest, RespuestaAutenticacion } from '../models/autenticacion.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  constructor(private readonly http: HttpClient) {}

  iniciarSesion(datos: LoginRequest): Observable<RespuestaAutenticacion> {
    return this.http.post<RespuestaAutenticacion>(`${this.apiUrl}/login`, datos);
  }

  guardarSesion(respuesta: RespuestaAutenticacion): void {
    localStorage.setItem('token', respuesta.token);
    localStorage.setItem('tipoToken', respuesta.tipoToken);
    localStorage.setItem('idUsuario', respuesta.id.toString());
    localStorage.setItem('correo', respuesta.correo);
    localStorage.setItem('rol', respuesta.rol);
  }

  obtenerToken(): string | null {
    return localStorage.getItem('token');
  }

  obtenerRol(): string {
    const rol = localStorage.getItem('rol') ?? '';
    return rol === 'CONSULTA' ? 'ESTUDIANTE' : rol;
  }

  tieneAlgunoDeLosRoles(...roles: string[]): boolean {
    return roles.includes(this.obtenerRol());
  }

  puedePlanificar(): boolean {
    return this.tieneAlgunoDeLosRoles('ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE');
  }

  esAdministrador(): boolean {
    return this.tieneAlgunoDeLosRoles('ADMINISTRADOR');
  }

  cerrarSesionLocal(): void {
    ['token', 'tipoToken', 'idUsuario', 'correo', 'rol'].forEach((clave) => localStorage.removeItem(clave));
  }
}
