import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-spinner',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; padding: 48px 16px; color: var(--texto-suave); font-size: 14px; }
    :host(.en-linea) { display: inline-flex; flex-direction: row; padding: 0; }
    .rueda { width: 32px; height: 32px; border: 3px solid var(--tan); border-top-color: var(--fig-leaf); border-radius: 50%; animation: girar 0.8s linear infinite; }
    :host(.en-linea) .rueda { width: 16px; height: 16px; border-width: 2px; border-color: rgba(255, 255, 255, 0.4); border-top-color: #fff; }
    @keyframes girar { to { transform: rotate(360deg); } }
    @media (prefers-reduced-motion: reduce) { .rueda { animation-duration: 2.4s; } }
  `,
  template: `
    <span class="rueda" role="status" [attr.aria-label]="texto() || 'Cargando'"></span>
    @if (texto()) { <span>{{ texto() }}</span> }
  `
})
export class Spinner {
  readonly texto = input('');
}
