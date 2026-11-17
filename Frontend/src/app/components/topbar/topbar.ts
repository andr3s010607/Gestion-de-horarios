import { ChangeDetectionStrategy, Component, ElementRef, computed, inject, output, signal } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { AsignaturaService } from '../../services/asignatura.service';
import { AulaService } from '../../services/aula.service';
import { DocenteService } from '../../services/docente.service';
import { Icono } from '../icono/icono';
import { ETIQUETA_ROL } from '../layout/navegacion';
import { normalizar } from '../../utils/texto';

interface ResultadoBusqueda {
  tipo: string;
  icono: string;
  titulo: string;
  detalle: string;
  ruta: string;
  termino: string;
}

const MAXIMO_RESULTADOS = 8;

@Component({
  selector: 'app-topbar',
  imports: [Icono],
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { '(document:click)': 'cerrarAlHacerClicFuera($event)' },
  styleUrl: './topbar.css',
  templateUrl: './topbar.html'
})
export class Topbar {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly elemento = inject(ElementRef<HTMLElement>);
  private readonly asignaturaService = inject(AsignaturaService);
  private readonly docenteService = inject(DocenteService);
  private readonly aulaService = inject(AulaService);

  readonly abrirMenu = output<void>();

  protected readonly sesion = this.auth.sesion;
  protected readonly etiquetaRol = computed(() => {
    const rol = this.auth.rol();
    return rol ? ETIQUETA_ROL[rol] : '';
  });
  protected readonly iniciales = computed(() => (this.sesion()?.nombreUsuario ?? '?').slice(0, 2).toUpperCase());
  protected readonly saludo = this.calcularSaludo();
  protected readonly subtitulo = computed(() => {
    switch (this.auth.rol()) {
      case 'ADMINISTRADOR':
        return 'Gestiona catálogos, genera y aprueba el horario del semestre.';
      case 'DOCENTE':
        return 'Consulta tu horario, tus restricciones y tus asignaturas habilitadas.';
      default:
        return 'Consulta el horario por asignatura, docente o aula.';
    }
  });

  protected readonly termino = signal('');
  protected readonly buscando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly menuUsuarioAbierto = signal(false);
  private readonly indice = signal<ResultadoBusqueda[] | null>(null);

  protected readonly resultados = computed(() => {
    const termino = this.normalizar(this.termino());
    const indice = this.indice();
    if (!termino || !indice) {
      return [];
    }
    return indice
      .filter(resultado => this.normalizar(`${resultado.titulo} ${resultado.detalle}`).includes(termino))
      .slice(0, MAXIMO_RESULTADOS);
  });

  protected alEscribir(valor: string): void {
    this.termino.set(valor);
    this.panelAbierto.set(valor.trim().length > 0);
    this.cargarIndice();
  }

  protected irAResultado(resultado: ResultadoBusqueda): void {
    this.panelAbierto.set(false);
    this.termino.set('');
    this.router.navigate([resultado.ruta], { queryParams: { buscar: resultado.termino } });
  }

  protected buscarEnModulo(evento: Event): void {
    evento.preventDefault();
    const primero = this.resultados()[0];
    if (primero) {
      this.irAResultado(primero);
    }
  }

  protected cerrarSesion(): void {
    this.menuUsuarioAbierto.set(false);
    this.auth.cerrarSesion();
  }

  protected cerrarAlHacerClicFuera(evento: MouseEvent): void {
    if (!this.elemento.nativeElement.contains(evento.target as Node)) {
      this.panelAbierto.set(false);
      this.menuUsuarioAbierto.set(false);
    }
  }

  private cargarIndice(): void {
    if (this.indice() !== null || this.buscando()) {
      return;
    }
    this.buscando.set(true);
    forkJoin({
      asignaturas: this.asignaturaService.obtenerTodos(),
      docentes: this.docenteService.obtenerTodos(),
      aulas: this.aulaService.obtenerTodos()
    }).subscribe({
      next: ({ asignaturas, docentes, aulas }) => {
        this.indice.set([
          ...asignaturas.map(asignatura => ({
            tipo: 'Asignatura', icono: 'libro', titulo: asignatura.nombre, detalle: asignatura.codigo,
            ruta: '/asignaturas', termino: asignatura.codigo
          })),
          ...docentes.map(docente => ({
            tipo: 'Docente', icono: 'personas', titulo: docente.nombre, detalle: 'Docente',
            ruta: '/docentes', termino: docente.nombre
          })),
          ...aulas.map(aula => ({
            tipo: 'Aula', icono: 'edificio', titulo: aula.codigo, detalle: `Capacidad ${aula.capacidad}`,
            ruta: '/aulas', termino: aula.codigo
          }))
        ]);
        this.buscando.set(false);
      },
      error: () => this.buscando.set(false)
    });
  }

  private normalizar(texto: string): string {
    return normalizar(texto);
  }

  private calcularSaludo(): string {
    const hora = new Date().getHours();
    if (hora < 12) {
      return 'Buenos días';
    }
    return hora < 19 ? 'Buenas tardes' : 'Buenas noches';
  }
}
