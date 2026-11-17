import { DiaSemana } from './enums';
import { Incidencia } from './incidencia';

export interface SesionClase {
  id: number;
  horarioId: number;
  grupoId: number;
  numeroGrupo: number;
  asignaturaId: number;
  codigoAsignatura: string;
  nombreAsignatura: string;
  docenteId: number | null;
  nombreDocente: string | null;
  aulaId: number | null;
  codigoAula: string | null;
  dia: DiaSemana | null;
  horaInicio: string | null;
  horaFin: string | null;
  duracionHoras: number;
  ubicada: boolean;
}

export interface SesionSinUbicar {
  sesion: SesionClase;
  codigoCausa: string | null;
  causa: string;
}

export interface MovimientoSesion {
  dia: DiaSemana;
  horaInicio: string;
  aulaId: number;
}

export interface ResultadoMovimiento {
  sesion: SesionClase;
  advertencias: Incidencia[];
}
