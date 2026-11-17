import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Grupo } from '../models/grupo';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class GrupoService extends CrudService<Grupo> {
  protected readonly recurso = 'grupos';

  obtenerPorHorario(horarioId: number): Observable<Grupo[]> {
    return this.http.get<Grupo[]>(`${this.url}/horario/${horarioId}`);
  }
}
