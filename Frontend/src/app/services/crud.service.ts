import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';

export abstract class CrudService<T> {
  protected readonly http = inject(HttpClient);
  protected abstract readonly recurso: string;

  protected get url(): string {
    return `${environment.apiUrl}/${this.recurso}`;
  }

  obtenerTodos(): Observable<T[]> {
    return this.http.get<T[]>(this.url);
  }

  obtenerPorId(id: number): Observable<T> {
    return this.http.get<T>(`${this.url}/${id}`);
  }

  crear(entidad: T): Observable<string> {
    return this.http.post(this.url, entidad, { responseType: 'text' });
  }

  actualizar(id: number, entidad: T): Observable<string> {
    return this.http.put(`${this.url}/${id}`, entidad, { responseType: 'text' });
  }

  eliminar(id: number): Observable<string> {
    return this.http.delete(`${this.url}/${id}`, { responseType: 'text' });
  }

  contar(): Observable<number> {
    return this.http.get<number>(`${this.url}/count`);
  }
}
