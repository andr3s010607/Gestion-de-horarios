import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Grupo } from '../../models/grupo';
import { Horario } from '../../models/horario';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { GrupoService } from '../../services/grupo.service';
import { HorarioService } from '../../services/horario.service';
import { ToastService } from '../../services/toast.service';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Modal } from '../modal/modal';
import { Spinner } from '../spinner/spinner';

export const PATRON_SEMESTRE = /^\d{4}-[12]$/;

@Component({
  selector: 'app-horarios',
  imports: [ReactiveFormsModule, RouterLink, Icono, Spinner, EstadoVacio, Modal],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    .rejilla { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 16px; }
    .horario { display: flex; flex-direction: column; gap: 14px; padding: 20px; transition: transform 0.15s, box-shadow 0.15s; }
    .horario:hover { transform: translateY(-2px); box-shadow: 0 10px 28px rgba(54, 47, 34, 0.1); }
    .cabecera { display: flex; align-items: center; justify-content: space-between; gap: 10px; }
    .sello-horario { display: grid; place-items: center; width: 44px; height: 44px; border-radius: 14px; background: var(--moss-oscuro); color: var(--fresh-crown); }
    .sello-horario.aprobado { background: var(--fig-leaf); }
    h3 { font-size: 20px; }
    .subtexto { margin: 2px 0 0; font-size: 13.5px; color: var(--texto-suave); }
    .acciones-tarjeta { display: flex; align-items: center; gap: 8px; margin-top: auto; padding-top: 4px; }
    .acciones-tarjeta .btn { flex: 1; }
  `,
  template: `
    <section class="pagina">
      <div class="encabezado-pagina">
        <div>
          <h2>Horarios</h2>
          <p>Un horario por semestre. Mientras está en borrador puede editarse; una vez aprobado queda bloqueado.</p>
        </div>
        @if (auth.esAdministrador()) {
          <div class="acciones-encabezado">
            <button type="button" class="btn btn-primario" (click)="abrirNuevo()">
              <app-icono nombre="mas" [tamano]="18" /> Nuevo horario
            </button>
          </div>
        }
      </div>

      @if (cargando()) {
        <div class="tarjeta"><app-spinner texto="Cargando horarios" /></div>
      } @else if (horarios().length === 0) {
        <div class="tarjeta">
          <app-estado-vacio icono="calendario" titulo="Aún no hay horarios" mensaje="Crea el horario del semestre para registrar grupos y armar el horario.">
            @if (auth.esAdministrador()) {
              <button type="button" class="btn btn-primario" (click)="abrirNuevo()">Crear el primero</button>
            }
          </app-estado-vacio>
        </div>
      } @else {
        <div class="rejilla">
          @for (horario of ordenados(); track horario.id) {
            <article class="tarjeta horario">
              <div class="cabecera">
                <span class="sello-horario" [class.aprobado]="horario.estado === 'APROBADO'">
                  <app-icono [nombre]="horario.estado === 'APROBADO' ? 'candado' : 'calendario'" [tamano]="20" />
                </span>
                <span class="pildora" [class.pildora-verde]="horario.estado === 'APROBADO'" [class.pildora-tan]="horario.estado !== 'APROBADO'">
                  {{ horario.estado === 'APROBADO' ? 'Aprobado' : 'Borrador' }}
                </span>
              </div>
              <div>
                <h3>Semestre {{ horario.semestre }}</h3>
                <p class="subtexto">{{ gruposDe(horario) }} grupos registrados</p>
              </div>
              <div class="acciones-tarjeta">
                <a class="btn btn-primario" [routerLink]="['/horarios', horario.id]">Abrir</a>
                @if (auth.esAdministrador()) {
                  <button type="button" class="btn-icono" (click)="abrirEdicion(horario)" [disabled]="horario.estado === 'APROBADO'"
                          [title]="horario.estado === 'APROBADO' ? 'Devuélvelo a borrador para editarlo' : 'Editar semestre'"
                          [attr.aria-label]="'Editar horario ' + horario.semestre">
                    <app-icono nombre="editar" [tamano]="16" />
                  </button>
                  <button type="button" class="btn-icono peligro" (click)="eliminar(horario)" [disabled]="horario.estado === 'APROBADO'"
                          [title]="horario.estado === 'APROBADO' ? 'Un horario aprobado no puede eliminarse' : 'Eliminar'"
                          [attr.aria-label]="'Eliminar horario ' + horario.semestre">
                    <app-icono nombre="eliminar" [tamano]="16" />
                  </button>
                }
              </div>
            </article>
          }
        </div>
      }
    </section>

    @if (modalAbierto()) {
      <app-modal [titulo]="editando() ? 'Editar horario' : 'Nuevo horario'" subtitulo="El semestre debe tener el formato AAAA-1 o AAAA-2." [ancho]="420" (cerrar)="cerrarModal()">
        <form class="formulario" [formGroup]="formulario" (ngSubmit)="guardar()" novalidate>
          <div class="campo completo" [class.invalido]="invalido()">
            <label for="semestre">Semestre</label>
            <input id="semestre" formControlName="semestre" placeholder="Ej. 2027-1" maxlength="6" autocomplete="off" />
            @if (invalido()) {
              <span class="error-campo">
                @if (formulario.controls.semestre.hasError('required')) { El semestre del horario es obligatorio }
                @else { El semestre debe tener el formato AAAA-1 o AAAA-2 }
              </span>
            }
          </div>
          <div class="acciones-formulario">
            <button type="button" class="btn btn-secundario" (click)="cerrarModal()" [disabled]="guardando()">Cancelar</button>
            <button type="submit" class="btn btn-primario" [disabled]="guardando()">
              @if (guardando()) { <app-spinner class="en-linea" /> }
              {{ editando() ? 'Guardar cambios' : 'Crear horario' }}
            </button>
          </div>
        </form>
      </app-modal>
    }
  `
})
export class Horarios implements OnInit {
  private readonly servicio = inject(HorarioService);
  private readonly grupoService = inject(GrupoService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  protected readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly horarios = signal<Horario[]>([]);
  private readonly grupos = signal<Grupo[]>([]);
  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Horario | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    semestre: ['', [Validators.required, Validators.pattern(PATRON_SEMESTRE)]]
  });

  protected readonly ordenados = computed(() => [...this.horarios()].sort((a, b) => b.semestre.localeCompare(a.semestre)));

  ngOnInit(): void {
    this.cargar();
  }

  protected gruposDe(horario: Horario): number {
    return this.grupos().filter(grupo => grupo.horarioId === horario.id).length;
  }

  protected abrirNuevo(): void {
    this.editando.set(null);
    this.formulario.reset({ semestre: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(horario: Horario): void {
    this.editando.set(horario);
    this.formulario.reset({ semestre: horario.semestre });
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    if (!this.guardando()) {
      this.modalAbierto.set(false);
    }
  }

  protected invalido(): boolean {
    const control = this.formulario.controls.semestre;
    return control.invalid && (control.touched || control.dirty);
  }

  protected guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const datos: Horario = { semestre: this.formulario.getRawValue().semestre.trim() };
    const actual = this.editando();
    const peticion = actual?.id ? this.servicio.actualizar(actual.id, datos) : this.servicio.crear(datos);
    this.guardando.set(true);
    peticion.subscribe({
      next: mensaje => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.toast.exito(actual ? 'Horario actualizado' : 'Horario creado', mensaje);
        this.cargar();
      },
      error: () => this.guardando.set(false)
    });
  }

  protected async eliminar(horario: Horario): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Eliminar horario',
      `Se eliminará el horario ${horario.semestre} con todos sus grupos y sesiones. Esta acción no se puede deshacer.`
    );
    if (!confirmado || !horario.id) {
      return;
    }
    this.servicio.eliminar(horario.id).subscribe(mensaje => {
      this.toast.exito('Horario eliminado', mensaje);
      this.cargar();
    });
  }

  private cargar(): void {
    this.cargando.set(true);
    forkJoin({ horarios: this.servicio.obtenerTodos(), grupos: this.grupoService.obtenerTodos() }).subscribe({
      next: ({ horarios, grupos }) => {
        this.horarios.set(horarios);
        this.grupos.set(grupos);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }
}
