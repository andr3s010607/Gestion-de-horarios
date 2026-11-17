import { ChangeDetectionStrategy, Component, OnInit, computed, effect, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Docente } from '../../models/docente';
import { TIPOS_VINCULACION, TipoVinculacion, etiquetaDe } from '../../models/enums';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { DocenteService } from '../../services/docente.service';
import { GrupoService } from '../../services/grupo.service';
import { ToastService } from '../../services/toast.service';
import { coincide } from '../../utils/texto';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Modal } from '../modal/modal';
import { Paginador, paginar } from '../paginador/paginador';
import { Spinner } from '../spinner/spinner';

const TAMANO_PAGINA = 10;

@Component({
  selector: 'app-docentes',
  imports: [ReactiveFormsModule, RouterLink, Icono, Spinner, EstadoVacio, Modal, Paginador],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    .nombre { display: flex; align-items: center; gap: 12px; }
    .avatar { display: grid; place-items: center; width: 34px; height: 34px; border-radius: 50%; background: var(--fresh-crown); color: var(--fig-leaf); font-size: 13px; font-weight: 600; flex-shrink: 0; }
    .propio { margin-left: 6px; }
    a.enlace { color: var(--texto); text-decoration: none; font-weight: 500; }
    a.enlace:hover { color: var(--fig-leaf); text-decoration: underline; }
  `,
  templateUrl: './docentes.html'
})
export class Docentes implements OnInit {
  private readonly servicio = inject(DocenteService);
  private readonly grupoService = inject(GrupoService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  protected readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  readonly buscar = input<string>();

  protected readonly vinculaciones = TIPOS_VINCULACION;
  protected readonly etiquetaVinculacion = (valor?: TipoVinculacion) => etiquetaDe(TIPOS_VINCULACION, valor);
  protected readonly tamanoPagina = TAMANO_PAGINA;
  protected readonly puedeVerDetalle = computed(() => this.auth.tieneRol(['ADMINISTRADOR', 'DOCENTE']));

  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly docentes = signal<Docente[]>([]);
  private readonly gruposPorDocente = signal<Map<number, number>>(new Map());
  protected readonly termino = signal('');
  protected readonly filtroVinculacion = signal<TipoVinculacion | ''>('');
  protected readonly pagina = signal(1);
  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Docente | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    vinculacion: ['' as TipoVinculacion | '', Validators.required]
  });

  protected readonly filtrados = computed(() => {
    const vinculacion = this.filtroVinculacion();
    return this.docentes()
      .filter(docente => !vinculacion || docente.vinculacion === vinculacion)
      .filter(docente => coincide(this.termino(), docente.nombre));
  });

  protected readonly visibles = computed(() => paginar(this.filtrados(), this.pagina(), TAMANO_PAGINA));

  constructor() {
    effect(() => {
      const termino = this.buscar();
      if (termino) {
        this.termino.set(termino);
      }
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  protected iniciales(nombre: string): string {
    return nombre.split(/\s+/).filter(Boolean).slice(0, 2).map(parte => parte[0]).join('').toUpperCase();
  }

  protected gruposDe(docente: Docente): number {
    return docente.id ? this.gruposPorDocente().get(docente.id) ?? 0 : 0;
  }

  protected esPropio(docente: Docente): boolean {
    return !!docente.id && this.auth.sesion()?.docenteId === docente.id;
  }

  /** El administrador ve el detalle de todos; un docente, solo el suyo. */
  protected veDetalle(docente: Docente): boolean {
    return this.auth.esAdministrador() || this.esPropio(docente);
  }

  protected alBuscar(valor: string): void {
    this.termino.set(valor);
    this.pagina.set(1);
  }

  protected alFiltrar(valor: string): void {
    this.filtroVinculacion.set(valor as TipoVinculacion | '');
    this.pagina.set(1);
  }

  protected abrirNuevo(): void {
    this.editando.set(null);
    this.formulario.reset({ nombre: '', vinculacion: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(docente: Docente): void {
    this.editando.set(docente);
    this.formulario.reset({ nombre: docente.nombre, vinculacion: docente.vinculacion ?? '' });
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    if (!this.guardando()) {
      this.modalAbierto.set(false);
    }
  }

  protected invalido(campo: keyof typeof this.formulario.controls): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && (control.touched || control.dirty);
  }

  protected guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const valores = this.formulario.getRawValue();
    const datos: Docente = { nombre: valores.nombre.trim(), vinculacion: valores.vinculacion as TipoVinculacion };
    const actual = this.editando();
    const peticion = actual?.id ? this.servicio.actualizar(actual.id, datos) : this.servicio.crear(datos);
    this.guardando.set(true);
    peticion.subscribe({
      next: mensaje => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.toast.exito(actual ? 'Docente actualizado' : 'Docente creado', mensaje);
        this.cargar();
      },
      error: () => this.guardando.set(false)
    });
  }

  protected async eliminar(docente: Docente): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Eliminar docente',
      `Se eliminará a ${docente.nombre} junto con sus restricciones y habilitaciones. Los usuarios vinculados quedarán sin docente. Esta acción no se puede deshacer.`
    );
    if (!confirmado || !docente.id) {
      return;
    }
    this.servicio.eliminar(docente.id).subscribe(mensaje => {
      this.toast.exito('Docente eliminado', mensaje);
      this.cargar();
    });
  }

  private cargar(): void {
    this.cargando.set(true);
    forkJoin({ docentes: this.servicio.obtenerTodos(), grupos: this.grupoService.obtenerTodos() }).subscribe({
      next: ({ docentes, grupos }) => {
        const conteo = new Map<number, number>();
        for (const grupo of grupos) {
          if (grupo.docenteId !== null) {
            conteo.set(grupo.docenteId, (conteo.get(grupo.docenteId) ?? 0) + 1);
          }
        }
        this.docentes.set([...docentes].sort((a, b) => a.nombre.localeCompare(b.nombre)));
        this.gruposPorDocente.set(conteo);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }
}
