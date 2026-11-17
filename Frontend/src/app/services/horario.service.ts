import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CuadriculaHorario, Horario, ResultadoGeneracionHorario } from '../models/horario';
import { Incidencia } from '../models/incidencia';
import { DemandaGrupo } from '../models/preseleccion';
import { SesionClase } from '../models/sesion';
import { conManejoDeErrorPropio } from './contexto-http';
import { CrudService } from './crud.service';

export type CriterioConsulta = 'docente' | 'aula' | 'asignatura';

@Injectable({ providedIn: 'root' })
export class HorarioService extends CrudService<Horario> {
  protected readonly recurso = 'horarios';

  generar(id: number): Observable<ResultadoGeneracionHorario> {
    return this.http.post<ResultadoGeneracionHorario>(`${this.url}/${id}/generar`, null);
  }

  validar(id: number): Observable<Incidencia[]> {
    return this.http.get<Incidencia[]>(`${this.url}/${id}/validar`);
  }

  obtenerCuadricula(id: number): Observable<CuadriculaHorario> {
    return this.http.get<CuadriculaHorario>(`${this.url}/${id}/cuadricula`);
  }

  obtenerSesiones(id: number): Observable<SesionClase[]> {
    return this.http.get<SesionClase[]>(`${this.url}/${id}/sesiones`);
  }

  aprobar(id: number): Observable<Horario> {
    return this.http.post<Horario>(`${this.url}/${id}/aprobar`, null, conManejoDeErrorPropio());
  }

  abrirPreseleccion(id: number): Observable<Horario> {
    return this.http.post<Horario>(`${this.url}/${id}/preseleccion/abrir`, null);
  }

  cerrarPreseleccion(id: number): Observable<Horario> {
    return this.http.post<Horario>(`${this.url}/${id}/preseleccion/cerrar`, null);
  }

  demanda(id: number): Observable<DemandaGrupo[]> {
    return this.http.get<DemandaGrupo[]>(`${this.url}/${id}/demanda`);
  }

  devolverABorrador(id: number): Observable<Horario> {
    return this.http.post<Horario>(`${this.url}/${id}/devolver-a-borrador`, null);
  }

  consultar(id: number, criterio: CriterioConsulta, valorId: number): Observable<SesionClase[]> {
    return this.http.get<SesionClase[]>(`${this.url}/${id}/${criterio}/${valorId}`);
  }

  exportar(id: number, criterio?: CriterioConsulta, valorId?: number): Observable<HttpResponse<Blob>> {
    const ruta = criterio && valorId
      ? `${this.url}/${id}/${criterio}/${valorId}/exportar`
      : `${this.url}/${id}/exportar`;
    return this.http.get(ruta, { responseType: 'blob', observe: 'response' });
  }
}
