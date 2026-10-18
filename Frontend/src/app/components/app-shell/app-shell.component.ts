import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-shell',
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.css',
})
export class AppShellComponent {
  menuAbierto = false;
  readonly navegacion = [
    { ruta: '/inicio', etiqueta: 'Vista general', icono: '✦', roles: ['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'] },
    { ruta: '/academico/asignaturas', etiqueta: 'Plan de estudios', icono: '◌', roles: ['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'] },
    { ruta: '/academico/grupos', etiqueta: 'Grupos y carga', icono: '◇', roles: ['ADMINISTRADOR', 'PLANIFICADOR'] },
    { ruta: '/personas/docentes', etiqueta: 'Docentes', icono: '♧', roles: ['ADMINISTRADOR', 'PLANIFICADOR'] },
    { ruta: '/personas/restricciones', etiqueta: 'Disponibilidad', icono: '◷', roles: ['ADMINISTRADOR', 'PLANIFICADOR'] },
    { ruta: '/espacios/aulas', etiqueta: 'Aulas y recursos', icono: '⌑', roles: ['ADMINISTRADOR', 'PLANIFICADOR'] },
    { ruta: '/academico/requisitos', etiqueta: 'Requisitos', icono: '⊹', roles: ['ADMINISTRADOR', 'PLANIFICADOR'] },
    { ruta: '/horarios', etiqueta: 'Diseñar horario', icono: '▦', roles: ['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'] },
    { ruta: '/calendario', etiqueta: 'Calendario semanal', icono: '▤', roles: ['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'] },
    { ruta: '/administracion/parametros', etiqueta: 'Administración', icono: '⚙', roles: ['ADMINISTRADOR'] },
    { ruta: '/administracion/usuarios', etiqueta: 'Usuarios', icono: '♙', roles: ['ADMINISTRADOR'] },
  ];

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  get correo(): string {
    return localStorage.getItem('correo') ?? 'Usuario institucional';
  }

  get rol(): string {
    return this.auth.obtenerRol().replace('_', ' ').toLowerCase();
  }

  puedeVer(roles: string[]): boolean {
    return this.auth.tieneAlgunoDeLosRoles(...roles);
  }

  cerrarSesion(): void {
    this.auth.cerrarSesionLocal();
    this.router.navigate(['/login']);
  }
}
