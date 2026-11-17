export interface Grupo {
  id?: number;
  numero: number;
  cupo: number;
  asignaturaId: number;
  horarioId: number;
  docenteId: number | null;
}
