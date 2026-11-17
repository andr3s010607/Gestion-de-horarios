import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RestriccionHorariaDocente } from '../models/restriccion';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class RestriccionService extends CrudService<RestriccionHorariaDocente> {
  protected readonly recurso = 'restricciones-horarias-docente';

  obtenerPorDocente(docenteId: number): Observable<RestriccionHorariaDocente[]> {
    return this.http.get<RestriccionHorariaDocente[]>(`${this.url}/docente/${docenteId}`);
  }
}
