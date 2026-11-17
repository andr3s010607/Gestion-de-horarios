import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

type Trazo =
  | { tipo: 'ruta'; d: string }
  | { tipo: 'circulo'; cx: number; cy: number; r: number }
  | { tipo: 'rectangulo'; x: number; y: number; ancho: number; alto: number; rx: number };

const ruta = (d: string): Trazo => ({ tipo: 'ruta', d });
const circulo = (cx: number, cy: number, r: number): Trazo => ({ tipo: 'circulo', cx, cy, r });
const rectangulo = (x: number, y: number, ancho: number, alto: number, rx: number): Trazo => ({
  tipo: 'rectangulo', x, y, ancho, alto, rx
});

const ICONOS: Record<string, Trazo[]> = {
  panel: [rectangulo(3, 3, 7, 7, 1.5), rectangulo(14, 3, 7, 7, 1.5), rectangulo(14, 14, 7, 7, 1.5), rectangulo(3, 14, 7, 7, 1.5)],
  libro: [ruta('M2 4h6a4 4 0 0 1 4 4v13a3 3 0 0 0-3-3H2z'), ruta('M22 4h-6a4 4 0 0 0-4 4v13a3 3 0 0 1 3-3h7z')],
  edificio: [
    ruta('M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z'), ruta('M6 12H4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h2'),
    ruta('M18 9h2a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2h-2'), ruta('M10 6h4'), ruta('M10 10h4'), ruta('M10 14h4'), ruta('M10 18h4')
  ],
  personas: [
    ruta('M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2'), circulo(9, 7, 4),
    ruta('M22 21v-2a4 4 0 0 0-3-3.87'), ruta('M16 3.13a4 4 0 0 1 0 7.75')
  ],
  calendario: [rectangulo(3, 4, 18, 18, 2.5), ruta('M16 2v4'), ruta('M8 2v4'), ruta('M3 10h18')],
  buscar: [circulo(11, 11, 7.5), ruta('m21 21-4.3-4.3')],
  ajustes: [
    ruta('M4 21v-7'), ruta('M4 10V3'), ruta('M12 21v-9'), ruta('M12 8V3'), ruta('M20 21v-5'), ruta('M20 12V3'),
    ruta('M2 14h4'), ruta('M10 8h4'), ruta('M18 16h4')
  ],
  usuario: [ruta('M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2'), circulo(12, 7, 4)],
  escudo: [ruta('M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z'), ruta('m9 12 2 2 4-4')],
  salir: [ruta('M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4'), ruta('m16 17 5-5-5-5'), ruta('M21 12H9')],
  menu: [ruta('M4 6h16'), ruta('M4 12h16'), ruta('M4 18h16')],
  cerrar: [ruta('M18 6 6 18'), ruta('m6 6 12 12')],
  mas: [ruta('M12 5v14'), ruta('M5 12h14')],
  editar: [ruta('M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z'), ruta('m15 5 4 4')],
  eliminar: [ruta('M3 6h18'), ruta('M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6'), ruta('M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2')],
  descargar: [ruta('M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4'), ruta('m7 10 5 5 5-5'), ruta('M12 15V3')],
  check: [ruta('M20 6 9 17l-5-5')],
  alerta: [ruta('m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3'), ruta('M12 9v4'), ruta('M12 17h.01')],
  info: [circulo(12, 12, 10), ruta('M12 16v-4'), ruta('M12 8h.01')],
  reloj: [circulo(12, 12, 10), ruta('M12 6v6l4 2')],
  hoja: [
    ruta('M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z'),
    ruta('M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12')
  ],
  izquierda: [ruta('m15 18-6-6 6-6')],
  derecha: [ruta('m9 18 6-6-6-6')],
  ojo: [ruta('M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z'), circulo(12, 12, 3)],
  ojoCerrado: [
    ruta('M9.88 9.88a3 3 0 1 0 4.24 4.24'), ruta('M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68'),
    ruta('M6.61 6.61A13.53 13.53 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61'), ruta('M2 2l20 20')
  ],
  recargar: [
    ruta('M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8'), ruta('M21 3v5h-5'),
    ruta('M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16'), ruta('M8 16H3v5')
  ],
  varita: [
    ruta('m21.64 3.64-1.28-1.28a1.21 1.21 0 0 0-1.72 0L2.36 18.64a1.21 1.21 0 0 0 0 1.72l1.28 1.28a1.2 1.2 0 0 0 1.72 0L21.64 5.36a1.2 1.2 0 0 0 0-1.72'),
    ruta('m14 7 3 3'), ruta('M5 6v4'), ruta('M19 14v4'), ruta('M10 2v2'), ruta('M7 8H3'), ruta('M21 16h-4'), ruta('M11 3H9')
  ],
  candado: [rectangulo(3, 11, 18, 11, 2), ruta('M7 11V7a5 5 0 0 1 10 0v4')],
  bandeja: [
    ruta('M22 12h-6l-2 3h-4l-2-3H2'),
    ruta('M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z')
  ]
};

@Component({
  selector: 'app-icono',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'icono', 'aria-hidden': 'true' },
  styles: `
    :host { display: inline-flex; flex-shrink: 0; line-height: 0; }
    svg { fill: none; stroke: currentColor; stroke-linecap: round; stroke-linejoin: round; }
  `,
  template: `
    <svg viewBox="0 0 24 24" [attr.width]="tamano()" [attr.height]="tamano()" [attr.stroke-width]="grosor()">
      @for (trazo of trazos(); track $index) {
        @switch (trazo.tipo) {
          @case ('ruta') { <path [attr.d]="$any(trazo).d" /> }
          @case ('circulo') { <circle [attr.cx]="$any(trazo).cx" [attr.cy]="$any(trazo).cy" [attr.r]="$any(trazo).r" /> }
          @case ('rectangulo') {
            <rect [attr.x]="$any(trazo).x" [attr.y]="$any(trazo).y" [attr.width]="$any(trazo).ancho"
                  [attr.height]="$any(trazo).alto" [attr.rx]="$any(trazo).rx" />
          }
        }
      }
    </svg>
  `
})
export class Icono {
  readonly nombre = input.required<string>();
  readonly tamano = input(20);
  readonly grosor = input(1.8);
  protected readonly trazos = computed(() => ICONOS[this.nombre()] ?? []);
}
