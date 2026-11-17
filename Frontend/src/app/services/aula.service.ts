import { Injectable } from '@angular/core';
import { Aula } from '../models/aula';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class AulaService extends CrudService<Aula> {
  protected readonly recurso = 'aulas';
}
