import { Routes } from '@angular/router';
import { MainLayout } from './components/layout/main-layout/main-layout';
import { Login } from './components/login/login';
import { authGuard, invitadoGuard } from './guards/auth-guard';
import { docentePropioGuard, rolGuard } from './guards/rol-guard';

export const routes: Routes = [
  { path: 'login', component: Login, canActivate: [invitadoGuard], title: 'Iniciar sesión · Gestor de horarios' },
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    canActivateChild: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () => import('./components/dashboard/dashboard').then(m => m.Dashboard),
        title: 'Panel · Gestor de horarios'
      },
      {
        path: 'horarios',
        loadComponent: () => import('./components/horarios/horarios').then(m => m.Horarios),
        title: 'Horarios · Gestor de horarios'
      },
      {
        path: 'horarios/:id',
        loadComponent: () => import('./components/horario-detalle/horario-detalle').then(m => m.HorarioDetalle),
        title: 'Horario · Gestor de horarios'
      },
      {
        path: 'consultas',
        loadComponent: () => import('./components/consultas/consultas').then(m => m.Consultas),
        title: 'Consultas · Gestor de horarios'
      },
      {
        path: 'asignaturas',
        loadComponent: () => import('./components/asignaturas/asignaturas').then(m => m.Asignaturas),
        title: 'Asignaturas · Gestor de horarios'
      },
      {
        path: 'aulas',
        loadComponent: () => import('./components/aulas/aulas').then(m => m.Aulas),
        title: 'Aulas · Gestor de horarios'
      },
      {
        path: 'docentes',
        loadComponent: () => import('./components/docentes/docentes').then(m => m.Docentes),
        title: 'Docentes · Gestor de horarios'
      },
      {
        path: 'docentes/:id',
        loadComponent: () => import('./components/docente-detalle/docente-detalle').then(m => m.DocenteDetalle),
        canActivate: [rolGuard, docentePropioGuard],
        data: { roles: ['ADMINISTRADOR', 'DOCENTE'] },
        title: 'Docente · Gestor de horarios'
      },
      {
        path: 'preseleccion',
        loadComponent: () => import('./components/preseleccion/preseleccion').then(m => m.Preseleccion),
        canActivate: [rolGuard],
        data: { roles: ['ESTUDIANTE'] },
        title: 'Preselección · Gestor de horarios'
      },
      {
        path: 'parametros',
        loadComponent: () => import('./components/parametros/parametros').then(m => m.Parametros),
        title: 'Parámetros · Gestor de horarios'
      },
      {
        path: 'usuarios',
        loadComponent: () => import('./components/usuarios/usuarios').then(m => m.Usuarios),
        canActivate: [rolGuard],
        data: { roles: ['ADMINISTRADOR'] },
        title: 'Usuarios · Gestor de horarios'
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
