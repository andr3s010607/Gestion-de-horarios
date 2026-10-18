import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, retry, timer } from 'rxjs';

import { environment } from '../../environments/environment';
import { RegistroBase } from '../models/gestion.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  listar<T extends RegistroBase>(endpoint: string): Observable<T[]> {
    return this.obtenerFresco<T[]>(endpoint);
  }

  contar(endpoint: string): Observable<number> {
    return this.obtenerFresco<number>(`${endpoint}/count`);
  }

  crear(endpoint: string, datos: RegistroBase): Observable<string> {
    return this.http.post(`${this.apiUrl}/${endpoint}`, datos, { responseType: 'text' });
  }

  actualizar(endpoint: string, id: number, datos: RegistroBase): Observable<string> {
    return this.http.put(`${this.apiUrl}/${endpoint}/${id}`, datos, { responseType: 'text' });
  }

  eliminar(endpoint: string, id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${endpoint}/${id}`, { responseType: 'text' });
  }

  obtener<T>(ruta: string): Observable<T> {
    return this.obtenerFresco<T>(ruta);
  }

  post<T>(ruta: string, datos: RegistroBase = {}): Observable<T> {
    return this.http.post<T>(`${this.apiUrl}/${ruta}`, datos);
  }

  descargar(ruta: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${ruta}`, { responseType: 'blob' });
  }

  private obtenerFresco<T>(ruta: string): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}/${ruta}`, {
      params: new HttpParams().set('_', Date.now().toString()),
      headers: { 'Cache-Control': 'no-cache', Pragma: 'no-cache' },
    }).pipe(retry({ count: 6, delay: () => timer(750) }));
  }
}
