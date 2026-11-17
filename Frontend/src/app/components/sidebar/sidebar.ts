import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Icono } from '../icono/icono';
import { NAVEGACION } from '../layout/navegacion';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive, Icono],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './sidebar.css',
  templateUrl: './sidebar.html'
})
export class Sidebar {
  private readonly auth = inject(AuthService);

  readonly abierta = input(false);
  readonly cerrar = output<void>();

  protected readonly grupos = computed(() => {
    const rol = this.auth.rol();
    return NAVEGACION
      .map(grupo => ({ ...grupo, elementos: grupo.elementos.filter(elemento => rol && elemento.roles.includes(rol)) }))
      .filter(grupo => grupo.elementos.length > 0);
  });

  protected cerrarSesion(): void {
    this.auth.cerrarSesion();
  }
}
