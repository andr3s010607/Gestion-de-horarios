import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { Horario, RegistroBase } from '../../models/gestion.model';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';
import { CargaHorarios } from '../../services/datos.resolver';

interface ResultadoGeneracion { sesionesCreadas?: number; advertencias?: string[]; mensaje?: string; }

@Component({ selector: 'app-horarios', imports: [CommonModule, FormsModule], templateUrl: './horarios.component.html', styleUrl: './horarios.component.css' })
export class HorariosComponent implements OnInit {
  horarios: Horario[] = []; semestres: RegistroBase[] = [];
  borrador: RegistroBase = { nombre: '', semestreId: '', estado: 'BORRADOR' };
  cargando = true; guardando = false; mensaje = ''; error = ''; validacion: string[] = []; resultado?: ResultadoGeneracion;
  constructor(private readonly api: ApiService, private readonly toast: ToastService, private readonly route: ActivatedRoute) {}
  ngOnInit(): void { this.aplicarCargaInicial(this.route.snapshot.data['carga'] as CargaHorarios | undefined); }
  cargarDatosIniciales(): void { this.cargando = true; this.error = ''; forkJoin({ horarios: this.api.listar<Horario>('horarios'), semestres: this.api.listar<RegistroBase>('semestres') }).subscribe({ next: (datos) => { this.horarios = datos.horarios; this.semestres = datos.semestres; this.cargando = false; }, error: () => { this.error = 'No se pudieron cargar los horarios y períodos. Verifica que el backend esté activo.'; this.cargando = false; } }); }
  cargar(): void { this.cargando = true; this.error = ''; this.api.listar<Horario>('horarios').subscribe({ next: (datos) => { this.horarios = datos; this.cargando = false; }, error: () => { this.error = 'No se pudieron consultar los horarios. Verifica que el backend esté activo.'; this.cargando = false; } }); }
  crear(): void { this.guardando = true; this.error = ''; this.api.crear('horarios', { ...this.borrador, semestreId: Number(this.borrador['semestreId']) }).subscribe({ next: (mensaje) => { this.guardando = false; this.mensaje = mensaje; this.toast.exito('Horario creado', mensaje); this.borrador = { nombre: '', semestreId: '', estado: 'BORRADOR' }; this.cargar(); }, error: (respuesta) => { this.guardando = false; this.error = respuesta.status === 403 ? 'Tu cuenta no tiene permiso para crear este horario. Cierra sesión e ingresa nuevamente.' : 'No fue posible crear el horario. Revisa los datos e inténtalo de nuevo.'; this.toast.error('No se creó el horario', this.error); } }); }
  generar(horario: Horario): void { this.limpiarResultado(); this.guardando = true; this.api.post<ResultadoGeneracion>(`horarios/${horario.id}/generar`).subscribe({ next: (resultado) => { this.guardando = false; this.resultado = resultado; this.mensaje = resultado.mensaje || 'Propuesta generada. Revisa las sesiones en el calendario.'; this.toast.exito('Propuesta generada', this.mensaje); this.cargar(); }, error: (respuesta) => { this.guardando = false; this.error = respuesta.error || 'No se pudo generar la propuesta.'; this.toast.error('No se generó el horario', this.error); } }); }
  validar(horario: Horario): void { this.limpiarResultado(); this.api.obtener<string[]>(`horarios/${horario.id}/validar`).subscribe({ next: (resultado) => { this.validacion = resultado; this.mensaje = resultado.length ? 'Encontramos aspectos por revisar.' : 'El horario no presenta errores de validación.'; this.toast[resultado.length ? 'advertencia' : 'exito'](resultado.length ? 'Hay hallazgos por revisar' : 'Horario validado', this.mensaje); }, error: () => { this.error = 'No se pudo validar el horario.'; this.toast.error('Validación no disponible', this.error); } }); }
  cambiarEstado(horario: Horario, estado: string): void { this.api.actualizar('horarios', horario.id!, { ...horario, estado }).subscribe({ next: (mensaje) => { this.mensaje = mensaje; this.toast.exito('Estado actualizado', mensaje); this.cargar(); }, error: (respuesta) => { this.error = respuesta.error || 'No se pudo actualizar el estado.'; this.toast.error('No se actualizó el estado', this.error); } }); }
  eliminar(horario: Horario): void { if (!horario.id || !confirm(`¿Eliminar “${horario.nombre}”?`)) return; this.api.eliminar('horarios', horario.id).subscribe({ next: (mensaje) => { this.mensaje = mensaje; this.toast.exito('Horario eliminado', mensaje); this.cargar(); }, error: (respuesta) => { this.error = respuesta.error || 'No se pudo eliminar.'; this.toast.error('No se eliminó el horario', this.error); } }); }
  exportar(horario: Horario): void { this.api.descargar(`horarios/${horario.id}/exportar`).subscribe({ next: (archivo) => { const url = URL.createObjectURL(archivo); const enlace = document.createElement('a'); enlace.href = url; enlace.download = `horario-${horario.id}.csv`; enlace.click(); URL.revokeObjectURL(url); this.toast.exito('Exportación lista', 'El archivo CSV se descargó en tu equipo.'); }, error: () => { this.error = 'No se pudo exportar el horario.'; this.toast.error('No se exportó el horario', this.error); } }); }
  textoEstado(estado: string): string { return estado.charAt(0) + estado.slice(1).toLowerCase(); }
  private aplicarCargaInicial(carga?: CargaHorarios): void { if (!carga) { this.cargarDatosIniciales(); return; } this.horarios = carga.horarios; this.semestres = carga.semestres; this.error = carga.error ?? ''; this.cargando = false; }
  private limpiarResultado(): void { this.error = ''; this.mensaje = ''; this.validacion = []; this.resultado = undefined; }
}
