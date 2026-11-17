import { TipoAula } from './enums';

export interface Aula {
  id?: number;
  codigo: string;
  capacidad: number;
  tipo: TipoAula;
}
