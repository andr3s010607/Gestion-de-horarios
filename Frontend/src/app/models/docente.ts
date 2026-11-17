import { Asignatura } from './asignatura';
import { TipoVinculacion } from './enums';
import { RestriccionHorariaDocente } from './restriccion';

export interface Docente {
  id?: number;
  nombre: string;
  vinculacion?: TipoVinculacion;
  restricciones?: RestriccionHorariaDocente[];
  asignaturasHabilitadas?: Asignatura[];
}

export interface DocenteAsignatura {
  docenteId: number;
  asignaturaId: number;
}
