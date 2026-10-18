import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { CargaResumen } from '../../services/datos.resolver';

@Component({ selector: 'app-dashboard', imports: [CommonModule, RouterLink], templateUrl: './dashboard.component.html', styleUrl: './dashboard.component.css' })
export class DashboardComponent implements OnInit {
  resumen = [
    { etiqueta: 'Asignaturas', endpoint: 'asignaturas', icono: '◌', valor: '—', ruta: '/academico/asignaturas', tono: 'sage', roles: ['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'] },
    { etiqueta: 'Docentes', endpoint: 'docentes', icono: '♧', valor: '—', ruta: '/personas/docentes', tono: 'clay', roles: ['ADMINISTRADOR', 'PLANIFICADOR'] },
    { etiqueta: 'Aulas', endpoint: 'aulas', icono: '⌑', valor: '—', ruta: '/espacios/aulas', tono: 'sand', roles: ['ADMINISTRADOR', 'PLANIFICADOR'] },
    { etiqueta: 'Mis horarios', endpoint: 'horarios', icono: '▦', valor: '—', ruta: '/horarios', tono: 'ink', roles: ['ADMINISTRADOR', 'PLANIFICADOR', 'ESTUDIANTE'] },
  ];
  constructor(private readonly api: ApiService, private readonly auth: AuthService, private readonly route: ActivatedRoute) {}
  ngOnInit(): void { const carga = this.route.snapshot.data['carga'] as CargaResumen | undefined; if (carga) { this.resumen.forEach((item) => item.valor = String(carga.totales[item.endpoint] ?? '—')); return; } this.resumen.forEach((item) => this.api.contar(item.endpoint).subscribe({ next: (valor) => item.valor = String(valor), error: () => item.valor = '—' })); }
  get resumenVisible() { return this.resumen.filter((item) => this.auth.tieneAlgunoDeLosRoles(...item.roles)); }
  get esEstudiante(): boolean { return this.auth.tieneAlgunoDeLosRoles('ESTUDIANTE'); }
}
