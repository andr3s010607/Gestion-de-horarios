import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';

import { Horario, RegistroBase, SesionClase } from '../models/gestion.model';
import { ApiService } from './api.service';

export interface CargaCatalogo {
  registros: RegistroBase[];
  dependencias: Record<string, RegistroBase[]>;
  error?: string;
}

export interface CargaHorarios {
  horarios: Horario[];
  semestres: RegistroBase[];
  error?: string;
}

export interface CargaCalendario {
  horarios: Horario[];
  grupos: RegistroBase[];
  aulas: RegistroBase[];
  asignaturas: RegistroBase[];
  sesiones: SesionClase[];
  error?: string;
}

export interface CargaResumen {
  totales: Record<string, number>;
}

const errorCatalogo = 'No fue posible consultar esta información. Verifica que el backend esté activo.';

export const catalogoResolver: ResolveFn<CargaCatalogo> = (ruta) => {
  const api = inject(ApiService);
  const endpoint = ruta.data['endpoint'] as string;
  const dependencias = (ruta.data['dependencias'] as string[] | undefined) ?? [];
  return forkJoin([api.listar<RegistroBase>(endpoint), ...dependencias.map((dependencia) => api.listar<RegistroBase>(dependencia))]).pipe(
    map(([registros, ...listas]) => ({ registros, dependencias: Object.fromEntries(dependencias.map((dependencia, indice) => [dependencia, listas[indice]])) })),
    catchError(() => of({ registros: [], dependencias: {}, error: errorCatalogo })),
  );
};

export const horariosResolver: ResolveFn<CargaHorarios> = () => {
  const api = inject(ApiService);
  return forkJoin({ horarios: api.listar<Horario>('horarios'), semestres: api.listar<RegistroBase>('semestres') }).pipe(
    catchError(() => of({ horarios: [], semestres: [], error: 'No se pudieron cargar los horarios y períodos. Verifica que el backend esté activo.' })),
  );
};

export const calendarioResolver: ResolveFn<CargaCalendario> = () => {
  const api = inject(ApiService);
  return forkJoin({ horarios: api.listar<Horario>('horarios'), grupos: api.listar<RegistroBase>('grupos'), aulas: api.listar<RegistroBase>('aulas'), asignaturas: api.listar<RegistroBase>('asignaturas') }).pipe(
    switchMap((datos) => {
      if (!datos.horarios.length) return of({ ...datos, sesiones: [] });
      return api.obtener<SesionClase[]>(`horarios/${datos.horarios[0].id}/sesiones`).pipe(map((sesiones) => ({ ...datos, sesiones })));
    }),
    catchError(() => of({ horarios: [], grupos: [], aulas: [], asignaturas: [], sesiones: [], error: 'No se pudo cargar el calendario. Verifica que el backend esté activo.' })),
  );
};

export const resumenResolver: ResolveFn<CargaResumen> = () => {
  const api = inject(ApiService);
  return forkJoin({ asignaturas: api.contar('asignaturas'), docentes: api.contar('docentes'), aulas: api.contar('aulas'), horarios: api.contar('horarios') }).pipe(
    map((totales) => ({ totales })),
    catchError(() => of({ totales: {} })),
  );
};
