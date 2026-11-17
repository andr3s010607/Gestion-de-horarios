import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { Icono } from '../icono/icono';

@Component({
  selector: 'app-confirm-dialog',
  imports: [Icono],
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { '(document:keydown.escape)': 'cancelar()' },
  styles: `
    .fondo { position: fixed; inset: 0; z-index: 900; display: grid; place-items: center; padding: 16px; background: rgba(35, 54, 26, 0.45); }
    .dialogo { width: min(420px, 100%); padding: 28px; border-radius: var(--radio-lg); background: var(--crema-claro); box-shadow: 0 24px 60px rgba(35, 54, 26, 0.3); }
    .marca { display: grid; place-items: center; width: 48px; height: 48px; margin-bottom: 16px; border-radius: 50%; background: var(--fresh-crown); color: var(--fig-leaf); }
    .marca.peligro { background: var(--error-fondo); color: var(--error); }
    h2 { font-size: 19px; margin-bottom: 8px; }
    p { margin: 0 0 24px; color: var(--texto-suave); line-height: 1.5; }
    .botones { display: flex; justify-content: flex-end; gap: 10px; }
  `,
  template: `
    @if (confirmacion.solicitud(); as solicitud) {
      <div class="fondo" (click)="cancelar()">
        <div class="dialogo" role="alertdialog" aria-modal="true" aria-labelledby="titulo-confirmacion" (click)="$event.stopPropagation()">
          <span class="marca" [class.peligro]="solicitud.peligro">
            <app-icono [nombre]="solicitud.peligro ? 'eliminar' : 'info'" [tamano]="22" />
          </span>
          <h2 id="titulo-confirmacion">{{ solicitud.titulo }}</h2>
          <p>{{ solicitud.mensaje }}</p>
          <div class="botones">
            <button type="button" class="btn btn-secundario" (click)="solicitud.responder(false)">Cancelar</button>
            <button type="button" class="btn" [class.btn-peligro]="solicitud.peligro" [class.btn-primario]="!solicitud.peligro"
                    (click)="solicitud.responder(true)">{{ solicitud.textoConfirmar }}</button>
          </div>
        </div>
      </div>
    }
  `
})
export class ConfirmDialog {
  protected readonly confirmacion = inject(ConfirmacionService);

  protected cancelar(): void {
    this.confirmacion.solicitud()?.responder(false);
  }
}
