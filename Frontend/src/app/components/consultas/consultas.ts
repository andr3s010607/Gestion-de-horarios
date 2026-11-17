import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { Asignatura } from '../../models/asignatura';
import { Aula } from '../../models/aula';
import { Docente } from '../../models/docente';
import { DIAS_SEMANA, DiaSemana, etiquetaDe } from '../../models/enums';
import { CuadriculaHorario, Horario } from '../../models/horario';
import { ParametroSistema } from '../../models/parametro';
import { SesionClase } from '../../models/sesion';
import { AsignaturaService } from '../../services/asignatura.service';
import { AulaService } from '../../services/aula.service';
import { AuthService } from '../../services/auth.service';
import { DocenteService } from '../../services/docente.service';
import { CriterioConsulta, HorarioService } from '../../services/horario.service';
import { ParametroService } from '../../services/parametro.service';
import { descargarArchivo } from '../../utils/descarga';
import { aMinutos, horaCorta } from '../../utils/texto';
import { CuadriculaHorarioComponent } from '../cuadricula-horario/cuadricula-horario';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Spinner } from '../spinner/spinner';

type Vista = 'cuadricula' | 'lista';

@Component({
  selector: 'app-consultas',
  imports: [Icono, Spinner, EstadoVacio, CuadriculaHorarioComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    .filtros { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)) auto; gap: 14px; align-items: end; padding: 18px 20px; }
    .criterios { display: inline-flex; padding: 4px; border-radius: 999px; background: var(--crema); }
    .criterios button { height: 34px; padding: 0 14px; border: none; border-radius: 999px; background: transparent; color: var(--texto-suave); font-weight: 500; cursor: pointer; }
    .criterios button.activo { background: var(--fig-leaf); color: #fff; }
    .barra-resultados { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; padding: 14px 20px; border-top: 1px solid var(--borde); border-bottom: 1px solid var(--borde); }
    .barra-resultados h3 { font-size: 16px; }
    .barra-resultados p { margin: 2px 0 0; font-size: 13px; color: var(--texto-suave); }
    .acciones-resultados { display: flex; gap: 8px; flex-wrap: wrap; }
    .panel { padding: 16px; }
    @media (max-width: 900px) { .filtros { grid-template-columns: minmax(0, 1fr); } }
  `,
  template: `
    <section class="pagina">
      <div class="encabezado-pagina">
        <div>
          <h2>Consultas</h2>
          <p>Consulta el horario de un docente, un aula o una asignatura y descárgalo en CSV.</p>
        </div>
      </div>

      @if (cargando()) {
        <div class="tarjeta"><app-spinner texto="Cargando datos" /></div>
      } @else if (horarios().length === 0) {
        <div class="tarjeta"><app-estado-vacio icono="calendario" titulo="No hay horarios para consultar" mensaje="Cuando se registre un horario podrás consultarlo aquí." /></div>
      } @else {
        <div class="tarjeta">
          <div class="filtros">
            <div class="campo">
              <label for="horario">Horario</label>
              <select id="horario" [value]="horarioId()" (change)="cambiarHorario($any($event.target).value)">
                @for (horario of horarios(); track horario.id) {
                  <option [value]="horario.id" [selected]="String(horario.id) === horarioId()">{{ horario.semestre }} · {{ horario.estado === 'APROBADO' ? 'Aprobado' : 'Borrador' }}</option>
                }
              </select>
            </div>
            <div class="campo">
              <span class="etiqueta-criterio" style="font-size: 13px; font-weight: 500; color: var(--texto-suave)">Consultar por</span>
              <div class="criterios" role="radiogroup" aria-label="Criterio de consulta">
                @for (opcion of criterios; track opcion.valor) {
                  <button type="button" role="radio" [attr.aria-checked]="criterio() === opcion.valor" [class.activo]="criterio() === opcion.valor"
                          (click)="cambiarCriterio(opcion.valor)">{{ opcion.etiqueta }}</button>
                }
              </div>
            </div>
            <div class="campo">
              <label for="valor">{{ etiquetaValor() }}</label>
              <select id="valor" [value]="valorId()" (change)="valorId.set($any($event.target).value)">
                <option value="">Selecciona…</option>
                @for (opcion of opcionesValor(); track opcion.id) {
                  <option [value]="opcion.id" [selected]="opcion.id === valorId()">{{ opcion.texto }}</option>
                }
              </select>
            </div>
            <button type="button" class="btn btn-primario" (click)="consultar()" [disabled]="!valorId() || consultando()">
              @if (consultando()) { <app-spinner class="en-linea" /> } @else { <app-icono nombre="buscar" [tamano]="16" /> } Consultar
            </button>
          </div>

          @if (resultado() !== null) {
            <div class="barra-resultados">
              <div>
                <h3>{{ tituloResultado() }}</h3>
                <p>{{ resultado()!.length }} sesiones ubicadas en el horario {{ horarioSeleccionado()?.semestre }}</p>
              </div>
              <div class="acciones-resultados">
                <div class="criterios">
                  <button type="button" [class.activo]="vista() === 'cuadricula'" (click)="vista.set('cuadricula')">Cuadrícula</button>
                  <button type="button" [class.activo]="vista() === 'lista'" (click)="vista.set('lista')">Lista</button>
                </div>
                <button type="button" class="btn btn-secundario" (click)="exportar()" [disabled]="exportando()">
                  @if (exportando()) { <app-spinner class="en-linea" style="filter: invert(0.6)" /> } @else { <app-icono nombre="descargar" [tamano]="16" /> } Exportar CSV
                </button>
              </div>
            </div>

            @if (resultado()!.length === 0) {
              <app-estado-vacio icono="calendario" titulo="Sin sesiones" mensaje="No hay sesiones ubicadas para este criterio en el horario seleccionado." />
            } @else if (vista() === 'cuadricula' && cuadriculaFiltrada()) {
              <div class="panel">
                <app-cuadricula-horario [cuadricula]="cuadriculaFiltrada()!" [aulas]="aulas()" [parametros]="parametros()" [editable]="false" />
              </div>
            } @else {
              <div class="tabla-contenedor">
                <table class="tabla">
                  <thead>
                    <tr><th>Día</th><th>Horario</th><th>Asignatura</th><th>Grupo</th><th>Docente</th><th>Aula</th></tr>
                  </thead>
                  <tbody>
                    @for (sesion of ordenadas(); track sesion.id) {
                      <tr>
                        <td>{{ etiquetaDia(sesion.dia) }}</td>
                        <td>{{ horaCorta(sesion.horaInicio) }}–{{ horaCorta(sesion.horaFin) }}</td>
                        <td><span class="codigo">{{ sesion.codigoAsignatura }}</span> {{ sesion.nombreAsignatura }}</td>
                        <td>G{{ sesion.numeroGrupo }}</td>
                        <td>{{ sesion.nombreDocente ?? 'Sin docente' }}</td>
                        <td>{{ sesion.codigoAula }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          } @else {
            <app-estado-vacio icono="buscar" titulo="Elige qué consultar" mensaje="Selecciona el horario, el criterio y el docente, aula o asignatura." />
          }
        </div>
      }
    </section>
  `
})
export class Consultas implements OnInit {
  private readonly horarioService = inject(HorarioService);
  private readonly docenteService = inject(DocenteService);
  private readonly aulaService = inject(AulaService);
  private readonly asignaturaService = inject(AsignaturaService);
  private readonly parametroService = inject(ParametroService);
  private readonly auth = inject(AuthService);

  protected readonly criterios: { valor: CriterioConsulta; etiqueta: string }[] = [
    { valor: 'docente', etiqueta: 'Docente' },
    { valor: 'aula', etiqueta: 'Aula' },
    { valor: 'asignatura', etiqueta: 'Asignatura' }
  ];
  protected readonly horaCorta = horaCorta;
  protected readonly String = String;
  protected readonly etiquetaDia = (dia: DiaSemana | null) => etiquetaDe(DIAS_SEMANA, dia);

  protected readonly cargando = signal(true);
  protected readonly consultando = signal(false);
  protected readonly exportando = signal(false);
  protected readonly horarios = signal<Horario[]>([]);
  protected readonly docentes = signal<Docente[]>([]);
  protected readonly aulas = signal<Aula[]>([]);
  protected readonly asignaturas = signal<Asignatura[]>([]);
  protected readonly parametros = signal<ParametroSistema | null>(null);
  private readonly cuadriculaBase = signal<CuadriculaHorario | null>(null);
  protected readonly horarioId = signal('');
  protected readonly criterio = signal<CriterioConsulta>('docente');
  protected readonly valorId = signal('');
  protected readonly resultado = signal<SesionClase[] | null>(null);
  private readonly consultaActual = signal<{ criterio: CriterioConsulta; valorId: number } | null>(null);
  protected readonly vista = signal<Vista>('cuadricula');

  protected readonly horarioSeleccionado = computed(() => this.horarios().find(horario => String(horario.id) === this.horarioId()));
  protected readonly etiquetaValor = computed(() => ({ docente: 'Docente', aula: 'Aula', asignatura: 'Asignatura' })[this.criterio()]);
  protected readonly opcionesValor = computed(() => {
    switch (this.criterio()) {
      case 'docente':
        return this.docentes().map(docente => ({ id: String(docente.id), texto: docente.nombre }));
      case 'aula':
        return this.aulas().map(aula => ({ id: String(aula.id), texto: aula.codigo }));
      default:
        return this.asignaturas().map(asignatura => ({ id: String(asignatura.id), texto: `${asignatura.codigo} · ${asignatura.nombre}` }));
    }
  });

  protected readonly tituloResultado = computed(() => {
    const consulta = this.consultaActual();
    if (!consulta) {
      return '';
    }
    const id = consulta.valorId;
    switch (consulta.criterio) {
      case 'docente':
        return `Docente: ${this.docentes().find(docente => docente.id === id)?.nombre ?? ''}`;
      case 'aula':
        return `Aula: ${this.aulas().find(aula => aula.id === id)?.codigo ?? ''}`;
      default:
        return `Asignatura: ${this.asignaturas().find(asignatura => asignatura.id === id)?.nombre ?? ''}`;
    }
  });

  protected readonly ordenadas = computed(() => {
    const orden = DIAS_SEMANA.map(dia => dia.valor);
    return [...(this.resultado() ?? [])].sort(
      (a, b) => orden.indexOf(a.dia!) - orden.indexOf(b.dia!) || aMinutos(a.horaInicio ?? '00:00') - aMinutos(b.horaInicio ?? '00:00')
    );
  });

  protected readonly cuadriculaFiltrada = computed<CuadriculaHorario | null>(() => {
    const base = this.cuadriculaBase();
    const sesiones = this.resultado();
    return base && sesiones ? { ...base, sesionesUbicadas: sesiones, sesionesSinUbicar: [] } : null;
  });

  ngOnInit(): void {
    forkJoin({
      horarios: this.horarioService.obtenerTodos(),
      docentes: this.docenteService.obtenerTodos(),
      aulas: this.aulaService.obtenerTodos(),
      asignaturas: this.asignaturaService.obtenerTodos(),
      parametros: this.parametroService.obtener()
    }).subscribe({
      next: datos => {
        const horarios = [...datos.horarios].sort((a, b) => b.semestre.localeCompare(a.semestre));
        this.horarios.set(horarios);
        this.docentes.set([...datos.docentes].sort((a, b) => a.nombre.localeCompare(b.nombre)));
        this.aulas.set([...datos.aulas].sort((a, b) => a.codigo.localeCompare(b.codigo)));
        this.asignaturas.set([...datos.asignaturas].sort((a, b) => a.codigo.localeCompare(b.codigo)));
        this.parametros.set(datos.parametros);
        this.cargando.set(false);
        if (horarios[0]?.id) {
          this.cambiarHorario(String(horarios[0].id));
        }
        const docentePropio = this.auth.sesion()?.docenteId;
        if (this.auth.esDocente() && docentePropio) {
          this.criterio.set('docente');
          this.valorId.set(String(docentePropio));
          this.consultar();
        }
      },
      error: () => this.cargando.set(false)
    });
  }

  protected cambiarHorario(id: string): void {
    this.horarioId.set(id);
    this.resultado.set(null);
    this.cuadriculaBase.set(null);
    this.horarioService.obtenerCuadricula(Number(id)).subscribe(cuadricula => this.cuadriculaBase.set(cuadricula));
    if (this.valorId()) {
      this.consultar();
    }
  }

  protected cambiarCriterio(criterio: CriterioConsulta): void {
    this.criterio.set(criterio);
    this.valorId.set('');
  }

  protected consultar(): void {
    const horarioId = Number(this.horarioId());
    const valorId = Number(this.valorId());
    if (!horarioId || !valorId) {
      return;
    }
    const criterio = this.criterio();
    this.consultando.set(true);
    this.horarioService.consultar(horarioId, criterio, valorId).subscribe({
      next: sesiones => {
        this.consultaActual.set({ criterio, valorId });
        this.resultado.set(sesiones);
        this.consultando.set(false);
      },
      error: () => this.consultando.set(false)
    });
  }

  protected exportar(): void {
    const consulta = this.consultaActual();
    const horario = this.horarioSeleccionado();
    if (!consulta || !horario?.id) {
      return;
    }
    this.exportando.set(true);
    this.horarioService.exportar(horario.id, consulta.criterio, consulta.valorId).subscribe({
      next: respuesta => {
        this.exportando.set(false);
        descargarArchivo(respuesta, `horario-${horario.semestre}-${consulta.criterio}-${consulta.valorId}.csv`);
      },
      error: () => this.exportando.set(false)
    });
  }
}
