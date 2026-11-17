import { SeveridadIncidencia } from './enums';

export interface Incidencia {
  codigo: string;
  severidad: SeveridadIncidencia;
  mensaje: string;
  sesionIds: number[];
}

export const DESCRIPCION_INCIDENCIA: Record<string, string> = {
  'CHQ-DOC': 'Choque de docente',
  'CHQ-AUL': 'Choque de aula',
  'FRA-INI': 'Fuera de la franja',
  'FRA-PAU': 'Cruza la pausa',
  'DOC-RES': 'Restricción del docente',
  'DOC-HAB': 'Docente no habilitado',
  'CUP-EXC': 'Cupo excedido',
  'AUL-TIP': 'Tipo de aula',
  'AUL-CAP': 'Capacidad de aula',
  'CON-DIA': 'Días consecutivos',
  'PRE-CHQ': 'Choque entre grupos elegidos'
};
