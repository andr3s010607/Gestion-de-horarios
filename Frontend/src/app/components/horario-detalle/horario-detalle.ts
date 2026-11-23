import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Asignatura } from '../../models/asignatura';
import { Aula } from '../../models/aula';
import { Docente } from '../../models/docente';
import { Grupo } from '../../models/grupo';
import { CuadriculaHorario, ResultadoGeneracionHorario } from '../../models/horario';
import { DESCRIPCION_INCIDENCIA, Incidencia } from '../../models/incidencia';
import { ParametroSistema } from '../../models/parametro';
import { DemandaGrupo } from '../../models/preseleccion';
import { SesionClase } from '../../models/sesion';
import { AsignaturaService } from '../../services/asignatura.service';
import { AulaService } from '../../services/aula.service';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { DocenteService } from '../../services/docente.service';
import { GrupoService } from '../../services/grupo.service';
import { HorarioService } from '../../services/horario.service';
import { leerCuerpo, mensajeDeError, tituloDeError } from '../../services/interceptor.service';
import { ParametroService } from '../../services/parametro.service';
import { ToastService } from '../../services/toast.service';
import { descargarHorarioPdf } from '../../utils/pdf-horario';
import { CuadriculaHorarioComponent } from '../cuadricula-horario/cuadricula-horario';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { GruposHorario } from '../grupos-horario/grupos-horario';
import { Icono } from '../icono/icono';
import { Modal } from '../modal/modal';
import { Spinner } from '../spinner/spinner';

type Pestana = 'cuadricula' | 'grupos' | 'validacion' | 'demanda';

@Component({
  selector: 'app-horario-detalle',
  imports: [RouterLink, Icono, Spinner, EstadoVacio, Modal, CuadriculaHorarioComponent, GruposHorario],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './horario-detalle.css',
  templateUrl: './horario-detalle.html'
})
export class HorarioDetalle {
  private readonly horarioService = inject(HorarioService);
  private readonly grupoService = inject(GrupoService);
  private readonly asignaturaService = inject(AsignaturaService);
  private readonly docenteService = inject(DocenteService);
  private readonly aulaService = inject(AulaService);
  private readonly parametroService = inject(ParametroService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  protected readonly auth = inject(AuthService);

  readonly id = input.required<string>();

  protected readonly descripcion = DESCRIPCION_INCIDENCIA;
  protected readonly cargando = signal(true);
  protected readonly noEncontrado = signal(false);
  protected readonly pestana = signal<Pestana>('cuadricula');
  protected readonly cuadricula = signal<CuadriculaHorario | null>(null);
  protected readonly grupos = signal<Grupo[]>([]);
  protected readonly asignaturas = signal<Asignatura[]>([]);
  protected readonly docentes = signal<Docente[]>([]);
  protected readonly aulas = signal<Aula[]>([]);
  protected readonly parametros = signal<ParametroSistema | null>(null);
  protected readonly incidencias = signal<Incidencia[] | null>(null);
  protected readonly validando = signal(false);
  protected readonly generando = signal(false);
  protected readonly cambiandoEstado = signal(false);
  protected readonly exportando = signal(false);
  protected readonly resultadoGeneracion = signal<ResultadoGeneracionHorario | null>(null);
  protected readonly demanda = signal<DemandaGrupo[] | null>(null);
  protected readonly cargandoDemanda = signal(false);
  protected readonly cambiandoPreseleccion = signal(false);
  protected readonly rechazoAprobacion = signal<{ mensaje: string; incidencias: Incidencia[]; sinUbicar: number } | null>(null);

  protected readonly horarioId = computed(() => Number(this.id()));
  protected readonly horario = computed(() => this.cuadricula()?.horario ?? null);
  protected readonly aprobado = computed(() => this.horario()?.estado === 'APROBADO');
  protected readonly preseleccionAbierta = computed(() => this.horario()?.preseleccionAbierta === true);
  protected readonly totalPreseleccionados = computed(() =>
    (this.demanda() ?? []).reduce((total, grupo) => total + grupo.preseleccionados, 0));
  protected readonly editable = computed(() => this.auth.esAdministrador() && !this.aprobado());
  protected readonly sesiones = computed<SesionClase[]>(() => {
    const cuadricula = this.cuadricula();
    return cuadricula ? [...cuadricula.sesionesUbicadas, ...cuadricula.sesionesSinUbicar] : [];
  });
  protected readonly resumen = computed(() => {
    const cuadricula = this.cuadricula();
    const ubicadas = cuadricula?.sesionesUbicadas.length ?? 0;
    const sinUbicar = cuadricula?.sesionesSinUbicar.length ?? 0;
    const total = ubicadas + sinUbicar;
    return { ubicadas, sinUbicar, total, porcentaje: total ? Math.round((ubicadas / total) * 100) : 0 };
  });
  protected readonly errores = computed(() => this.incidencias()?.filter(incidencia => incidencia.severidad === 'ERROR') ?? []);
  protected readonly advertencias = computed(() => this.incidencias()?.filter(incidencia => incidencia.severidad === 'ADVERTENCIA') ?? []);

  constructor() {
    effect(() => this.cargar(this.horarioId()));
  }

  protected recargar(): void {
    const id = this.horarioId();
    forkJoin({ cuadricula: this.horarioService.obtenerCuadricula(id), grupos: this.grupoService.obtenerPorHorario(id) }).subscribe(
      ({ cuadricula, grupos }) => {
        this.cuadricula.set(cuadricula);
        this.grupos.set(grupos);
        if (this.incidencias() !== null) {
          this.validar(false);
        }
        if (this.demanda() !== null) {
          this.verDemanda(false);
        }
      }
    );
  }

  protected validar(cambiarPestana = true): void {
    this.validando.set(true);
    if (cambiarPestana) {
      this.pestana.set('validacion');
    }
    this.horarioService.validar(this.horarioId()).subscribe({
      next: incidencias => {
        this.incidencias.set(incidencias);
        this.validando.set(false);
      },
      error: () => this.validando.set(false)
    });
  }

  protected async generar(): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Generar propuesta',
      'El generador ubicará automáticamente las sesiones pendientes respetando las reglas. Las sesiones ya ubicadas no se mueven.',
      'Generar',
      false
    );
    if (!confirmado) {
      return;
    }
    this.generando.set(true);
    this.horarioService.generar(this.horarioId()).subscribe({
      next: resultado => {
        this.generando.set(false);
        this.resultadoGeneracion.set(resultado);
        this.recargar();
      },
      error: () => this.generando.set(false)
    });
  }

  protected async aprobar(): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Aprobar horario',
      `El horario ${this.horario()?.semestre} quedará bloqueado para cambios hasta que se devuelva a borrador.`,
      'Aprobar',
      false
    );
    if (!confirmado) {
      return;
    }
    this.cambiandoEstado.set(true);
    this.horarioService.aprobar(this.horarioId()).subscribe({
      next: () => {
        this.cambiandoEstado.set(false);
        this.toast.exito('Horario aprobado', `El horario ${this.horario()?.semestre} quedó aprobado y bloqueado.`);
        this.recargar();
      },
      error: (error: HttpErrorResponse) => {
        this.cambiandoEstado.set(false);
        const cuerpo = leerCuerpo(error);
        if (error.status === 409 && cuerpo?.incidencias) {
          this.rechazoAprobacion.set({ mensaje: cuerpo.mensaje, incidencias: cuerpo.incidencias, sinUbicar: cuerpo.sesionesSinUbicar ?? 0 });
        } else if (error.status !== 401) {
          this.toast.error(tituloDeError(error.status), mensajeDeError(error));
        }
      }
    });
  }

  protected async devolverABorrador(): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Devolver a borrador',
      `El horario ${this.horario()?.semestre} volverá a admitir cambios y deberá aprobarse de nuevo.`,
      'Devolver a borrador',
      false
    );
    if (!confirmado) {
      return;
    }
    this.cambiandoEstado.set(true);
    this.horarioService.devolverABorrador(this.horarioId()).subscribe({
      next: () => {
        this.cambiandoEstado.set(false);
        this.toast.info('Horario en borrador', 'Ya puedes editar grupos y sesiones.');
        this.recargar();
      },
      error: () => this.cambiandoEstado.set(false)
    });
  }

  /** RF-35: preselecciones confirmadas de cada grupo frente a su cupo. */
  protected verDemanda(cambiarPestana = true): void {
    if (cambiarPestana) {
      this.pestana.set('demanda');
    }
    this.cargandoDemanda.set(true);
    this.horarioService.demanda(this.horarioId()).subscribe({
      next: demanda => {
        this.demanda.set(demanda);
        this.cargandoDemanda.set(false);
      },
      error: () => this.cargandoDemanda.set(false)
    });
  }

  /** RF-31: abrir la preselección solo sin sesiones sin ubicar, y cerrarla cuando se quiera. */
  protected async alternarPreseleccion(): Promise<void> {
    const abrir = !this.preseleccionAbierta();
    const semestre = this.horario()?.semestre;
    const confirmado = await this.confirmacion.confirmar(
      abrir ? 'Abrir preselección' : 'Cerrar preselección',
      abrir
        ? `Los estudiantes podrán elegir un grupo por asignatura del horario ${semestre} y confirmar su preselección. No descuenta cupo ni es una inscripción.`
        : `Los estudiantes ya no podrán confirmar ni modificar su preselección del horario ${semestre}. Las preselecciones guardadas se conservan.`,
      abrir ? 'Abrir' : 'Cerrar',
      false
    );
    if (!confirmado) {
      return;
    }
    this.cambiandoPreseleccion.set(true);
    const peticion = abrir ? this.horarioService.abrirPreseleccion(this.horarioId()) : this.horarioService.cerrarPreseleccion(this.horarioId());
    peticion.subscribe({
      next: () => {
        this.cambiandoPreseleccion.set(false);
        this.toast.exito(abrir ? 'Preselección abierta' : 'Preselección cerrada',
          abrir ? 'Los estudiantes ya pueden elegir sus grupos.' : 'Ya no se aceptan confirmaciones.');
        this.recargar();
      },
      error: () => this.cambiandoPreseleccion.set(false)
    });
  }

  protected porcentajeDemanda(grupo: DemandaGrupo): number {
    return grupo.cupo ? Math.min(100, Math.round((grupo.preseleccionados / grupo.cupo) * 100)) : 0;
  }

  protected exportar(): void {
    const semestre = this.horario()?.semestre ?? String(this.horarioId());
    this.exportando.set(true);
    try {
      descargarHorarioPdf({
        titulo: `Horario del semestre ${semestre}`,
        subtitulo: this.aprobado() ? 'Horario aprobado' : 'Horario en borrador',
        sesiones: this.cuadricula()?.sesionesUbicadas ?? [],
        nombreArchivo: `horario-${semestre}.pdf`
      });
    } finally {
      this.exportando.set(false);
    }
  }

  protected irAValidacionDesdeRechazo(): void {
    this.rechazoAprobacion.set(null);
    this.validar();
  }

  private cargar(id: number): void {
    this.cargando.set(true);
    this.noEncontrado.set(false);
    this.incidencias.set(null);
    this.demanda.set(null);
    forkJoin({
      cuadricula: this.horarioService.obtenerCuadricula(id),
      grupos: this.grupoService.obtenerPorHorario(id),
      asignaturas: this.asignaturaService.obtenerTodos(),
      docentes: this.docenteService.obtenerTodos(),
      aulas: this.aulaService.obtenerTodos(),
      parametros: this.parametroService.obtener()
    }).subscribe({
      next: datos => {
        this.cuadricula.set(datos.cuadricula);
        this.grupos.set(datos.grupos);
        this.asignaturas.set(datos.asignaturas);
        this.docentes.set(datos.docentes);
        this.aulas.set([...datos.aulas].sort((a, b) => a.codigo.localeCompare(b.codigo)));
        this.parametros.set(datos.parametros);
        this.cargando.set(false);
      },
      error: () => {
        this.noEncontrado.set(true);
        this.cargando.set(false);
      }
    });
  }
}
