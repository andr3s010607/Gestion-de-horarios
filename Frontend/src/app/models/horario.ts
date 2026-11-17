import { DiaSemana, EstadoHorario } from './enums';
import { Incidencia } from './incidencia';
import { SesionClase, SesionSinUbicar } from './sesion';

export interface Horario {
  id?: number;
  semestre: string;
  estado?: EstadoHorario;
  preseleccionAbierta?: boolean;
}

export interface CuadriculaHorario {
  horario: Horario;
  dias: DiaSemana[];
  horasInicioLunesViernes: string[];
  horasInicioSabado: string[];
  sesionesUbicadas: SesionClase[];
  sesionesSinUbicar: SesionClase[];
}

export interface ResultadoGeneracionHorario {
  sesionesUbicadas: SesionClase[];
  sesionesSinUbicar: SesionSinUbicar[];
  advertencias: Incidencia[];
  totalUbicadas: number;
  totalSinUbicar: number;
}
