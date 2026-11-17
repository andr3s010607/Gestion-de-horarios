import { ChangeDetectionStrategy, Component, OnInit, computed, effect, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Aula } from '../../models/aula';
import { TIPOS_AULA, TipoAula, etiquetaDe } from '../../models/enums';
import { AulaService } from '../../services/aula.service';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { SesionService } from '../../services/sesion.service';
import { ToastService } from '../../services/toast.service';
import { coincide } from '../../utils/texto';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Modal } from '../modal/modal';
import { Paginador, paginar } from '../paginador/paginador';
import { Spinner } from '../spinner/spinner';

const TAMANO_PAGINA = 10;

@Component({
  selector: 'app-aulas',
  imports: [ReactiveFormsModule, Icono, Spinner, EstadoVacio, Modal, Paginador],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './aulas.html'
})
export class Aulas implements OnInit {
  private readonly servicio = inject(AulaService);
  private readonly sesionService = inject(SesionService);
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
  protected readonly aulas = signal<Aula[]>([]);
  private readonly sesionesPorAula = signal<Map<number, number>>(new Map());
  protected readonly termino = signal('');
  protected readonly filtroTipo = signal<TipoAula | ''>('');
  protected readonly pagina = signal(1);
  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Aula | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    codigo: ['', [Validators.required, Validators.maxLength(20)]],
    capacidad: [30, [Validators.required, Validators.min(1)]],
    tipo: ['' as TipoAula | '', Validators.required]
  });

  protected readonly filtradas = computed(() => {
    const tipo = this.filtroTipo();
    return this.aulas()
      .filter(aula => !tipo || aula.tipo === tipo)
      .filter(aula => coincide(this.termino(), aula.codigo, this.etiquetaTipo(aula.tipo)));
  });

  protected readonly visibles = computed(() => paginar(this.filtradas(), this.pagina(), TAMANO_PAGINA));
  protected readonly capacidadTotal = computed(() => this.aulas().reduce((suma, aula) => suma + aula.capacidad, 0));

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

  protected sesionesDe(aula: Aula): number {
    return aula.id ? this.sesionesPorAula().get(aula.id) ?? 0 : 0;
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
    this.formulario.reset({ codigo: '', capacidad: 30, tipo: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(aula: Aula): void {
    this.editando.set(aula);
    this.formulario.reset({ codigo: aula.codigo, capacidad: aula.capacidad, tipo: aula.tipo });
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
    const datos: Aula = { codigo: valores.codigo.trim(), capacidad: Number(valores.capacidad), tipo: valores.tipo as TipoAula };
    const actual = this.editando();
    const peticion = actual?.id ? this.servicio.actualizar(actual.id, datos) : this.servicio.crear(datos);
    this.guardando.set(true);
    peticion.subscribe({
      next: mensaje => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.toast.exito(actual ? 'Aula actualizada' : 'Aula creada', mensaje);
        this.cargar();
      },
      error: () => this.guardando.set(false)
    });
  }

  protected async eliminar(aula: Aula): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Eliminar aula',
      `Se eliminará el aula ${aula.codigo}. Esta acción no se puede deshacer.`
    );
    if (!confirmado || !aula.id) {
      return;
    }
    this.servicio.eliminar(aula.id).subscribe(mensaje => {
      this.toast.exito('Aula eliminada', mensaje);
      this.cargar();
    });
  }

  private cargar(): void {
    this.cargando.set(true);
    forkJoin({ aulas: this.servicio.obtenerTodos(), sesiones: this.sesionService.obtenerTodas() }).subscribe({
      next: ({ aulas, sesiones }) => {
        const conteo = new Map<number, number>();
        for (const sesion of sesiones) {
          if (sesion.aulaId !== null) {
            conteo.set(sesion.aulaId, (conteo.get(sesion.aulaId) ?? 0) + 1);
          }
        }
        this.aulas.set(aulas);
        this.sesionesPorAula.set(conteo);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }
}
