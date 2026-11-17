import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { Horario } from '../models/horario';
import { Incidencia } from '../models/incidencia';
import { AsignaturaPreseleccion, Preseleccion } from '../models/preseleccion';
import { conManejoDeErrorPropio } from './contexto-http';

/** Preselección de grupos del estudiante (RF-32 a RF-34). */
@Injectable({ providedIn: 'root' })
export class PreseleccionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/preselecciones`;

  /** Horarios con la preselección abierta y, en solo lectura, los cerrados donde el estudiante confirmó una. */
  horariosVisibles(): Observable<Horario[]> {
    return this.http.get<Horario[]>(`${this.url}/horarios`);
  }

  grupos(horarioId: number): Observable<AsignaturaPreseleccion[]> {
    return this.http.get<AsignaturaPreseleccion[]>(`${this.url}/horario/${horarioId}/grupos`);
  }

  revisarBorrador(horarioId: number, grupoIds: number[]): Observable<Incidencia[]> {
    return this.http.post<Incidencia[]>(`${this.url}/horario/${horarioId}/borrador`, { grupoIds });
  }

  miPreseleccion(horarioId: number): Observable<Preseleccion> {
    return this.http.get<Preseleccion>(`${this.url}/horario/${horarioId}`);
  }

  /** CSV con las sesiones de los grupos que el estudiante confirmó. */
  exportar(horarioId: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.url}/horario/${horarioId}/exportar`, { responseType: 'blob', observe: 'response' });
  }

  confirmar(horarioId: number, grupoIds: number[]): Observable<Preseleccion> {
    return this.http.put<Preseleccion>(`${this.url}/horario/${horarioId}`, { grupoIds }, conManejoDeErrorPropio());
  }
}
