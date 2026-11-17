import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { Icono } from '../icono/icono';

@Component({
  selector: 'app-estado-vacio',
  imports: [Icono],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host { display: flex; flex-direction: column; align-items: center; text-align: center; gap: 10px; padding: 48px 24px; }
    .circulo { display: grid; place-items: center; width: 64px; height: 64px; border-radius: 50%; background: var(--fresh-crown); color: var(--fig-leaf); }
    h3 { font-size: 17px; color: var(--texto); }
    p { margin: 0; max-width: 380px; color: var(--texto-suave); font-size: 14px; line-height: 1.5; }
    .acciones { margin-top: 8px; }
  `,
  template: `
    <span class="circulo"><app-icono [nombre]="icono()" [tamano]="28" /></span>
    <h3>{{ titulo() }}</h3>
    @if (mensaje()) { <p>{{ mensaje() }}</p> }
    <div class="acciones"><ng-content /></div>
  `
})
export class EstadoVacio {
  readonly icono = input('hoja');
  readonly titulo = input.required<string>();
  readonly mensaje = input('');
}
