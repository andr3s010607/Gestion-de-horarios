import { Injectable, signal } from '@angular/core';

export interface SolicitudConfirmacion {
  titulo: string;
  mensaje: string;
  textoConfirmar: string;
  peligro: boolean;
  responder: (confirmado: boolean) => void;
}

@Injectable({ providedIn: 'root' })
export class ConfirmacionService {
  readonly solicitud = signal<SolicitudConfirmacion | null>(null);

  confirmar(titulo: string, mensaje: string, textoConfirmar = 'Eliminar', peligro = true): Promise<boolean> {
    return new Promise(resolver => {
      this.solicitud.set({
        titulo,
        mensaje,
        textoConfirmar,
        peligro,
        responder: confirmado => {
          this.solicitud.set(null);
          resolver(confirmado);
        }
      });
    });
  }
}
