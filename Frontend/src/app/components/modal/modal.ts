import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { Icono } from '../icono/icono';

@Component({
  selector: 'app-modal',
  imports: [Icono],
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { '(document:keydown.escape)': 'cerrar.emit()' },
  styles: `
    .fondo { position: fixed; inset: 0; z-index: 800; display: grid; place-items: center; padding: 16px; background: rgba(36, 48, 40, 0.45); overflow-y: auto; }
    .ventana { width: min(var(--ancho), 100%); max-height: calc(100vh - 32px); display: flex; flex-direction: column; border-radius: var(--radio-lg); background: var(--crema-claro); box-shadow: 0 24px 60px rgba(36, 48, 40, 0.3); }
    header { display: flex; align-items: flex-start; gap: 12px; padding: 24px 24px 0; }
    .titulos { flex: 1; min-width: 0; }
    h2 { font-size: 20px; }
    .subtitulo { margin: 4px 0 0; color: var(--texto-suave); font-size: 14px; }
    .cuerpo { padding: 20px 24px 24px; overflow-y: auto; }
  `,
  template: `
    <div class="fondo" (click)="cerrar.emit()">
      <section class="ventana" role="dialog" aria-modal="true" aria-labelledby="titulo-modal"
               [style.--ancho]="ancho() + 'px'" (click)="$event.stopPropagation()">
        <header>
          <div class="titulos">
            <h2 id="titulo-modal">{{ titulo() }}</h2>
            @if (subtitulo()) { <p class="subtitulo">{{ subtitulo() }}</p> }
          </div>
          <button type="button" class="btn-icono" (click)="cerrar.emit()" aria-label="Cerrar">
            <app-icono nombre="cerrar" [tamano]="18" />
          </button>
        </header>
        <div class="cuerpo"><ng-content /></div>
      </section>
    </div>
  `
})
export class Modal {
  readonly titulo = input.required<string>();
  readonly subtitulo = input('');
  readonly ancho = input(520);
  readonly cerrar = output<void>();
}
