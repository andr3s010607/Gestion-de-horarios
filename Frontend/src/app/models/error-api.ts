import { Incidencia } from './incidencia';

export interface ErrorApi {
  timestamp?: string;
  estado?: number;
  mensaje: string;
  incidencias?: Incidencia[];
  sesionesSinUbicar?: number;
}
