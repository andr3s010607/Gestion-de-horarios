import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type TipoToast = 'exito' | 'error' | 'informacion' | 'advertencia';

export interface MensajeToast {
  id: number;
  tipo: TipoToast;
  titulo: string;
  detalle?: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private readonly mensajesSubject = new BehaviorSubject<MensajeToast[]>([]);
  readonly mensajes$ = this.mensajesSubject.asObservable();
  private siguienteId = 1;

  exito(titulo: string, detalle?: string): void { this.mostrar('exito', titulo, detalle); }
  error(titulo: string, detalle?: string): void { this.mostrar('error', titulo, detalle); }
  informacion(titulo: string, detalle?: string): void { this.mostrar('informacion', titulo, detalle); }
  advertencia(titulo: string, detalle?: string): void { this.mostrar('advertencia', titulo, detalle); }

  cerrar(id: number): void {
    this.mensajesSubject.next(this.mensajesSubject.value.filter((mensaje) => mensaje.id !== id));
  }

  private mostrar(tipo: TipoToast, titulo: string, detalle?: string): void {
    const mensaje = { id: this.siguienteId++, tipo, titulo, detalle };
    this.mensajesSubject.next([mensaje, ...this.mensajesSubject.value].slice(0, 4));
    window.setTimeout(() => this.cerrar(mensaje.id), 4600);
  }
}
