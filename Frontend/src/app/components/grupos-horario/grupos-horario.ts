import { ChangeDetectionStrategy, Component, computed, inject, input, output, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Asignatura } from '../../models/asignatura';
import { Docente } from '../../models/docente';
import { Grupo } from '../../models/grupo';
import { ParametroSistema } from '../../models/parametro';
import { SesionClase } from '../../models/sesion';
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

interface FilaGrupo {
  grupo: Grupo;
  asignatura: Asignatura | undefined;
  docente: Docente | undefined;
  ubicadas: number;
  total: number;
}

@Component({
  selector: 'app-grupos-horario',
  imports: [ReactiveFormsModule, Icono, Spinner, EstadoVacio, Modal, Paginador],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    .progreso { display: inline-flex; align-items: center; gap: 8px; }
    .barra { width: 64px; height: 6px; border-radius: 999px; background: var(--borde); overflow: hidden; }
    .barra span { display: block; height: 100%; background: var(--azeitona); }
    .sin-docente { color: var(--advertencia); font-weight: 500; }
    .aviso { grid-column: 1 / -1; }
  `,
  template: `
    <div class="barra-herramientas">
      <label class="buscador">
        <span class="oculto-visual">Buscar grupo</span>
        <app-icono nombre="buscar" [tamano]="16" />
        <input type="search" placeholder="Buscar por asignatura o docente" [value]="termino()" (input)="termino.set($any($event.target).value); pagina.set(1)" />
      </label>
      <span class="texto-suave">{{ grupos().length }} grupos · {{ sinDocente() }} sin docente</span>
      @if (editable()) {
        <button type="button" class="btn btn-primario" style="margin-left: auto" (click)="abrirNuevo()">
          <app-icono nombre="mas" [tamano]="16" /> Nuevo grupo
        </button>
      }
    </div>

    @if (filas().length === 0) {
      @if (grupos().length === 0) {
        <app-estado-vacio icono="personas" titulo="Este horario no tiene grupos"
                          mensaje="Cada grupo crea automáticamente sus sesiones semanales según la asignatura.">
          @if (editable()) { <button type="button" class="btn btn-primario" (click)="abrirNuevo()">Crear el primer grupo</button> }
        </app-estado-vacio>
      } @else {
        <app-estado-vacio icono="buscar" titulo="Sin coincidencias" mensaje="Ningún grupo coincide con la búsqueda." />
      }
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>Asignatura</th>
              <th>Grupo</th>
              <th>Cupo</th>
              <th>Docente</th>
              <th>Sesiones ubicadas</th>
              @if (editable()) { <th><span class="oculto-visual">Acciones</span></th> }
            </tr>
          </thead>
          <tbody>
            @for (fila of visibles(); track fila.grupo.id) {
              <tr>
                <td><span class="codigo">{{ fila.asignatura?.codigo }}</span> {{ fila.asignatura?.nombre }}</td>
                <td>G{{ fila.grupo.numero }}</td>
                <td>{{ fila.grupo.cupo }}</td>
                <td>
                  @if (fila.docente) { {{ fila.docente.nombre }} }
                  @else { <span class="sin-docente">Sin docente</span> }
                </td>
                <td>
                  <span class="progreso">
                    <span class="barra"><span [style.width.%]="fila.total ? (fila.ubicadas / fila.total) * 100 : 0"></span></span>
                    {{ fila.ubicadas }}/{{ fila.total }}
                  </span>
                </td>
                @if (editable()) {
                  <td>
                    <div class="acciones">
                      <button type="button" class="btn-icono" (click)="abrirEdicion(fila.grupo)" [attr.aria-label]="'Editar grupo ' + fila.asignatura?.codigo + ' G' + fila.grupo.numero" title="Editar">
                        <app-icono nombre="editar" [tamano]="16" />
                      </button>
                      <button type="button" class="btn-icono peligro" (click)="eliminar(fila)" [attr.aria-label]="'Eliminar grupo ' + fila.asignatura?.codigo + ' G' + fila.grupo.numero" title="Eliminar">
                        <app-icono nombre="eliminar" [tamano]="16" />
                      </button>
                    </div>
                  </td>
                }
              </tr>
            }
          </tbody>
        </table>
      </div>
      <app-paginador [total]="filas().length" [tamano]="tamanoPagina" [(pagina)]="pagina" />
    }

    @if (modalAbierto()) {
      <app-modal [titulo]="editando() ? 'Editar grupo' : 'Nuevo grupo'"
                 subtitulo="Si el grupo incumple alguna regla del horario, el servidor lo rechaza y muestra el motivo." (cerrar)="cerrarModal()">
        <form class="formulario" [formGroup]="formulario" (ngSubmit)="guardar()" novalidate>
          <div class="campo completo" [class.invalido]="invalido('asignaturaId')">
            <label for="asignatura">Asignatura</label>
            <select id="asignatura" formControlName="asignaturaId">
              <option value="" disabled>Selecciona la asignatura</option>
              @for (asignatura of asignaturas(); track asignatura.id) {
                <option [value]="asignatura.id">{{ asignatura.codigo }} · {{ asignatura.nombre }} ({{ asignatura.sesionesSemana }} {{ asignatura.sesionesSemana === 1 ? 'sesión' : 'sesiones' }})</option>
              }
            </select>
            @if (invalido('asignaturaId')) { <span class="error-campo">La asignatura del grupo es obligatoria</span> }
            @else if (editando()) { <span class="ayuda-campo">No se puede cambiar la asignatura de un grupo registrado.</span> }
          </div>
          <div class="campo" [class.invalido]="invalido('numero')">
            <label for="numero">Número de grupo</label>
            <input id="numero" type="number" min="1" formControlName="numero" />
            @if (invalido('numero')) { <span class="error-campo">El número del grupo debe ser un entero positivo</span> }
          </div>
          <div class="campo" [class.invalido]="invalido('cupo')">
            <label for="cupo">Cupo</label>
            <input id="cupo" type="number" min="1" formControlName="cupo" />
            @if (invalido('cupo')) { <span class="error-campo">El cupo del grupo debe ser un entero positivo</span> }
            @else if (parametros(); as p) { <span class="ayuda-campo">Cupo máximo {{ p.cupoMaximo }} (+{{ p.toleranciaCupoPorcentaje }}% de tolerancia)</span> }
          </div>
          <div class="campo completo">
            <label for="docente">Docente (opcional)</label>
            <select id="docente" formControlName="docenteId">
              <option value="">Sin docente por ahora</option>
              @if (habilitados().length) {
                <optgroup label="Habilitados para la asignatura">
                  @for (docente of habilitados(); track docente.id) { <option [value]="docente.id">{{ docente.nombre }}</option> }
                </optgroup>
              }
              @if (noHabilitados().length) {
                <optgroup label="No habilitados">
                  @for (docente of noHabilitados(); track docente.id) { <option [value]="docente.id">{{ docente.nombre }}</option> }
                </optgroup>
              }
            </select>
            @if (docenteNoHabilitado()) {
              <span class="ayuda-campo" style="color: var(--advertencia)">Este docente no está habilitado para la asignatura; el servidor puede rechazar el grupo.</span>
            }
          </div>
          <div class="acciones-formulario">
            <button type="button" class="btn btn-secundario" (click)="cerrarModal()" [disabled]="guardando()">Cancelar</button>
            <button type="submit" class="btn btn-primario" [disabled]="guardando()">
              @if (guardando()) { <app-spinner class="en-linea" /> }
              {{ editando() ? 'Guardar cambios' : 'Crear grupo' }}
            </button>
          </div>
        </form>
      </app-modal>
    }
  `
})
export class GruposHorario {
  private readonly servicio = inject(GrupoService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(FormBuilder);

  readonly horarioId = input.required<number>();
  readonly grupos = input.required<Grupo[]>();
  readonly sesiones = input.required<SesionClase[]>();
  readonly asignaturas = input.required<Asignatura[]>();
  readonly docentes = input.required<Docente[]>();
  readonly parametros = input<ParametroSistema | null>(null);
  readonly editable = input(false);
  readonly cambio = output<void>();

  protected readonly tamanoPagina = TAMANO_PAGINA;
  protected readonly termino = signal('');
  protected readonly pagina = signal(1);
  protected readonly guardando = signal(false);
  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Grupo | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    asignaturaId: ['', Validators.required],
    numero: [1, [Validators.required, Validators.min(1)]],
    cupo: [30, [Validators.required, Validators.min(1)]],
    docenteId: ['']
  });

  private readonly valores = toSignal(this.formulario.valueChanges, { initialValue: this.formulario.getRawValue() });

  private readonly filasCompletas = computed<FilaGrupo[]>(() => {
    const asignaturas = new Map(this.asignaturas().map(asignatura => [asignatura.id, asignatura]));
    const docentes = new Map(this.docentes().map(docente => [docente.id, docente]));
    return this.grupos()
      .map(grupo => {
        const propias = this.sesiones().filter(sesion => sesion.grupoId === grupo.id);
        return {
          grupo,
          asignatura: asignaturas.get(grupo.asignaturaId),
          docente: grupo.docenteId !== null ? docentes.get(grupo.docenteId) : undefined,
          ubicadas: propias.filter(sesion => sesion.ubicada).length,
          total: propias.length
        };
      })
      .sort((a, b) => (a.asignatura?.codigo ?? '').localeCompare(b.asignatura?.codigo ?? '') || a.grupo.numero - b.grupo.numero);
  });

  protected readonly filas = computed(() =>
    this.filasCompletas().filter(fila =>
      coincide(this.termino(), fila.asignatura?.codigo, fila.asignatura?.nombre, fila.docente?.nombre, `G${fila.grupo.numero}`)
    )
  );
  protected readonly visibles = computed(() => paginar(this.filas(), this.pagina(), TAMANO_PAGINA));
  protected readonly sinDocente = computed(() => this.grupos().filter(grupo => grupo.docenteId === null).length);

  private readonly asignaturaElegida = computed(() => {
    this.valores();
    return Number(this.formulario.getRawValue().asignaturaId || 0);
  });
  private estaHabilitado(docente: Docente, asignaturaId: number): boolean {
    return !!docente.asignaturasHabilitadas?.some(asignatura => asignatura.id === asignaturaId);
  }
  protected readonly habilitados = computed(() => {
    const id = this.asignaturaElegida();
    return this.docentes().filter(docente => id && this.estaHabilitado(docente, id));
  });
  protected readonly noHabilitados = computed(() => {
    const id = this.asignaturaElegida();
    return this.docentes().filter(docente => !id || !this.estaHabilitado(docente, id));
  });
  protected readonly docenteNoHabilitado = computed(() => {
    this.valores();
    const docenteId = Number(this.formulario.getRawValue().docenteId || 0);
    return !!docenteId && !!this.asignaturaElegida() && this.noHabilitados().some(docente => docente.id === docenteId);
  });

  protected abrirNuevo(): void {
    this.editando.set(null);
    this.formulario.reset({ asignaturaId: '', numero: 1, cupo: 30, docenteId: '' });
    this.formulario.controls.asignaturaId.enable();
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(grupo: Grupo): void {
    this.editando.set(grupo);
    this.formulario.reset({
      asignaturaId: String(grupo.asignaturaId),
      numero: grupo.numero,
      cupo: grupo.cupo,
      docenteId: grupo.docenteId !== null ? String(grupo.docenteId) : ''
    });
    this.formulario.controls.asignaturaId.disable();
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
    const datos: Grupo = {
      numero: Number(valores.numero),
      cupo: Number(valores.cupo),
      asignaturaId: Number(valores.asignaturaId),
      horarioId: this.horarioId(),
      docenteId: valores.docenteId ? Number(valores.docenteId) : null
    };
    const actual = this.editando();
    const peticion = actual?.id ? this.servicio.actualizar(actual.id, datos) : this.servicio.crear(datos);
    this.guardando.set(true);
    peticion.subscribe({
      next: mensaje => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.toast.exito(actual ? 'Grupo actualizado' : 'Grupo creado', mensaje);
        this.cambio.emit();
      },
      error: () => this.guardando.set(false)
    });
  }

  protected async eliminar(fila: FilaGrupo): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Eliminar grupo',
      `Se eliminará el grupo ${fila.grupo.numero} de ${fila.asignatura?.nombre ?? 'la asignatura'} con sus ${fila.total} sesiones.`
    );
    if (!confirmado || !fila.grupo.id) {
      return;
    }
    this.servicio.eliminar(fila.grupo.id).subscribe(mensaje => {
      this.toast.exito('Grupo eliminado', mensaje);
      this.cambio.emit();
    });
  }
}
