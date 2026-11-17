import { Injectable } from '@angular/core';
import { Asignatura } from '../models/asignatura';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class AsignaturaService extends CrudService<Asignatura> {
  protected readonly recurso = 'asignaturas';
}
