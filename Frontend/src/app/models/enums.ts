export type DiaSemana = 'LUNES' | 'MARTES' | 'MIERCOLES' | 'JUEVES' | 'VIERNES' | 'SABADO';
export type EstadoHorario = 'BORRADOR' | 'APROBADO';
export type RolUsuario = 'ADMINISTRADOR' | 'DOCENTE' | 'ESTUDIANTE';
export type SeveridadIncidencia = 'ERROR' | 'ADVERTENCIA';
export type TipoAula = 'COMUN' | 'COMPUTADORES' | 'SILLAS_MOVILES';
export type TipoVinculacion = 'TIEMPO_COMPLETO' | 'TRES_CUARTOS' | 'MEDIO_TIEMPO' | 'CUARTO_TIEMPO';

export const DIAS_SEMANA: { valor: DiaSemana; etiqueta: string; corta: string }[] = [
  { valor: 'LUNES', etiqueta: 'Lunes', corta: 'Lun' },
  { valor: 'MARTES', etiqueta: 'Martes', corta: 'Mar' },
  { valor: 'MIERCOLES', etiqueta: 'Miércoles', corta: 'Mié' },
  { valor: 'JUEVES', etiqueta: 'Jueves', corta: 'Jue' },
  { valor: 'VIERNES', etiqueta: 'Viernes', corta: 'Vie' },
  { valor: 'SABADO', etiqueta: 'Sábado', corta: 'Sáb' }
];

export const TIPOS_AULA: { valor: TipoAula; etiqueta: string }[] = [
  { valor: 'COMUN', etiqueta: 'Común' },
  { valor: 'COMPUTADORES', etiqueta: 'Computadores' },
  { valor: 'SILLAS_MOVILES', etiqueta: 'Sillas móviles' }
];

export const TIPOS_VINCULACION: { valor: TipoVinculacion; etiqueta: string }[] = [
  { valor: 'TIEMPO_COMPLETO', etiqueta: 'Tiempo completo' },
  { valor: 'TRES_CUARTOS', etiqueta: 'Tres cuartos' },
  { valor: 'MEDIO_TIEMPO', etiqueta: 'Medio tiempo' },
  { valor: 'CUARTO_TIEMPO', etiqueta: 'Cuarto de tiempo' }
];

export const ROLES_USUARIO: { valor: RolUsuario; etiqueta: string }[] = [
  { valor: 'ADMINISTRADOR', etiqueta: 'Administrador' },
  { valor: 'DOCENTE', etiqueta: 'Docente' },
  { valor: 'ESTUDIANTE', etiqueta: 'Estudiante' }
];

export function etiquetaDe<T extends string>(lista: { valor: T; etiqueta: string }[], valor: T | null | undefined): string {
  return lista.find(opcion => opcion.valor === valor)?.etiqueta ?? '';
}
