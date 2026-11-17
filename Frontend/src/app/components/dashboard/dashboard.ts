import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { CuadriculaHorario, Horario } from '../../models/horario';
import { AuthService } from '../../services/auth.service';
import { AsignaturaService } from '../../services/asignatura.service';
import { AulaService } from '../../services/aula.service';
import { DocenteService } from '../../services/docente.service';
import { HorarioService } from '../../services/horario.service';
import { EstadoVacio } from '../estado-vacio/estado-vacio';
import { Icono } from '../icono/icono';
import { Spinner } from '../spinner/spinner';

interface Indicador {
  etiqueta: string;
  valor: number;
  detalle: string;
  icono: string;
  tono: 'verde' | 'crema' | 'petroleo' | 'tan';
  ruta: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, Icono, Spinner, EstadoVacio],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  private readonly asignaturaService = inject(AsignaturaService);
  private readonly docenteService = inject(DocenteService);
  private readonly aulaService = inject(AulaService);
  private readonly horarioService = inject(HorarioService);
  protected readonly auth = inject(AuthService);

  protected readonly cargando = signal(true);
  protected readonly indicadores = signal<Indicador[]>([]);
  protected readonly horarios = signal<Horario[]>([]);
  protected readonly cuadricula = signal<CuadriculaHorario | null>(null);

  protected readonly horarioReciente = computed(() => {
    const lista = [...this.horarios()].sort((a, b) => b.semestre.localeCompare(a.semestre));
    return lista[0] ?? null;
  });

  protected readonly progreso = computed(() => {
    const cuadricula = this.cuadricula();
    if (!cuadricula) {
      return null;
    }
    const ubicadas = cuadricula.sesionesUbicadas.length;
    const total = ubicadas + cuadricula.sesionesSinUbicar.length;
    return { ubicadas, sinUbicar: cuadricula.sesionesSinUbicar.length, total, porcentaje: total ? Math.round((ubicadas / total) * 100) : 0 };
  });

  ngOnInit(): void {
    forkJoin({
      asignaturas: this.asignaturaService.contar(),
      docentes: this.docenteService.contar(),
      aulas: this.aulaService.contar(),
      horarios: this.horarioService.obtenerTodos()
    }).subscribe({
      next: ({ asignaturas, docentes, aulas, horarios }) => {
        const aprobados = horarios.filter(horario => horario.estado === 'APROBADO').length;
        this.horarios.set(horarios);
        this.indicadores.set([
          { etiqueta: 'Asignaturas', valor: asignaturas, detalle: 'En el catálogo', icono: 'libro', tono: 'verde', ruta: '/asignaturas' },
          { etiqueta: 'Docentes', valor: docentes, detalle: 'Registrados', icono: 'personas', tono: 'crema', ruta: '/docentes' },
          { etiqueta: 'Aulas', valor: aulas, detalle: 'Disponibles', icono: 'edificio', tono: 'petroleo', ruta: '/aulas' },
          { etiqueta: 'Horarios', valor: horarios.length, detalle: `${aprobados} aprobados`, icono: 'calendario', tono: 'tan', ruta: '/horarios' }
        ]);
        this.cargando.set(false);
        this.cargarCuadricula();
      },
      error: () => this.cargando.set(false)
    });
  }

  private cargarCuadricula(): void {
    const reciente = this.horarioReciente();
    if (reciente?.id) {
      this.horarioService.obtenerCuadricula(reciente.id).subscribe(cuadricula => this.cuadricula.set(cuadricula));
    }
  }
}
