import { RolUsuario } from '../../models/enums';

export interface ElementoNavegacion {
  ruta: string;
  etiqueta: string;
  icono: string;
  roles: RolUsuario[];
}

export interface GrupoNavegacion {
  titulo: string;
  elementos: ElementoNavegacion[];
}

const TODOS: RolUsuario[] = ['ADMINISTRADOR', 'DOCENTE', 'ESTUDIANTE'];
const SOLO_ADMINISTRADOR: RolUsuario[] = ['ADMINISTRADOR'];
const SOLO_ESTUDIANTE: RolUsuario[] = ['ESTUDIANTE'];

export const NAVEGACION: GrupoNavegacion[] = [
  {
    titulo: 'General',
    elementos: [
      { ruta: '/dashboard', etiqueta: 'Panel', icono: 'panel', roles: TODOS },
      { ruta: '/horarios', etiqueta: 'Horarios', icono: 'calendario', roles: TODOS },
      { ruta: '/consultas', etiqueta: 'Consultas', icono: 'buscar', roles: TODOS },
      { ruta: '/preseleccion', etiqueta: 'Preselección', icono: 'check', roles: SOLO_ESTUDIANTE }
    ]
  },
  {
    titulo: 'Catálogos',
    elementos: [
      { ruta: '/asignaturas', etiqueta: 'Asignaturas', icono: 'libro', roles: TODOS },
      { ruta: '/docentes', etiqueta: 'Docentes', icono: 'personas', roles: TODOS },
      { ruta: '/aulas', etiqueta: 'Aulas', icono: 'edificio', roles: TODOS }
    ]
  },
  {
    titulo: 'Sistema',
    elementos: [
      { ruta: '/parametros', etiqueta: 'Parámetros', icono: 'ajustes', roles: TODOS },
      { ruta: '/usuarios', etiqueta: 'Usuarios', icono: 'escudo', roles: SOLO_ADMINISTRADOR }
    ]
  }
];

export const ETIQUETA_ROL: Record<RolUsuario, string> = {
  ADMINISTRADOR: 'Administrador',
  DOCENTE: 'Docente',
  ESTUDIANTE: 'Estudiante'
};
