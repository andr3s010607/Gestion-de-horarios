import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { MovimientoSesion, ResultadoMovimiento, SesionClase } from '../models/sesion';
import { conManejoDeErrorPropio } from './contexto-http';

@Injectable({ providedIn: 'root' })
export class SesionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/sesiones-clase`;

  obtenerTodas(): Observable<SesionClase[]> {
    return this.http.get<SesionClase[]>(this.url);
  }

  mover(id: number, movimiento: MovimientoSesion): Observable<ResultadoMovimiento> {
    return this.http.put<ResultadoMovimiento>(`${this.url}/${id}/mover`, movimiento, conManejoDeErrorPropio());
  }

  devolverABandeja(id: number): Observable<SesionClase> {
    return this.http.put<SesionClase>(`${this.url}/${id}/devolver-a-bandeja`, null);
  }
}
