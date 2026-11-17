import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, input, output, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Aula } from '../../models/aula';
import { DIAS_SEMANA, DiaSemana, TIPOS_AULA, etiquetaDe } from '../../models/enums';
import { CuadriculaHorario } from '../../models/horario';
import { DESCRIPCION_INCIDENCIA, Incidencia } from '../../models/incidencia';
import { ParametroSistema } from '../../models/parametro';
import { SesionClase } from '../../models/sesion';
import { leerCuerpo, mensajeDeError } from '../../services/interceptor.service';
import { SesionService } from '../../services/sesion.service';
import { ToastService } from '../../services/toast.service';
import { aMinutos, horaCorta } from '../../utils/texto';
import { Icono } from '../icono/icono';
import { Modal } from '../modal/modal';
import { Spinner } from '../spinner/spinner';

const ALTO_HORA = 58;
const TONOS = ['verde', 'petroleo', 'cafe', 'kombu', 'moss', 'azeitona', 'tan'] as const;

interface BloqueSesion {
  sesion: SesionClase;
  arriba: number;
  alto: number;
  carril: number;
  carriles: number;
  tono: string;
}

interface ColumnaDia {
  valor: DiaSemana;
  etiqueta: string;
  corta: string;
  bloques: BloqueSesion[];
  ranuras: { hora: string; arriba: number }[];
  bloqueos: { arriba: number; alto: number; etiqueta: string }[];
}

@Component({
  selector: 'app-cuadricula-horario',
  imports: [ReactiveFormsModule, Icono, Spinner, Modal],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './cuadricula-horario.css',
  templateUrl: './cuadricula-horario.html'
})
export class CuadriculaHorarioComponent {
  private readonly sesionService = inject(SesionService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(FormBuilder);

  readonly cuadricula = input.required<CuadriculaHorario>();
  readonly aulas = input.required<Aula[]>();
  readonly parametros = input<ParametroSistema | null>(null);
  readonly editable = input(false);
  readonly cambio = output<void>();

  protected readonly horaCorta = horaCorta;
  protected readonly descripcion = DESCRIPCION_INCIDENCIA;
  protected readonly dias = DIAS_SEMANA;
  protected readonly etiquetaTipo = (aula: Aula) => etiquetaDe(TIPOS_AULA, aula.tipo);
  protected readonly etiquetaDia = (dia: DiaSemana | null) => etiquetaDe(DIAS_SEMANA, dia);

  protected readonly arrastrando = signal<SesionClase | null>(null);
  protected readonly soltando = signal(false);
  protected readonly ranuraActiva = signal<string | null>(null);
  protected readonly bandejaActiva = signal(false);
  protected readonly seleccionada = signal<SesionClase | null>(null);
  protected readonly moviendo = signal(false);
  protected readonly errorMovimiento = signal<{ mensaje: string; incidencias: Incidencia[] } | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    dia: ['' as DiaSemana | '', Validators.required],
    horaInicio: ['', Validators.required],
    aulaId: ['', Validators.required]
  });

  private readonly ubicacionElegida = toSignal(this.formulario.valueChanges, { initialValue: this.formulario.getRawValue() });

  protected readonly aulasOcupadas = computed(() => {
    const sesion = this.seleccionada();
    const { dia, horaInicio } = this.ubicacionElegida();
    return sesion && dia && horaInicio ? this.aulasOcupadasEn(sesion, dia, horaInicio) : new Set<number>();
  });

  private aulasOcupadasEn(sesion: SesionClase, dia: DiaSemana, horaInicio: string): Set<number> {
    const desde = aMinutos(horaInicio);
    const hasta = desde + sesion.duracionHoras * 60;
    return new Set(this.cuadricula().sesionesUbicadas
      .filter(otra => otra.id !== sesion.id && otra.dia === dia && otra.horaInicio && otra.aulaId !== null)
      .filter(otra => {
        const otraDesde = aMinutos(otra.horaInicio!);
        const otraHasta = otra.horaFin ? aMinutos(otra.horaFin) : otraDesde + otra.duracionHoras * 60;
        return otraDesde < hasta && desde < otraHasta;
      })
      .map(otra => otra.aulaId!));
  }

  private readonly duracionMinutos = computed(() => {
    const horas = this.parametros()?.duracionSesionHoras
      ?? this.cuadricula().sesionesUbicadas[0]?.duracionHoras
      ?? this.cuadricula().sesionesSinUbicar[0]?.duracionHoras
      ?? 2;
    return horas * 60;
  });

  private readonly limites = computed(() => {
    const cuadricula = this.cuadricula();
    const inicios = [...cuadricula.horasInicioLunesViernes, ...cuadricula.horasInicioSabado].map(aMinutos);
    const parametros = this.parametros();
    const inicio = parametros ? aMinutos(parametros.horaInicioLunesViernes) : Math.min(...inicios, 7 * 60);
    const fin = parametros ? aMinutos(parametros.horaFinLunesViernes) : Math.max(...inicios, 18 * 60) + this.duracionMinutos();
    return { inicio: Math.floor(inicio / 60) * 60, fin: Math.ceil(fin / 60) * 60 };
  });

  protected readonly horas = computed(() => {
    const { inicio, fin } = this.limites();
    const lista: string[] = [];
    for (let minuto = inicio; minuto < fin; minuto += 60) {
      lista.push(`${String(Math.floor(minuto / 60)).padStart(2, '0')}:00`);
    }
    return lista;
  });

  protected readonly altoTotal = computed(() => this.horas().length * ALTO_HORA);
  protected readonly altoHora = ALTO_HORA;

  private readonly tonoPorAsignatura = computed(() => {
    const ids = [...new Set([...this.cuadricula().sesionesUbicadas, ...this.cuadricula().sesionesSinUbicar].map(sesion => sesion.asignaturaId))].sort((a, b) => a - b);
    return new Map(ids.map((id, indice) => [id, TONOS[indice % TONOS.length]]));
  });

  protected tono(sesion: SesionClase): string {
    return this.tonoPorAsignatura().get(sesion.asignaturaId) ?? 'verde';
  }

  protected readonly columnas = computed<ColumnaDia[]>(() => {
    const cuadricula = this.cuadricula();
    const { inicio } = this.limites();
    const posicion = (minuto: number) => ((minuto - inicio) / 60) * ALTO_HORA;
    const parametros = this.parametros();
    const diasVisibles = DIAS_SEMANA.filter(dia => cuadricula.dias.includes(dia.valor));

    return diasVisibles.map(dia => {
      const inicios = dia.valor === 'SABADO' ? cuadricula.horasInicioSabado : cuadricula.horasInicioLunesViernes;
      const sesiones = cuadricula.sesionesUbicadas
        .filter(sesion => sesion.dia === dia.valor && sesion.horaInicio)
        .sort((a, b) => aMinutos(a.horaInicio!) - aMinutos(b.horaInicio!));

      const finesPorCarril: number[] = [];
      const asignados = sesiones.map(sesion => {
        const desde = aMinutos(sesion.horaInicio!);
        const hasta = sesion.horaFin ? aMinutos(sesion.horaFin) : desde + sesion.duracionHoras * 60;
        let carril = finesPorCarril.findIndex(fin => fin <= desde);
        if (carril === -1) {
          carril = finesPorCarril.length;
          finesPorCarril.push(hasta);
        } else {
          finesPorCarril[carril] = hasta;
        }
        return { sesion, desde, hasta, carril };
      });
      const carriles = Math.max(1, finesPorCarril.length);

      const bloqueos: ColumnaDia['bloqueos'] = [];
      if (parametros) {
        const pausaDesde = aMinutos(parametros.horaInicioPausa);
        const pausaHasta = aMinutos(parametros.horaFinPausa);
        bloqueos.push({ arriba: posicion(pausaDesde), alto: posicion(pausaHasta) - posicion(pausaDesde), etiqueta: 'Pausa' });
        if (dia.valor === 'SABADO') {
          const finSabado = aMinutos(parametros.horaFinSabado);
          const finDia = this.limites().fin;
          if (finDia > finSabado) {
            bloqueos.push({ arriba: posicion(finSabado), alto: posicion(finDia) - posicion(finSabado), etiqueta: 'Fuera de franja' });
          }
        }
      }

      return {
        ...dia,
        ranuras: inicios.map(hora => ({ hora, arriba: posicion(aMinutos(hora)) })),
        bloqueos,
        bloques: asignados.map(({ sesion, desde, hasta, carril }) => ({
          sesion,
          arriba: posicion(desde),
          alto: posicion(hasta) - posicion(desde),
          carril,
          carriles,
          tono: this.tono(sesion)
        }))
      };
    });
  });

  protected readonly horasDelDiaElegido = computed(() => {
    const dia = this.diaElegido();
    const cuadricula = this.cuadricula();
    if (!dia) {
      return [];
    }
    return dia === 'SABADO' ? cuadricula.horasInicioSabado : cuadricula.horasInicioLunesViernes;
  });

  private readonly diaElegido = signal<DiaSemana | ''>('');

  protected alCambiarDia(dia: string): void {
    this.diaElegido.set(dia as DiaSemana | '');
    const horas = this.horasDelDiaElegido();
    if (!horas.includes(this.formulario.controls.horaInicio.value)) {
      this.formulario.controls.horaInicio.setValue('');
    }
  }

  protected iniciarArrastre(evento: DragEvent, sesion: SesionClase): void {
    if (!this.editable()) {
      evento.preventDefault();
      return;
    }
    evento.dataTransfer?.setData('text/plain', String(sesion.id));
    if (evento.dataTransfer) {
      evento.dataTransfer.effectAllowed = 'move';
    }
    this.arrastrando.set(sesion);
    setTimeout(() => this.soltando.set(this.arrastrando() !== null));
  }

  protected terminarArrastre(): void {
    this.arrastrando.set(null);
    this.soltando.set(false);
    this.ranuraActiva.set(null);
    this.bandejaActiva.set(false);
  }

  protected sobreRanura(evento: DragEvent, clave: string): void {
    if (!this.arrastrando()) {
      return;
    }
    evento.preventDefault();
    this.ranuraActiva.set(clave);
  }

  protected soltarEnRanura(evento: DragEvent, dia: DiaSemana, hora: string): void {
    evento.preventDefault();
    const sesion = this.arrastrando();
    this.terminarArrastre();
    if (sesion) {
      this.abrirMovimiento(sesion, dia, hora);
    }
  }

  protected sobreBandeja(evento: DragEvent): void {
    if (this.arrastrando()?.ubicada) {
      evento.preventDefault();
      this.bandejaActiva.set(true);
    }
  }

  protected soltarEnBandeja(evento: DragEvent): void {
    evento.preventDefault();
    const sesion = this.arrastrando();
    this.terminarArrastre();
    if (sesion?.ubicada) {
      this.devolverABandeja(sesion);
    }
  }

  protected abrirMovimiento(sesion: SesionClase, dia?: DiaSemana | null, hora?: string | null): void {
    this.errorMovimiento.set(null);
    this.seleccionada.set(sesion);
    const diaInicial = dia ?? sesion.dia ?? '';
    const horaInicial = hora ?? sesion.horaInicio ?? '';
    const aulaLibre = sesion.aulaId !== null && !(diaInicial && horaInicial && this.aulasOcupadasEn(sesion, diaInicial, horaInicial).has(sesion.aulaId));
    this.diaElegido.set(diaInicial);
    this.formulario.reset({
      dia: diaInicial,
      horaInicio: horaInicial,
      aulaId: aulaLibre ? String(sesion.aulaId) : ''
    });
    if (!this.editable()) {
      this.formulario.disable();
    } else {
      this.formulario.enable();
    }
  }

  protected cerrarMovimiento(): void {
    if (!this.moviendo()) {
      this.seleccionada.set(null);
    }
  }

  protected invalido(campo: keyof typeof this.formulario.controls): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && (control.touched || control.dirty);
  }

  protected confirmarMovimiento(): void {
    const sesion = this.seleccionada();
    if (!sesion) {
      return;
    }
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const valores = this.formulario.getRawValue();
    this.moviendo.set(true);
    this.errorMovimiento.set(null);
    this.sesionService
      .mover(sesion.id, { dia: valores.dia as DiaSemana, horaInicio: valores.horaInicio, aulaId: Number(valores.aulaId) })
      .subscribe({
        next: resultado => {
          this.moviendo.set(false);
          this.seleccionada.set(null);
          this.toast.exito('Sesión movida', `${sesion.codigoAsignatura} G${sesion.numeroGrupo} quedó el ${etiquetaDe(DIAS_SEMANA, resultado.sesion.dia).toLowerCase()} a las ${horaCorta(resultado.sesion.horaInicio)} en ${resultado.sesion.codigoAula}.`);
          if (resultado.advertencias.length) {
            this.toast.advertencia('Advertencias del movimiento', 'La sesión se movió, pero revisa lo siguiente:', resultado.advertencias.map(incidencia => incidencia.mensaje));
          }
          this.cambio.emit();
        },
        error: (error: HttpErrorResponse) => {
          this.moviendo.set(false);
          if (error.status === 401) {
            return;
          }
          this.errorMovimiento.set({ mensaje: mensajeDeError(error), incidencias: leerCuerpo(error)?.incidencias ?? [] });
        }
      });
  }

  protected devolverSeleccionada(): void {
    const sesion = this.seleccionada();
    if (sesion) {
      this.devolverABandeja(sesion);
    }
  }

  private devolverABandeja(sesion: SesionClase): void {
    this.moviendo.set(true);
    this.sesionService.devolverABandeja(sesion.id).subscribe({
      next: () => {
        this.moviendo.set(false);
        this.seleccionada.set(null);
        this.toast.info('Sesión devuelta a la bandeja', `${sesion.codigoAsignatura} G${sesion.numeroGrupo} quedó sin ubicar.`);
        this.cambio.emit();
      },
      error: () => this.moviendo.set(false)
    });
  }
}
