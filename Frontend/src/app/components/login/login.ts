import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { mensajeDeError } from '../../services/interceptor.service';
import { Icono } from '../icono/icono';
import { Spinner } from '../spinner/spinner';

const LONGITUD_MAXIMA_NOMBRE_USUARIO = 60;

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, Icono, Spinner],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './login.css',
  templateUrl: './login.html'
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly longitudMaxima = LONGITUD_MAXIMA_NOMBRE_USUARIO;
  protected readonly enviando = signal(false);
  protected readonly errorServidor = signal<string | null>(null);
  protected readonly mostrarContrasena = signal(false);
  protected readonly sesionExpirada = inject(ActivatedRoute).snapshot.queryParamMap.get('motivo') === 'expirada';

  protected readonly formulario = this.formBuilder.nonNullable.group({
    nombreUsuario: ['', [Validators.required, Validators.maxLength(LONGITUD_MAXIMA_NOMBRE_USUARIO)]],
    contrasena: ['', Validators.required]
  });

  protected invalido(campo: 'nombreUsuario' | 'contrasena'): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && (control.touched || control.dirty);
  }

  protected iniciarSesion(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.errorServidor.set(null);
    const { nombreUsuario, contrasena } = this.formulario.getRawValue();
    this.auth.iniciarSesion({ nombreUsuario: nombreUsuario.trim(), contrasena }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (error: HttpErrorResponse) => {
        this.enviando.set(false);
        this.errorServidor.set(mensajeDeError(error));
      }
    });
  }
}
