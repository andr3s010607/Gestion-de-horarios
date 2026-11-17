import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { Asignatura } from '../models/asignatura';
import { DocenteAsignatura } from '../models/docente';

@Injectable({ providedIn: 'root' })
export class DocenteAsignaturaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/docente-asignaturas`;

  habilitar(habilitacion: DocenteAsignatura): Observable<string> {
    return this.http.post(this.url, habilitacion, { responseType: 'text' });
  }

  obtenerAsignaturasDeDocente(docenteId: number): Observable<Asignatura[]> {
    return this.http.get<Asignatura[]>(`${this.url}/docente/${docenteId}`);
  }

  retirar(docenteId: number, asignaturaId: number): Observable<string> {
    return this.http.delete(`${this.url}/docente/${docenteId}/asignatura/${asignaturaId}`, { responseType: 'text' });
  }
}
