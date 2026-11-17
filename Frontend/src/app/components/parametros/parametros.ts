import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ParametroSistema } from '../../models/parametro';
import { AuthService } from '../../services/auth.service';
import { ConfirmacionService } from '../../services/confirmacion.service';
import { ParametroService } from '../../services/parametro.service';
import { ToastService } from '../../services/toast.service';
import { aMinutos, conSegundos, horaCorta } from '../../utils/texto';
import { Icono } from '../icono/icono';
import { Spinner } from '../spinner/spinner';

function posterior(fin: string, inicio: string, error: string) {
  return (grupo: AbstractControl): ValidationErrors | null => {
    const desde = grupo.get(inicio)?.value as string;
    const hasta = grupo.get(fin)?.value as string;
    return desde && hasta && aMinutos(hasta) <= aMinutos(desde) ? { [error]: true } : null;
  };
}

/** La pausa del mediodía debe quedar dentro de la franja de lunes a viernes, como exige el backend. */
function pausaDentroDeFranja(grupo: AbstractControl): ValidationErrors | null {
  const inicio = grupo.get('horaInicioLunesViernes')?.value as string;
  const fin = grupo.get('horaFinLunesViernes')?.value as string;
  const inicioPausa = grupo.get('horaInicioPausa')?.value as string;
  const finPausa = grupo.get('horaFinPausa')?.value as string;
  if (!inicio || !fin || !inicioPausa || !finPausa) {
    return null;
  }
  return aMinutos(inicioPausa) < aMinutos(inicio) || aMinutos(finPausa) > aMinutos(fin) ? { pausaFuera: true } : null;
}

/** Con la duración elegida debe caber al menos una sesión entre semana sin cruzar la pausa. */
function duracionCabe(grupo: AbstractControl): ValidationErrors | null {
  const inicio = grupo.get('horaInicioLunesViernes')?.value as string;
  const fin = grupo.get('horaFinLunesViernes')?.value as string;
  const inicioPausa = grupo.get('horaInicioPausa')?.value as string;
  const finPausa = grupo.get('horaFinPausa')?.value as string;
  const horas = Number(grupo.get('duracionSesionHoras')?.value);
  if (!inicio || !fin || !inicioPausa || !finPausa || !(horas > 0)) {
    return null;
  }
  const duracion = horas * 60;
  for (let minuto = aMinutos(inicio); minuto + duracion <= aMinutos(fin); minuto += 60) {
    const cruzaPausa = minuto < aMinutos(finPausa) && aMinutos(inicioPausa) < minuto + duracion;
    if (!cruzaPausa) {
      return null;
    }
  }
  return { sinHoras: true };
}

@Component({
  selector: 'app-parametros',
  imports: [ReactiveFormsModule, Icono, Spinner],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    .secciones { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px; }
    .seccion { padding: 20px; }
    .seccion header { display: flex; align-items: center; gap: 12px; margin-bottom: 18px; }
    .seccion header span { display: grid; place-items: center; width: 40px; height: 40px; border-radius: 12px; background: var(--fresh-crown); color: var(--fig-leaf); }
    .seccion h3 { font-size: 16px; }
    .seccion header p { margin: 2px 0 0; font-size: 13px; color: var(--texto-suave); }
    .barra-guardar { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; padding: 16px 20px; }
    .barra-guardar p { margin: 0; font-size: 13.5px; color: var(--texto-suave); }
    .botones { display: flex; gap: 10px; }
  `,
  template: `
    <section class="pagina">
      <div class="encabezado-pagina">
        <div>
          <h2>Parámetros del sistema</h2>
          <p>Reglas institucionales que usa el generador y el validador del horario.</p>
        </div>
      </div>

      @if (cargando()) {
        <div class="tarjeta"><app-spinner texto="Cargando parámetros" /></div>
      } @else {
        @if (!auth.esAdministrador()) {
          <div class="alerta alerta-advertencia">Solo un administrador puede modificar estos parámetros.</div>
        }
        <form [formGroup]="formulario" (ngSubmit)="guardar()" novalidate class="pagina">
          <div class="secciones">
            <div class="tarjeta seccion">
              <header>
                <span><app-icono nombre="reloj" [tamano]="20" /></span>
                <div><h3>Franja institucional</h3><p>Horas en las que pueden ubicarse sesiones.</p></div>
              </header>
              <div class="formulario">
                <div class="campo" [class.invalido]="invalido('horaInicioLunesViernes')">
                  <label for="inicio-lv">Inicio lunes a viernes</label>
                  <input id="inicio-lv" type="time" formControlName="horaInicioLunesViernes" />
                </div>
                <div class="campo" [class.invalido]="invalido('horaFinLunesViernes') || error('finLvAntes')">
                  <label for="fin-lv">Fin lunes a viernes</label>
                  <input id="fin-lv" type="time" formControlName="horaFinLunesViernes" />
                  @if (error('finLvAntes')) { <span class="error-campo">Debe ser posterior a la hora de inicio</span> }
                </div>
                <div class="campo completo" [class.invalido]="invalido('horaFinSabado') || error('finSabadoAntes')">
                  <label for="fin-sab">Fin del sábado</label>
                  <input id="fin-sab" type="time" formControlName="horaFinSabado" />
                  @if (error('finSabadoAntes')) { <span class="error-campo">La hora de fin del sábado debe ser posterior a la de inicio de la franja</span> }
                  @else { <span class="ayuda-campo">El sábado inicia a la misma hora que entre semana.</span> }
                </div>
              </div>
            </div>

            <div class="tarjeta seccion">
              <header>
                <span><app-icono nombre="hoja" [tamano]="20" /></span>
                <div><h3>Pausa del mediodía</h3><p>Ninguna sesión puede cruzar este intervalo.</p></div>
              </header>
              <div class="formulario">
                <div class="campo" [class.invalido]="invalido('horaInicioPausa')">
                  <label for="inicio-pausa">Inicio</label>
                  <input id="inicio-pausa" type="time" formControlName="horaInicioPausa" />
                </div>
                <div class="campo" [class.invalido]="invalido('horaFinPausa') || error('finPausaAntes')">
                  <label for="fin-pausa">Fin</label>
                  <input id="fin-pausa" type="time" formControlName="horaFinPausa" />
                  @if (error('finPausaAntes')) { <span class="error-campo">Debe ser posterior al inicio de la pausa</span> }
                </div>
                @if (error('pausaFuera')) {
                  <span class="error-campo completo">La pausa del mediodía debe quedar dentro de la franja de lunes a viernes</span>
                }
              </div>
            </div>

            <div class="tarjeta seccion">
              <header>
                <span><app-icono nombre="personas" [tamano]="20" /></span>
                <div><h3>Sesiones y cupos</h3><p>Duración de cada sesión y límites de estudiantes.</p></div>
              </header>
              <div class="formulario">
                <div class="campo" [class.invalido]="invalido('duracionSesionHoras') || error('sinHoras')">
                  <label for="duracion">Duración de la sesión (horas)</label>
                  <input id="duracion" type="number" min="1" formControlName="duracionSesionHoras" />
                  @if (invalido('duracionSesionHoras')) { <span class="error-campo">La duración de la sesión debe ser un valor positivo</span> }
                  @else if (error('sinHoras')) { <span class="error-campo">Con esa duración no cabe ninguna sesión entre semana sin cruzar la pausa</span> }
                </div>
                <div class="campo" [class.invalido]="invalido('cupoMaximo')">
                  <label for="cupo">Cupo máximo por grupo</label>
                  <input id="cupo" type="number" min="1" formControlName="cupoMaximo" />
                  @if (invalido('cupoMaximo')) { <span class="error-campo">El cupo máximo debe ser un valor positivo</span> }
                </div>
                <div class="campo completo" [class.invalido]="invalido('toleranciaCupoPorcentaje')">
                  <label for="tolerancia">Tolerancia de cupo (%)</label>
                  <input id="tolerancia" type="number" min="0" formControlName="toleranciaCupoPorcentaje" />
                  @if (invalido('toleranciaCupoPorcentaje')) { <span class="error-campo">El porcentaje de tolerancia no puede ser negativo</span> }
                </div>
              </div>
            </div>
          </div>

          @if (auth.esAdministrador()) {
            <div class="tarjeta barra-guardar">
              <p>Los cambios afectan la generación y validación de todos los horarios en borrador.</p>
              <div class="botones">
                <button type="button" class="btn btn-secundario" (click)="restablecer()" [disabled]="guardando() || formulario.pristine">Descartar cambios</button>
                <button type="submit" class="btn btn-primario" [disabled]="guardando() || formulario.pristine">
                  @if (guardando()) { <app-spinner class="en-linea" /> } Guardar parámetros
                </button>
              </div>
            </div>
          }
        </form>
      }
    </section>
  `
})
export class Parametros implements OnInit {
  private readonly servicio = inject(ParametroService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly toast = inject(ToastService);
  protected readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  private originales: ParametroSistema | null = null;

  protected readonly formulario = this.fb.nonNullable.group(
    {
      horaInicioLunesViernes: ['', Validators.required],
      horaFinLunesViernes: ['', Validators.required],
      horaFinSabado: ['', Validators.required],
      horaInicioPausa: ['', Validators.required],
      horaFinPausa: ['', Validators.required],
      duracionSesionHoras: [2, [Validators.required, Validators.min(1)]],
      cupoMaximo: [40, [Validators.required, Validators.min(1)]],
      toleranciaCupoPorcentaje: [0, [Validators.required, Validators.min(0)]]
    },
    {
      validators: [
        posterior('horaFinLunesViernes', 'horaInicioLunesViernes', 'finLvAntes'),
        posterior('horaFinSabado', 'horaInicioLunesViernes', 'finSabadoAntes'),
        posterior('horaFinPausa', 'horaInicioPausa', 'finPausaAntes'),
        pausaDentroDeFranja,
        duracionCabe
      ]
    }
  );

  ngOnInit(): void {
    this.servicio.obtener().subscribe({
      next: parametros => {
        this.aplicar(parametros);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }

  protected invalido(campo: keyof typeof this.formulario.controls): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && (control.touched || control.dirty);
  }

  protected error(clave: string): boolean {
    return this.formulario.hasError(clave) && this.formulario.dirty;
  }

  protected restablecer(): void {
    if (this.originales) {
      this.aplicar(this.originales);
    }
  }

  protected async guardar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.toast.advertencia('Revisa los parámetros', 'Hay valores que no cumplen las reglas del sistema.');
      return;
    }
    const confirmado = await this.confirmacion.confirmar(
      'Guardar parámetros',
      'Las nuevas reglas se aplicarán a la generación y validación de los horarios. Las sesiones ya ubicadas no se mueven, pero podrían generar incidencias.',
      'Guardar',
      false
    );
    if (!confirmado) {
      return;
    }
    const valores = this.formulario.getRawValue();
    const datos: ParametroSistema = {
      horaInicioLunesViernes: conSegundos(valores.horaInicioLunesViernes),
      horaFinLunesViernes: conSegundos(valores.horaFinLunesViernes),
      horaFinSabado: conSegundos(valores.horaFinSabado),
      horaInicioPausa: conSegundos(valores.horaInicioPausa),
      horaFinPausa: conSegundos(valores.horaFinPausa),
      duracionSesionHoras: Number(valores.duracionSesionHoras),
      cupoMaximo: Number(valores.cupoMaximo),
      toleranciaCupoPorcentaje: Number(valores.toleranciaCupoPorcentaje)
    };
    this.guardando.set(true);
    this.servicio.actualizar(datos).subscribe({
      next: parametros => {
        this.guardando.set(false);
        this.aplicar(parametros);
        this.toast.exito('Parámetros actualizados', 'Las reglas del sistema quedaron guardadas.');
      },
      error: () => this.guardando.set(false)
    });
  }

  private aplicar(parametros: ParametroSistema): void {
    this.originales = parametros;
    this.formulario.reset({
      horaInicioLunesViernes: horaCorta(parametros.horaInicioLunesViernes),
      horaFinLunesViernes: horaCorta(parametros.horaFinLunesViernes),
      horaFinSabado: horaCorta(parametros.horaFinSabado),
      horaInicioPausa: horaCorta(parametros.horaInicioPausa),
      horaFinPausa: horaCorta(parametros.horaFinPausa),
      duracionSesionHoras: parametros.duracionSesionHoras,
      cupoMaximo: parametros.cupoMaximo,
      toleranciaCupoPorcentaje: parametros.toleranciaCupoPorcentaje
    });
    if (this.auth.esAdministrador()) {
      this.formulario.enable();
    } else {
      this.formulario.disable();
    }
  }
}
