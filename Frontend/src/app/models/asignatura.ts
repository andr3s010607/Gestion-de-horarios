import { TipoAula } from './enums';

export interface Asignatura {
  id?: number;
  codigo: string;
  nombre: string;
  sesionesSemana: number;
  tipoAulaRequerido: TipoAula;
}
