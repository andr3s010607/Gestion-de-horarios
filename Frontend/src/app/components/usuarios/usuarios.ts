import { ChangeDetectionStrategy, Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Docente } from '../../models/docente';
import { ROLES_USUARIO, RolUsuario, etiquetaDe } from '../../models/enums';
import { Usuario } from '../../models/usuario';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { DocenteService } from '../../services/docente.service';
import { ToastService } from '../../services/toast.service';
import { UsuarioService } from '../../services/usuario.service';
import { coincide } from '../../utils/texto';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Modal } from '../modal/modal';
import { Paginador, paginar } from '../paginador/paginador';
import { Spinner } from '../spinner/spinner';

const TAMANO_PAGINA = 10;

/** Igual que el backend: entre 8 caracteres y 72 bytes (límite de BCrypt). Vacía se permite al editar. */
function claveValida(control: AbstractControl): ValidationErrors | null {
  const valor = (control.value as string) ?? '';
  if (!valor) {
    return null;
  }
  if (valor.length < 8) {
    return { corta: true };
  }
  return new TextEncoder().encode(valor).length > 72 ? { larga: true } : null;
}

/** Solo el rol Docente se vincula a un docente, y en ese caso es obligatorio. */
function rolCoherente(grupo: AbstractControl): ValidationErrors | null {
  const rol = grupo.get('rol')?.value as string;
  const docenteId = grupo.get('docenteId')?.value as string;
  return rol === 'DOCENTE' && !docenteId ? { docenteRequerido: true } : null;
}

@Component({
  selector: 'app-usuarios',
  imports: [ReactiveFormsModule, Icono, Spinner, EstadoVacio, Modal, Paginador],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    .nombre { display: flex; align-items: center; gap: 12px; }
    .avatar { display: grid; place-items: center; width: 34px; height: 34px; border-radius: 50%; background: var(--moss-oscuro); color: var(--fresh-crown); font-size: 13px; font-weight: 600; }
    .campo-clave { position: relative; }
    .campo-clave input { width: 100%; padding-right: 44px; }
    .campo-clave button { position: absolute; right: 6px; top: 50%; transform: translateY(-50%); width: 32px; height: 32px; border: none; background: transparent; color: var(--texto-suave); cursor: pointer; }
  `,
  template: `
    <section class="pagina">
      <div class="encabezado-pagina">
        <div>
          <h2>Usuarios</h2>
          <p>Cuentas de acceso al sistema y su rol. Un usuario docente puede vincularse a su ficha de docente.</p>
        </div>
        <div class="acciones-encabezado">
          <button type="button" class="btn btn-primario" (click)="abrirNuevo()"><app-icono nombre="mas" [tamano]="18" /> Nuevo usuario</button>
        </div>
      </div>

      <div class="tarjeta">
        <div class="barra-herramientas">
          <label class="buscador">
            <span class="oculto-visual">Buscar usuario</span>
            <app-icono nombre="buscar" [tamano]="16" />
            <input type="search" placeholder="Buscar por nombre de usuario" [value]="termino()" (input)="termino.set($any($event.target).value); pagina.set(1)" />
          </label>
          <label>
            <span class="oculto-visual">Filtrar por rol</span>
            <select class="filtro" [value]="filtroRol()" (change)="filtroRol.set($any($event.target).value); pagina.set(1)">
              <option value="">Todos los roles</option>
              @for (rol of roles; track rol.valor) { <option [value]="rol.valor">{{ rol.etiqueta }}</option> }
            </select>
          </label>
        </div>

        @if (cargando()) {
          <app-spinner texto="Cargando usuarios" />
        } @else if (filtrados().length === 0) {
          <app-estado-vacio icono="usuario" titulo="Sin usuarios" mensaje="Ningún usuario coincide con la búsqueda o el filtro." />
        } @else {
          <div class="tabla-contenedor">
            <table class="tabla">
              <thead><tr><th>Usuario</th><th>Rol</th><th>Docente vinculado</th><th><span class="oculto-visual">Acciones</span></th></tr></thead>
              <tbody>
                @for (usuario of visibles(); track usuario.id) {
                  <tr>
                    <td>
                      <span class="nombre">
                        <span class="avatar" aria-hidden="true">{{ usuario.nombreUsuario.slice(0, 2).toUpperCase() }}</span>
                        {{ usuario.nombreUsuario }}
                        @if (esActual(usuario)) { <span class="pildora pildora-verde">Tú</span> }
                      </span>
                    </td>
                    <td>
                      <span class="pildora" [class.pildora-verde]="usuario.rol === 'ADMINISTRADOR'" [class.pildora-petroleo]="usuario.rol === 'DOCENTE'"
                            [class.pildora-tan]="usuario.rol === 'ESTUDIANTE'">{{ etiquetaRol(usuario.rol) }}</span>
                    </td>
                    <td>
                      @if (nombreDocente(usuario); as nombre) { {{ nombre }} } @else { <span class="texto-suave">—</span> }
                    </td>
                    <td>
                      <div class="acciones">
                        <button type="button" class="btn-icono" (click)="abrirEdicion(usuario)" [attr.aria-label]="'Editar ' + usuario.nombreUsuario" title="Editar">
                          <app-icono nombre="editar" [tamano]="16" />
                        </button>
                        <button type="button" class="btn-icono peligro" (click)="eliminar(usuario)" [disabled]="esActual(usuario)"
                                [attr.aria-label]="'Eliminar ' + usuario.nombreUsuario"
                                [title]="esActual(usuario) ? 'No puedes eliminar tu propia cuenta' : 'Eliminar'">
                          <app-icono nombre="eliminar" [tamano]="16" />
                        </button>
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
          <app-paginador [total]="filtrados().length" [tamano]="tamanoPagina" [(pagina)]="pagina" />
        }
      </div>
    </section>

    @if (modalAbierto()) {
      <app-modal [titulo]="editando() ? 'Editar usuario' : 'Nuevo usuario'" (cerrar)="cerrarModal()">
        <form class="formulario" [formGroup]="formulario" (ngSubmit)="guardar()" novalidate>
          <div class="campo completo" [class.invalido]="invalido('nombreUsuario')">
            <label for="nombre-usuario">Nombre de usuario</label>
            <input id="nombre-usuario" formControlName="nombreUsuario" maxlength="60" autocomplete="off" />
            @if (invalido('nombreUsuario')) {
              <span class="error-campo">
                @if (formulario.controls.nombreUsuario.hasError('required')) { El nombre de usuario es obligatorio }
                @else { El nombre de usuario admite hasta 60 caracteres }
              </span>
            }
          </div>
          <div class="campo completo" [class.invalido]="invalido('contrasena')">
            <label for="contrasena">Contraseña</label>
            <div class="campo-clave">
              <input id="contrasena" [type]="verClave() ? 'text' : 'password'" formControlName="contrasena" autocomplete="new-password" />
              <button type="button" (click)="verClave.set(!verClave())" [attr.aria-label]="verClave() ? 'Ocultar contraseña' : 'Mostrar contraseña'">
                <app-icono [nombre]="verClave() ? 'ojoCerrado' : 'ojo'" [tamano]="18" />
              </button>
            </div>
            @if (invalido('contrasena')) {
              <span class="error-campo">
                @if (formulario.controls.contrasena.hasError('required')) { La contraseña es obligatoria }
                @else if (formulario.controls.contrasena.hasError('corta')) { La contraseña debe tener al menos 8 caracteres }
                @else { La contraseña admite hasta 72 caracteres }
              </span>
            }
            @else if (editando()) { <span class="ayuda-campo">Déjala vacía para conservar la contraseña actual. Mínimo 8 caracteres.</span> }
            @else { <span class="ayuda-campo">Entre 8 y 72 caracteres.</span> }
          </div>
          <div class="campo" [class.invalido]="invalido('rol')">
            <label for="rol">Rol</label>
            <select id="rol" formControlName="rol">
              <option value="" disabled>Selecciona el rol</option>
              @for (rol of roles; track rol.valor) { <option [value]="rol.valor">{{ rol.etiqueta }}</option> }
            </select>
            @if (invalido('rol')) { <span class="error-campo">El rol del usuario es obligatorio</span> }
          </div>
          <div class="campo" [class.invalido]="docenteFaltante()">
            <label for="docente">Docente vinculado</label>
            <select id="docente" formControlName="docenteId">
              <option value="">{{ rolElegido() === 'DOCENTE' ? 'Selecciona el docente' : 'Ninguno' }}</option>
              @for (docente of docentes(); track docente.id) { <option [value]="docente.id">{{ docente.nombre }}</option> }
            </select>
            @if (docenteFaltante()) {
              <span class="error-campo">Un usuario con rol Docente debe estar vinculado a un docente</span>
            } @else if (rolElegido() !== 'DOCENTE') {
              <span class="ayuda-campo">Solo los usuarios con rol Docente se vinculan a un docente.</span>
            }
          </div>
          <div class="acciones-formulario">
            <button type="button" class="btn btn-secundario" (click)="cerrarModal()" [disabled]="guardando()">Cancelar</button>
            <button type="submit" class="btn btn-primario" [disabled]="guardando()">
              @if (guardando()) { <app-spinner class="en-linea" /> }
              {{ editando() ? 'Guardar cambios' : 'Crear usuario' }}
            </button>
          </div>
        </form>
      </app-modal>
    }
  `
})
export class Usuarios implements OnInit {
  private readonly servicio = inject(UsuarioService);
  private readonly docenteService = inject(DocenteService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  protected readonly roles = ROLES_USUARIO;
  protected readonly etiquetaRol = (rol: RolUsuario) => etiquetaDe(ROLES_USUARIO, rol);
  protected readonly tamanoPagina = TAMANO_PAGINA;

  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly docentes = signal<Docente[]>([]);
  protected readonly termino = signal('');
  protected readonly filtroRol = signal<RolUsuario | ''>('');
  protected readonly pagina = signal(1);
  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Usuario | null>(null);
  protected readonly verClave = signal(false);

  protected readonly formulario = this.fb.nonNullable.group({
    nombreUsuario: ['', [Validators.required, Validators.maxLength(60)]],
    contrasena: ['', claveValida],
    rol: ['' as RolUsuario | '', Validators.required],
    docenteId: ['']
  }, { validators: rolCoherente });

  private readonly valores = toSignal(this.formulario.valueChanges, { initialValue: this.formulario.getRawValue() });
  protected readonly rolElegido = computed(() => this.valores().rol);
  protected readonly docenteElegido = computed(() => this.valores().docenteId);
  private readonly enviado = signal(false);
  protected readonly docenteFaltante = computed(() =>
    this.rolElegido() === 'DOCENTE' && !this.docenteElegido()
      && (this.enviado() || this.formulario.controls.docenteId.touched));

  constructor() {
    effect(() => {
      if (this.rolElegido() !== 'DOCENTE' && this.docenteElegido()) {
        this.formulario.controls.docenteId.setValue('');
      }
      this.formulario.controls.docenteId[this.rolElegido() === 'DOCENTE' ? 'enable' : 'disable']({ emitEvent: false });
    });
  }

  protected readonly filtrados = computed(() => {
    const rol = this.filtroRol();
    return this.usuarios()
      .filter(usuario => !rol || usuario.rol === rol)
      .filter(usuario => coincide(this.termino(), usuario.nombreUsuario, this.nombreDocente(usuario)));
  });
  protected readonly visibles = computed(() => paginar(this.filtrados(), this.pagina(), TAMANO_PAGINA));

  ngOnInit(): void {
    this.cargar();
  }

  protected nombreDocente(usuario: Usuario): string {
    return usuario.docenteId !== null ? this.docentes().find(docente => docente.id === usuario.docenteId)?.nombre ?? '' : '';
  }

  protected esActual(usuario: Usuario): boolean {
    return usuario.id === this.auth.sesion()?.id;
  }

  protected abrirNuevo(): void {
    this.editando.set(null);
    this.verClave.set(false);
    this.formulario.controls.contrasena.setValidators([Validators.required, claveValida]);
    this.enviado.set(false);
    this.formulario.reset({ nombreUsuario: '', contrasena: '', rol: '', docenteId: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(usuario: Usuario): void {
    this.editando.set(usuario);
    this.verClave.set(false);
    this.formulario.controls.contrasena.setValidators(claveValida);
    this.enviado.set(false);
    this.formulario.reset({
      nombreUsuario: usuario.nombreUsuario,
      contrasena: '',
      rol: usuario.rol,
      docenteId: usuario.docenteId !== null ? String(usuario.docenteId) : ''
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

  protected guardar(): void {
    this.enviado.set(true);
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const valores = this.formulario.getRawValue();
    const datos: Usuario = {
      nombreUsuario: valores.nombreUsuario.trim(),
      contrasena: valores.contrasena ? valores.contrasena : null,
      rol: valores.rol as RolUsuario,
      docenteId: valores.docenteId ? Number(valores.docenteId) : null
    };
    const actual = this.editando();
    const peticion = actual?.id ? this.servicio.actualizar(actual.id, datos) : this.servicio.crear(datos);
    this.guardando.set(true);
    peticion.subscribe({
      next: mensaje => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.toast.exito(actual ? 'Usuario actualizado' : 'Usuario creado', mensaje);
        this.cargar();
      },
      error: () => this.guardando.set(false)
    });
  }

  protected async eliminar(usuario: Usuario): Promise<void> {
    const confirmado = await this.confirmacion.confirmar(
      'Eliminar usuario',
      `La cuenta ${usuario.nombreUsuario} perderá el acceso al sistema. Esta acción no se puede deshacer.`
    );
    if (!confirmado || !usuario.id) {
      return;
    }
    this.servicio.eliminar(usuario.id).subscribe(mensaje => {
      this.toast.exito('Usuario eliminado', mensaje);
      this.cargar();
    });
  }

  private cargar(): void {
    this.cargando.set(true);
    forkJoin({ usuarios: this.servicio.obtenerTodos(), docentes: this.docenteService.obtenerTodos() }).subscribe({
      next: ({ usuarios, docentes }) => {
        this.usuarios.set([...usuarios].sort((a, b) => a.nombreUsuario.localeCompare(b.nombreUsuario)));
        this.docentes.set(docentes);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }
}
