import { SesionClase } from './sesion';

/** Grupo que el estudiante puede elegir (RF-32). Del docente solo llega el nombre (RNF-07). */
export interface GrupoPreseleccion {
  grupoId: number;
  numero: number;
  cupo: number;
  nombreDocente: string | null;
  sesiones: SesionClase[];
}

export interface AsignaturaPreseleccion {
  asignaturaId: number;
  codigo: string;
  nombre: string;
  grupos: GrupoPreseleccion[];
}

/** Grupos elegidos por el estudiante en un horario: borrador (RF-33) o preselección confirmada (RF-34). */
export interface Preseleccion {
  horarioId?: number;
  grupoIds: number[];
}

/** Preselecciones confirmadas de un grupo frente a su cupo (RF-35). */
export interface DemandaGrupo {
  grupoId: number;
  asignaturaId: number;
  codigoAsignatura: string;
  nombreAsignatura: string;
  numero: number;
  cupo: number;
  preseleccionados: number;
}
