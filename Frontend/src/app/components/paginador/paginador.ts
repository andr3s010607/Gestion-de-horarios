import { ChangeDetectionStrategy, Component, computed, input, model } from '@angular/core';
import { Icono } from '../icono/icono';

@Component({
  selector: 'app-paginador',
  imports: [Icono],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; padding: 14px 16px; border-top: 1px solid var(--borde); font-size: 13.5px; color: var(--texto-suave); }
    .controles { display: flex; align-items: center; gap: 6px; }
    .numero { min-width: 34px; height: 34px; padding: 0 8px; border: 1px solid transparent; border-radius: 999px; background: transparent; cursor: pointer; color: var(--texto); }
    .numero:hover { background: var(--crema); }
    .numero.activa { background: var(--moss-oscuro); color: var(--texto-claro); font-weight: 600; }
    .btn-icono { width: 34px; height: 34px; }
    .btn-icono:disabled { opacity: 0.4; cursor: not-allowed; }
  `,
  template: `
    <span>Mostrando {{ desde() }}–{{ hasta() }} de {{ total() }}</span>
    @if (totalPaginas() > 1) {
      <div class="controles">
        <button type="button" class="btn-icono" [disabled]="pagina() === 1" (click)="pagina.set(pagina() - 1)" aria-label="Página anterior">
          <app-icono nombre="izquierda" [tamano]="16" />
        </button>
        @for (numero of paginas(); track numero) {
          <button type="button" class="numero" [class.activa]="numero === pagina()" (click)="pagina.set(numero)"
                  [attr.aria-current]="numero === pagina() ? 'page' : null">{{ numero }}</button>
        }
        <button type="button" class="btn-icono" [disabled]="pagina() === totalPaginas()" (click)="pagina.set(pagina() + 1)" aria-label="Página siguiente">
          <app-icono nombre="derecha" [tamano]="16" />
        </button>
      </div>
    }
  `
})
export class Paginador {
  readonly total = input.required<number>();
  readonly tamano = input(10);
  readonly pagina = model(1);

  protected readonly totalPaginas = computed(() => Math.max(1, Math.ceil(this.total() / this.tamano())));
  protected readonly desde = computed(() => (this.total() === 0 ? 0 : (this.pagina() - 1) * this.tamano() + 1));
  protected readonly hasta = computed(() => Math.min(this.total(), this.pagina() * this.tamano()));
  protected readonly paginas = computed(() => {
    const total = this.totalPaginas();
    const actual = this.pagina();
    const inicio = Math.max(1, Math.min(actual - 2, total - 4));
    const fin = Math.min(total, inicio + 4);
    return Array.from({ length: fin - inicio + 1 }, (_, indice) => inicio + indice);
  });
}

export function paginar<T>(lista: T[], pagina: number, tamano: number): T[] {
  return lista.slice((pagina - 1) * tamano, pagina * tamano);
}
