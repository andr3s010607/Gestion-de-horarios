import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  correo = '';
  contrasena = '';
  mostrarContrasena = false;
  cargando = false;
  error = '';
  mensaje = '';

  constructor(private readonly authService: AuthService, private readonly router: Router, private readonly toast: ToastService) {}

  iniciarSesion(): void {
    this.error = '';
    this.mensaje = '';

    if (!this.correo.trim() || !this.contrasena.trim()) {
      this.error = 'Ingresa tu correo institucional y contraseña para continuar.';
      this.toast.advertencia('Completa tus credenciales', this.error);
      return;
    }

    this.cargando = true;
    this.authService
      .iniciarSesion({ correo: this.correo.trim(), contrasena: this.contrasena })
      .subscribe({
        next: (respuesta) => {
          this.cargando = false;
          this.authService.guardarSesion(respuesta);
          this.mensaje = `Sesión iniciada como ${this.nombreRol(respuesta.rol)}.`;
          this.toast.exito('Sesión iniciada', `Bienvenido/a, ${this.nombreRol(respuesta.rol)}.`);
          this.router.navigate(['/inicio']);
        },
        error: () => {
          this.cargando = false;
          this.error = 'No pudimos verificar tus credenciales. Revisa los datos e inténtalo de nuevo.';
          this.toast.error('No fue posible iniciar sesión', this.error);
        },
      });
  }

  private nombreRol(rol: string): string {
    return rol.toLowerCase().replace('_', ' ');
  }
}
