import { ChangeDetectionStrategy, Component, OnInit, computed, effect, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Asignatura } from '../../models/asignatura';
import { TIPOS_AULA, TipoAula, etiquetaDe } from '../../models/enums';
import { AsignaturaService } from '../../services/asignatura.service';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
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
  selector: 'app-asignaturas',
  imports: [ReactiveFormsModule, Icono, Spinner, EstadoVacio, Modal, Paginador],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './asignaturas.html'
})
export class Asignaturas implements OnInit {
  private readonly servicio = inject(AsignaturaService);
  private readonly grupoService = inject(GrupoService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  protected readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  readonly buscar = input<string>();

  protected readonly tiposAula = TIPOS_AULA;
  protected readonly etiquetaTipo = (tipo: TipoAula) => etiquetaDe(TIPOS_AULA, tipo);
  protected readonly tamanoPagina = TAMANO_PAGINA;

  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly asignaturas = signal<Asignatura[]>([]);
  private readonly asignaturasConGrupos = signal<Set<number>>(new Set());
  protected readonly termino = signal('');
  protected readonly filtroTipo = signal<TipoAula | ''>('');
  protected readonly pagina = signal(1);
  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Asignatura | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    codigo: ['', [Validators.required, Validators.maxLength(20)]],
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    sesionesSemana: [1, [Validators.required, Validators.min(1), Validators.max(4)]],
    tipoAulaRequerido: ['' as TipoAula | '', Validators.required]
  });

  protected readonly filtradas = computed(() => {
    const tipo = this.filtroTipo();
    return this.asignaturas()
      .filter(asignatura => !tipo || asignatura.tipoAulaRequerido === tipo)
      .filter(asignatura => coincide(this.termino(), asignatura.codigo, asignatura.nombre));
  });

  protected readonly visibles = computed(() => paginar(this.filtradas(), this.pagina(), TAMANO_PAGINA));

  protected readonly sesionesBloqueadas = computed(() => {
    const actual = this.editando();
    return !!actual?.id && this.asignaturasConGrupos().has(actual.id);
  });

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

  protected tieneGrupos(asignatura: Asignatura): boolean {
    return !!asignatura.id && this.asignaturasConGrupos().has(asignatura.id);
  }

  protected alBuscar(valor: string): void {
    this.termino.set(valor);
    this.pagina.set(1);
  }

  protected alFiltrar(valor: string): void {
    this.filtroTipo.set(valor as TipoAula | '');
    this.pagina.set(1);
  }

  protected abrirNueva(): void {
    this.editando.set(null);
    this.formulario.reset({ codigo: '', nombre: '', sesionesSemana: 1, tipoAulaRequerido: '' });
    this.formulario.controls.sesionesSemana.enable();
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(asignatura: Asignatura): void {
    this.editando.set(asignatura);
    this.formulario.reset({
      codigo: asignatura.codigo,
      nombre: asignatura.nombre,
      sesionesSemana: asignatura.sesionesSemana,
      tipoAulaRequerido: asignatura.tipoAulaRequerido
    });
    if (this.sesionesBloqueadas()) {
      this.formulario.controls.sesionesSemana.disable();
    } else {
      this.formulario.controls.sesionesSemana.enable();
    }
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
    const datos: Asignatura = {
      codigo: valores.codigo.trim(),
      nombre: valores.nombre.trim(),
      sesionesSemana: Number(valores.sesionesSemana),
      tipoAulaRequerido: valores.tipoAulaRequerido as TipoAula
    };
    const actual = this.editando();
    const peticion = actual?.id ? this.servicio.actualizar(actual.id, datos) : this.servicio.crear(datos);
    this.guardando.set(true);
    peticion.subscribe({
      next: mensaje => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.toast.exito(actual ? 'Asignatura actualizada' : 'Asignatura creada', mensaje);
        this.cargar();
      },
      error: () => this.guardando.set(false)
    });
  }

  protected async eliminar(asignatura: Asignatura): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Eliminar asignatura',
      `Se eliminará ${asignatura.codigo} · ${asignatura.nombre} y sus habilitaciones docentes. Esta acción no se puede deshacer.`
    );
    if (!confirmado || !asignatura.id) {
      return;
    }
    this.servicio.eliminar(asignatura.id).subscribe(mensaje => {
      this.toast.exito('Asignatura eliminada', mensaje);
      this.cargar();
    });
  }

  private cargar(): void {
    this.cargando.set(true);
    forkJoin({ asignaturas: this.servicio.obtenerTodos(), grupos: this.grupoService.obtenerTodos() }).subscribe({
      next: ({ asignaturas, grupos }) => {
        this.asignaturas.set(asignaturas);
        this.asignaturasConGrupos.set(new Set(grupos.map(grupo => grupo.asignaturaId)));
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }
}
