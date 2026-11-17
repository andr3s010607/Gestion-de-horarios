import { ChangeDetectionStrategy, Component, computed, effect, inject, input, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Asignatura } from '../../models/asignatura';
import { Docente } from '../../models/docente';
import { DIAS_SEMANA, DiaSemana, TIPOS_AULA, TIPOS_VINCULACION, TipoAula, etiquetaDe } from '../../models/enums';
import { RestriccionHorariaDocente } from '../../models/restriccion';
import { AsignaturaService } from '../../services/asignatura.service';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { DocenteAsignaturaService } from '../../services/docente-asignatura.service';
import { DocenteService } from '../../services/docente.service';
import { RestriccionService } from '../../services/restriccion.service';
import { ToastService } from '../../services/toast.service';
import { aMinutos, conSegundos, horaCorta } from '../../utils/texto';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Modal } from '../modal/modal';
import { Spinner } from '../spinner/spinner';

function finPosteriorAInicio(grupo: AbstractControl): ValidationErrors | null {
  const inicio = grupo.get('horaInicio')?.value as string;
  const fin = grupo.get('horaFin')?.value as string;
  return inicio && fin && aMinutos(fin) <= aMinutos(inicio) ? { finAntesDeInicio: true } : null;
}

@Component({
  selector: 'app-docente-detalle',
  imports: [ReactiveFormsModule, RouterLink, Icono, Spinner, EstadoVacio, Modal],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './docente-detalle.css',
  templateUrl: './docente-detalle.html'
})
export class DocenteDetalle {
  private readonly docenteService = inject(DocenteService);
  private readonly restriccionService = inject(RestriccionService);
  private readonly habilitacionService = inject(DocenteAsignaturaService);
  private readonly asignaturaService = inject(AsignaturaService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  protected readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  readonly id = input.required<string>();

  protected readonly dias = DIAS_SEMANA;
  protected readonly etiquetaDia = (dia: DiaSemana) => etiquetaDe(DIAS_SEMANA, dia);
  protected readonly etiquetaTipo = (tipo: TipoAula) => etiquetaDe(TIPOS_AULA, tipo);
  protected readonly horaCorta = horaCorta;

  protected readonly cargando = signal(true);
  protected readonly noEncontrado = signal(false);
  protected readonly guardando = signal(false);
  protected readonly docente = signal<Docente | null>(null);
  protected readonly restricciones = signal<RestriccionHorariaDocente[]>([]);
  protected readonly habilitadas = signal<Asignatura[]>([]);
  protected readonly catalogo = signal<Asignatura[]>([]);
  protected readonly asignaturaSeleccionada = signal('');
  protected readonly habilitando = signal(false);
  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<RestriccionHorariaDocente | null>(null);

  protected readonly formulario = this.fb.nonNullable.group(
    {
      dia: ['' as DiaSemana | '', Validators.required],
      horaInicio: ['', Validators.required],
      horaFin: ['', Validators.required]
    },
    { validators: finPosteriorAInicio }
  );

  protected readonly docenteId = computed(() => Number(this.id()));
  protected readonly vinculacion = computed(() => etiquetaDe(TIPOS_VINCULACION, this.docente()?.vinculacion));
  protected readonly disponibles = computed(() => {
    const habilitadas = new Set(this.habilitadas().map(asignatura => asignatura.id));
    return this.catalogo().filter(asignatura => !habilitadas.has(asignatura.id));
  });

  protected readonly restriccionesPorDia = computed(() =>
    DIAS_SEMANA.map(dia => ({
      ...dia,
      bloques: this.restricciones()
        .filter(restriccion => restriccion.dia === dia.valor)
        .sort((a, b) => a.horaInicio.localeCompare(b.horaInicio))
    }))
  );

  constructor() {
    effect(() => this.cargar(this.docenteId()));
  }

  protected posicion(restriccion: RestriccionHorariaDocente): { izquierda: number; ancho: number } {
    const inicioDia = 6 * 60;
    const finDia = 22 * 60;
    const rango = finDia - inicioDia;
    const inicio = Math.max(aMinutos(restriccion.horaInicio), inicioDia);
    const fin = Math.min(aMinutos(restriccion.horaFin), finDia);
    return { izquierda: ((inicio - inicioDia) / rango) * 100, ancho: Math.max(((fin - inicio) / rango) * 100, 2) };
  }

  protected abrirNuevaRestriccion(): void {
    this.editando.set(null);
    this.formulario.reset({ dia: '', horaInicio: '', horaFin: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEdicionRestriccion(restriccion: RestriccionHorariaDocente): void {
    this.editando.set(restriccion);
    this.formulario.reset({
      dia: restriccion.dia,
      horaInicio: horaCorta(restriccion.horaInicio),
      horaFin: horaCorta(restriccion.horaFin)
    });
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

  protected rangoInvalido(): boolean {
    const fin = this.formulario.controls.horaFin;
    return this.formulario.hasError('finAntesDeInicio') && (fin.touched || fin.dirty);
  }

  protected guardarRestriccion(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const valores = this.formulario.getRawValue();
    const datos: RestriccionHorariaDocente = {
      docenteId: this.docenteId(),
      dia: valores.dia as DiaSemana,
      horaInicio: conSegundos(valores.horaInicio),
      horaFin: conSegundos(valores.horaFin)
    };
    const actual = this.editando();
    const peticion = actual?.id ? this.restriccionService.actualizar(actual.id, datos) : this.restriccionService.crear(datos);
    this.guardando.set(true);
    peticion.subscribe({
      next: mensaje => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.toast.exito(actual ? 'Restricción actualizada' : 'Restricción registrada', mensaje);
        this.recargarRestricciones();
      },
      error: () => this.guardando.set(false)
    });
  }

  protected async eliminarRestriccion(restriccion: RestriccionHorariaDocente): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Eliminar restricción',
      `Se eliminará la restricción del ${this.etiquetaDia(restriccion.dia).toLowerCase()} de ${horaCorta(restriccion.horaInicio)} a ${horaCorta(restriccion.horaFin)}.`
    );
    if (!confirmado || !restriccion.id) {
      return;
    }
    this.restriccionService.eliminar(restriccion.id).subscribe(mensaje => {
      this.toast.exito('Restricción eliminada', mensaje);
      this.recargarRestricciones();
    });
  }

  protected habilitar(): void {
    const asignaturaId = Number(this.asignaturaSeleccionada());
    if (!asignaturaId) {
      return;
    }
    this.habilitando.set(true);
    this.habilitacionService.habilitar({ docenteId: this.docenteId(), asignaturaId }).subscribe({
      next: mensaje => {
        this.habilitando.set(false);
        this.asignaturaSeleccionada.set('');
        this.toast.exito('Asignatura habilitada', mensaje);
        this.recargarHabilitadas();
      },
      error: () => this.habilitando.set(false)
    });
  }

  protected async retirar(asignatura: Asignatura): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Retirar habilitación',
      `${this.docente()?.nombre} dejará de estar habilitado para dictar ${asignatura.codigo} · ${asignatura.nombre}.`,
      'Retirar'
    );
    if (!confirmado || !asignatura.id) {
      return;
    }
    this.habilitacionService.retirar(this.docenteId(), asignatura.id).subscribe(mensaje => {
      this.toast.exito('Habilitación retirada', mensaje);
      this.recargarHabilitadas();
    });
  }

  private cargar(id: number): void {
    this.cargando.set(true);
    this.noEncontrado.set(false);
    forkJoin({
      docente: this.docenteService.obtenerPorId(id),
      restricciones: this.restriccionService.obtenerPorDocente(id),
      habilitadas: this.habilitacionService.obtenerAsignaturasDeDocente(id),
      catalogo: this.asignaturaService.obtenerTodos()
    }).subscribe({
      next: ({ docente, restricciones, habilitadas, catalogo }) => {
        this.docente.set(docente);
        this.restricciones.set(restricciones);
        this.habilitadas.set(habilitadas);
        this.catalogo.set(catalogo);
        this.cargando.set(false);
      },
      error: () => {
        this.noEncontrado.set(true);
        this.cargando.set(false);
      }
    });
  }

  private recargarRestricciones(): void {
    this.restriccionService.obtenerPorDocente(this.docenteId()).subscribe(lista => this.restricciones.set(lista));
  }

  private recargarHabilitadas(): void {
    this.habilitacionService.obtenerAsignaturasDeDocente(this.docenteId()).subscribe(lista => this.habilitadas.set(lista));
  }
}
