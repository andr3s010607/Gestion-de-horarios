import { Injectable, signal } from '@angular/core';

export type TipoToast = 'exito' | 'error' | 'info' | 'advertencia';

export interface MensajeToast {
  id: number;
  tipo: TipoToast;
  titulo: string;
  mensaje?: string;
  detalles?: string[];
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private siguienteId = 1;
  readonly mensajes = signal<MensajeToast[]>([]);

  exito(titulo: string, mensaje?: string): void {
    this.mostrar('exito', titulo, mensaje);
  }

  error(titulo: string, mensaje?: string, detalles?: string[]): void {
    this.mostrar('error', titulo, mensaje, detalles, 8000);
  }

  info(titulo: string, mensaje?: string): void {
    this.mostrar('info', titulo, mensaje);
  }

  advertencia(titulo: string, mensaje?: string, detalles?: string[]): void {
    this.mostrar('advertencia', titulo, mensaje, detalles, 8000);
  }

  cerrar(id: number): void {
    this.mensajes.update(lista => lista.filter(mensaje => mensaje.id !== id));
  }

  private mostrar(tipo: TipoToast, titulo: string, mensaje?: string, detalles?: string[], duracion = 4500): void {
    const id = this.siguienteId++;
    this.mensajes.update(lista => [...lista, { id, tipo, titulo, mensaje, detalles }]);
    setTimeout(() => this.cerrar(id), duracion);
  }
}
