import { DiaSemana } from './enums';

export interface RestriccionHorariaDocente {
  id?: number;
  docenteId: number;
  dia: DiaSemana;
  horaInicio: string;
  horaFin: string;
}
