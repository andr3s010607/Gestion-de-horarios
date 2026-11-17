import { Injectable } from '@angular/core';
import { Docente } from '../models/docente';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class DocenteService extends CrudService<Docente> {
  protected readonly recurso = 'docentes';
}
