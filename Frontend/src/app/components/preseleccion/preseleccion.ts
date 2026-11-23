import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { DIAS_SEMANA, DiaSemana } from '../../models/enums';
import { Horario } from '../../models/horario';
import { Incidencia } from '../../models/incidencia';
import { AsignaturaPreseleccion, GrupoPreseleccion } from '../../models/preseleccion';
import { SesionClase } from '../../models/sesion';
import { leerCuerpo, mensajeDeError, tituloDeError } from '../../services/interceptor.service';
import { PreseleccionService } from '../../services/preseleccion.service';
import { ToastService } from '../../services/toast.service';
import { descargarHorarioPdf } from '../../utils/pdf-horario';
import { aMinutos, horaCorta } from '../../utils/texto';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Spinner } from '../spinner/spinner';

interface BloqueBorrador {
  sesion: SesionClase;
  arriba: number;
  alto: number;
  choca: boolean;
}

const HORA_INICIO_VISTA = 7;
const HORA_FIN_VISTA = 22;
const ALTO_HORA = 30;

/**
 * Preselección de grupos del estudiante (RF-32 a RF-34): elige como máximo un
 * grupo por asignatura, ve los choques del borrador y confirma solo una
 * combinación sin choques. La preselección no descuenta cupo ni es una inscripción.
 */
@Component({
  selector: 'app-preseleccion',
  imports: [Icono, Spinner, EstadoVacio],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './preseleccion.css',
  templateUrl: './preseleccion.html'
})
export class Preseleccion implements OnInit {
  private readonly servicio = inject(PreseleccionService);
  private readonly toast = inject(ToastService);

  protected readonly dias = DIAS_SEMANA;
  protected readonly horasVista = Array.from({ length: HORA_FIN_VISTA - HORA_INICIO_VISTA }, (_, i) => HORA_INICIO_VISTA + i);
  protected readonly altoVista = (HORA_FIN_VISTA - HORA_INICIO_VISTA) * ALTO_HORA;
  protected readonly horaCorta = horaCorta;

  protected readonly cargando = signal(true);
  protected readonly cargandoGrupos = signal(false);
  protected readonly guardando = signal(false);
  protected readonly exportando = signal(false);
  protected readonly horarios = signal<Horario[]>([]);
  protected readonly horarioId = signal<number | null>(null);
  protected readonly asignaturas = signal<AsignaturaPreseleccion[]>([]);
  /** Grupo elegido por asignatura en el borrador (asignaturaId → grupoId). */
  protected readonly borrador = signal<Map<number, number>>(new Map());
  protected readonly confirmados = signal<number[]>([]);
  protected readonly choques = signal<Incidencia[]>([]);
  protected readonly revisando = signal(false);
  protected readonly errorConfirmacion = signal<string | null>(null);

  protected readonly horario = computed(() => this.horarios().find(horario => horario.id === this.horarioId()) ?? null);
  /** Con la preselección cerrada, el estudiante solo consulta la combinación que confirmó. */
  protected readonly soloLectura = computed(() => this.horario()?.preseleccionAbierta !== true);
  protected readonly gruposElegidos = computed(() => [...this.borrador().values()]);
  protected readonly sesionesEnChoque = computed(() => new Set(this.choques().flatMap(choque => choque.sesionIds)));
  protected readonly gruposEnChoque = computed(() => {
    const enChoque = this.sesionesEnChoque();
    const grupos = new Set<number>();
    for (const asignatura of this.asignaturas()) {
      for (const grupo of asignatura.grupos) {
        if (grupo.sesiones.some(sesion => enChoque.has(sesion.id))) {
          grupos.add(grupo.grupoId);
        }
      }
    }
    return grupos;
  });
  protected readonly hayCambios = computed(() => {
    const elegidos = [...this.gruposElegidos()].sort((a, b) => a - b).join(',');
    const guardados = [...this.confirmados()].sort((a, b) => a - b).join(',');
    return elegidos !== guardados;
  });
  protected readonly puedeConfirmar = computed(() =>
    this.hayCambios() && this.choques().length === 0 && !this.revisando() && !this.guardando());
  protected readonly bloquesPorDia = computed(() => {
    const enChoque = this.sesionesEnChoque();
    const elegidos = new Set(this.gruposElegidos());
    const bloques = new Map<DiaSemana, BloqueBorrador[]>();
    for (const asignatura of this.asignaturas()) {
      for (const grupo of asignatura.grupos) {
        if (!elegidos.has(grupo.grupoId)) {
          continue;
        }
        for (const sesion of grupo.sesiones) {
          if (!sesion.dia || !sesion.horaInicio || !sesion.horaFin) {
            continue;
          }
          const inicio = aMinutos(sesion.horaInicio) - HORA_INICIO_VISTA * 60;
          const fin = aMinutos(sesion.horaFin) - HORA_INICIO_VISTA * 60;
          const lista = bloques.get(sesion.dia) ?? [];
          lista.push({ sesion, arriba: (inicio / 60) * ALTO_HORA, alto: ((fin - inicio) / 60) * ALTO_HORA, choca: enChoque.has(sesion.id) });
          bloques.set(sesion.dia, lista);
        }
      }
    }
    return bloques;
  });

  ngOnInit(): void {
    this.servicio.horariosVisibles().subscribe({
      next: horarios => {
        this.horarios.set(horarios);
        this.cargando.set(false);
        if (horarios.length > 0 && horarios[0].id) {
          this.seleccionarHorario(horarios[0].id);
        }
      },
      error: () => this.cargando.set(false)
    });
  }

  protected seleccionarHorario(id: number): void {
    this.horarioId.set(id);
    this.cargandoGrupos.set(true);
    this.errorConfirmacion.set(null);
    forkJoin({ asignaturas: this.servicio.grupos(id), mia: this.servicio.miPreseleccion(id) }).subscribe({
      next: ({ asignaturas, mia }) => {
        this.asignaturas.set(asignaturas);
        this.confirmados.set(mia.grupoIds);
        this.borrador.set(this.mapaDesde(mia.grupoIds, asignaturas));
        this.choques.set([]);
        this.cargandoGrupos.set(false);
      },
      error: () => this.cargandoGrupos.set(false)
    });
  }

  protected elegido(asignatura: AsignaturaPreseleccion): number | null {
    return this.borrador().get(asignatura.asignaturaId) ?? null;
  }

  protected elegir(asignatura: AsignaturaPreseleccion, grupoId: number | null): void {
    if (this.soloLectura()) {
      return;
    }
    const mapa = new Map(this.borrador());
    if (grupoId === null) {
      mapa.delete(asignatura.asignaturaId);
    } else {
      mapa.set(asignatura.asignaturaId, grupoId);
    }
    this.borrador.set(mapa);
    this.errorConfirmacion.set(null);
    this.revisarChoques();
  }

  protected descartar(): void {
    this.borrador.set(this.mapaDesde(this.confirmados(), this.asignaturas()));
    this.errorConfirmacion.set(null);
    this.revisarChoques();
  }

  protected confirmar(): void {
    const id = this.horarioId();
    if (id === null || this.soloLectura()) {
      return;
    }
    this.guardando.set(true);
    this.errorConfirmacion.set(null);
    this.servicio.confirmar(id, this.gruposElegidos()).subscribe({
      next: resultado => {
        this.guardando.set(false);
        this.confirmados.set(resultado.grupoIds);
        this.toast.exito('Preselección confirmada', resultado.grupoIds.length
          ? `Guardaste ${resultado.grupoIds.length} ${resultado.grupoIds.length === 1 ? 'grupo' : 'grupos'}. Puedes cambiarla mientras la preselección esté abierta.`
          : 'Retiraste tu preselección de este horario.');
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        if (error.status === 401) {
          return;
        }
        const cuerpo = leerCuerpo(error);
        if (cuerpo?.incidencias?.length) {
          this.choques.set(cuerpo.incidencias);
        }
        this.errorConfirmacion.set(cuerpo?.mensaje ?? mensajeDeError(error));
        if (!cuerpo?.incidencias?.length) {
          this.toast.error(tituloDeError(error.status), mensajeDeError(error));
        }
      }
    });
  }

  protected exportar(): void {
    const id = this.horarioId();
    if (id === null || this.confirmados().length === 0) {
      return;
    }
    const confirmados = new Set(this.confirmados());
    const sesiones = this.asignaturas().flatMap(asignatura => asignatura.grupos
      .filter(grupo => confirmados.has(grupo.grupoId))
      .flatMap(grupo => grupo.sesiones.map(sesion => ({ ...sesion, nombreDocente: sesion.nombreDocente ?? grupo.nombreDocente }))));
    const semestre = this.horario()?.semestre ?? String(id);
    this.exportando.set(true);
    try {
      descargarHorarioPdf({
        titulo: 'Mi preselección',
        subtitulo: `Horario del semestre ${semestre}`,
        sesiones,
        nombreArchivo: `mi-preseleccion-${semestre}.pdf`
      });
    } finally {
      this.exportando.set(false);
    }
  }

  protected bloques(dia: DiaSemana): BloqueBorrador[] {
    return this.bloquesPorDia().get(dia) ?? [];
  }

  protected etiquetaDia(dia: DiaSemana | null): string {
    return this.dias.find(item => item.valor === dia)?.corta ?? '';
  }

  protected resumenSesiones(grupo: GrupoPreseleccion): string {
    return grupo.sesiones
      .map(sesion => `${this.etiquetaDia(sesion.dia)} ${horaCorta(sesion.horaInicio)}–${horaCorta(sesion.horaFin)}`)
      .join(' · ');
  }

  private revisarChoques(): void {
    const id = this.horarioId();
    const elegidos = this.gruposElegidos();
    if (id === null || elegidos.length < 2) {
      this.choques.set([]);
      return;
    }
    this.revisando.set(true);
    this.servicio.revisarBorrador(id, elegidos).subscribe({
      next: choques => {
        this.choques.set(choques);
        this.revisando.set(false);
      },
      error: () => this.revisando.set(false)
    });
  }

  private mapaDesde(grupoIds: number[], asignaturas: AsignaturaPreseleccion[]): Map<number, number> {
    const mapa = new Map<number, number>();
    for (const asignatura of asignaturas) {
      const grupo = asignatura.grupos.find(item => grupoIds.includes(item.grupoId));
      if (grupo) {
        mapa.set(asignatura.asignaturaId, grupo.grupoId);
      }
    }
    return mapa;
  }
}
