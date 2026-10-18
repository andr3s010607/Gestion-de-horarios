import { Routes } from '@angular/router';

import { AppShellComponent } from './components/app-shell/app-shell.component';
import { CalendarioComponent } from './components/calendario/calendario.component';
import { CatalogoComponent } from './components/catalogo/catalogo.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HorariosComponent } from './components/horarios/horarios.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard, rolGuard } from './services/auth.guard';
import { calendarioResolver, catalogoResolver, horariosResolver, resumenResolver } from './services/datos.resolver';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, title: 'Ingreso | Gestión de Horarios' },
  {
    path: '', component: AppShellComponent, canActivate: [authGuard], children: [
      { path: 'inicio', component: DashboardComponent, resolve: { carga: resumenResolver }, title: 'Vista general | Gestión de Horarios' },
      { path: 'academico/asignaturas', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'asignaturas', endpoint: 'asignaturas' }, title: 'Plan de estudios' },
      { path: 'academico/semestres', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'semestres', endpoint: 'semestres' }, title: 'Períodos académicos' },
      { path: 'academico/grupos', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'grupos', endpoint: 'grupos', dependencias: ['asignaturas', 'semestres', 'docentes', 'docentes-asignaturas'] }, title: 'Grupos y carga' },
      { path: 'personas/docentes', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'docentes', endpoint: 'docentes', dependencias: ['asignaturas', 'docentes-asignaturas'] }, title: 'Docentes' },
      { path: 'personas/carga', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'docentes-asignaturas', endpoint: 'docentes-asignaturas', dependencias: ['docentes', 'asignaturas'] }, title: 'Carga docente' },
      { path: 'personas/restricciones', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'restricciones', endpoint: 'restricciones-horarias-docente', dependencias: ['docentes'] }, title: 'Restricciones docentes' },
      { path: 'espacios/aulas', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'aulas', endpoint: 'aulas' }, title: 'Aulas' },
      { path: 'espacios/recursos', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'recursos', endpoint: 'recursos-aula' }, title: 'Recursos de aula' },
      { path: 'espacios/inventario', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'aulas-recursos', endpoint: 'aulas-recursos', dependencias: ['aulas', 'recursos-aula'] }, title: 'Inventario de aulas' },
      { path: 'academico/requisitos', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'requisitos', endpoint: 'asignaturas-recursos-requeridos', dependencias: ['asignaturas', 'recursos-aula'] }, title: 'Requisitos de materias' },
      { path: 'horarios', component: HorariosComponent, canActivate: [rolGuard(['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'])], resolve: { carga: horariosResolver }, title: 'Diseñar horario' },
      { path: 'calendario', component: CalendarioComponent, resolve: { carga: calendarioResolver }, title: 'Calendario semanal' },
      { path: 'administracion/parametros', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'parametros', endpoint: 'parametros-sistema' }, title: 'Parámetros' },
      { path: 'administracion/usuarios', component: CatalogoComponent, canActivate: [rolGuard(['ADMINISTRADOR'])], resolve: { carga: catalogoResolver }, data: { catalogo: 'usuarios', endpoint: 'usuarios' }, title: 'Usuarios' },
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
    ],
  },
  { path: '**', redirectTo: '' },
];
