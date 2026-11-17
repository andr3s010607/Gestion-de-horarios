import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { ParametroSistema } from '../models/parametro';

@Injectable({ providedIn: 'root' })
export class ParametroService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/parametros-sistema`;

  obtener(): Observable<ParametroSistema> {
    return this.http.get<ParametroSistema>(this.url);
  }

  actualizar(parametros: ParametroSistema): Observable<ParametroSistema> {
    return this.http.put<ParametroSistema>(this.url, parametros);
  }
}
