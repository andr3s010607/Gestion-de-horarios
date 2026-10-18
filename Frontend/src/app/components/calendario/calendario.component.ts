import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of, switchMap } from 'rxjs';

import { Horario, RegistroBase, SesionClase } from '../../models/gestion.model';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { CargaCalendario } from '../../services/datos.resolver';

const dias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];

@Component({ selector: 'app-calendario', imports: [CommonModule, FormsModule], templateUrl: './calendario.component.html', styleUrl: './calendario.component.css' })
export class CalendarioComponent implements OnInit {
  readonly dias = dias; readonly horas = ['07:00', '08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00'];
  horarios: Horario[] = []; grupos: RegistroBase[] = []; aulas: RegistroBase[] = []; asignaturas: RegistroBase[] = [];
  horarioId = ''; sesiones: SesionClase[] = []; cargando = true; guardando = false; mensaje = ''; error = '';
  borrador: RegistroBase = { grupoId: '', aulaId: '', dia: 'LUNES', horaInicio: '07:00', horaFin: '09:00' };
  constructor(private readonly api: ApiService, private readonly auth: AuthService, private readonly toast: ToastService, private readonly route: ActivatedRoute) {}
  get puedeEditar(): boolean { return this.auth.puedePlanificar(); }
  ngOnInit(): void { this.aplicarCargaInicial(this.route.snapshot.data['carga'] as CargaCalendario | undefined); }
  cargarDatosIniciales(): void {
    this.cargando = true;
    this.error = '';
    forkJoin({ horarios: this.api.listar<Horario>('horarios'), grupos: this.api.listar<RegistroBase>('grupos'), aulas: this.api.listar<RegistroBase>('aulas'), asignaturas: this.api.listar<RegistroBase>('asignaturas') })
      .pipe(switchMap((datos) => {
        this.horarios = datos.horarios;
        this.grupos = datos.grupos;
        this.aulas = datos.aulas;
        this.asignaturas = datos.asignaturas;
        if (!this.horarios.length) { this.horarioId = ''; this.sesiones = []; return of(null); }
        this.horarioId = String(this.horarios[0].id);
        return this.api.obtener<SesionClase[]>(`horarios/${this.horarioId}/sesiones`);
      }))
      .subscribe({ next: (sesiones) => { if (sesiones) this.sesiones = sesiones; this.cargando = false; }, error: () => { this.error = 'No se pudo cargar el calendario. Verifica que el backend esté activo.'; this.cargando = false; } });
  }
  cargarSesiones(): void { if (!this.horarioId) { this.sesiones = []; return; } this.cargando = true; this.api.obtener<SesionClase[]>(`horarios/${this.horarioId}/sesiones`).subscribe({ next: (datos) => { this.sesiones = datos; this.cargando = false; }, error: () => { this.error = 'No se pudieron cargar las sesiones de este horario.'; this.cargando = false; } }); }
  crearSesion(): void { if (!this.horarioId || !this.puedeEditar) return; this.guardando = true; this.mensaje = ''; this.error = ''; const datos = { ...this.borrador, horarioId: Number(this.horarioId), grupoId: Number(this.borrador['grupoId']), aulaId: Number(this.borrador['aulaId']) }; this.api.crear('sesiones-clase', datos).subscribe({ next: (mensaje) => { this.guardando = false; this.mensaje = mensaje; this.toast.exito('Sesión agregada', mensaje); this.cargarSesiones(); }, error: (respuesta) => { this.guardando = false; this.error = respuesta.error || 'No fue posible registrar la sesión. Comprueba sus conflictos y datos.'; this.toast.error('No se agregó la sesión', this.error); } }); }
  eliminar(sesion: SesionClase): void { if (!sesion.id || !this.puedeEditar || !confirm('¿Eliminar esta sesión?')) return; this.api.eliminar('sesiones-clase', sesion.id).subscribe({ next: (mensaje) => { this.mensaje = mensaje; this.toast.exito('Sesión eliminada', mensaje); this.cargarSesiones(); }, error: (respuesta) => { this.error = respuesta.error || 'No fue posible eliminar la sesión.'; this.toast.error('No se eliminó la sesión', this.error); } }); }
  sesionesDia(dia: string): SesionClase[] { return this.sesiones.filter((sesion) => sesion.dia === dia); }
  etiquetaDia(dia: string): string { return dia.charAt(0) + dia.slice(1).toLowerCase(); }
  nombreGrupo(id: number): string { const grupo = this.grupos.find((item) => item.id === id); const asignatura = grupo ? this.asignaturas.find((item) => item.id === grupo['asignaturaId']) : undefined; return String(asignatura?.['nombre'] ?? grupo?.['codigo'] ?? `Grupo #${id}`); }
  nombreAula(id: number): string { const aula = this.aulas.find((item) => item.id === id); return String(aula?.['nombre'] ?? aula?.['codigo'] ?? `Aula #${id}`); }
  posicion(sesion: SesionClase): Record<string, string> { const inicio = Number(sesion.horaInicio?.slice(0, 2) ?? 7); const fin = Number(sesion.horaFin?.slice(0, 2) ?? inicio + 1); return { top: `${(inicio - 7) * 62 + 3}px`, height: `${Math.max((fin - inicio) * 62 - 6, 45)}px` }; }
  private aplicarCargaInicial(carga?: CargaCalendario): void { if (!carga) { this.cargarDatosIniciales(); return; } this.horarios = carga.horarios; this.grupos = carga.grupos; this.aulas = carga.aulas; this.asignaturas = carga.asignaturas; this.sesiones = carga.sesiones; this.horarioId = carga.horarios.length ? String(carga.horarios[0].id) : ''; this.error = carga.error ?? ''; this.cargando = false; }
}
