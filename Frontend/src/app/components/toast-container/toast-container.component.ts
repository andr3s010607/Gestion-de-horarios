import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';

import { MensajeToast, ToastService } from '../../services/toast.service';

@Component({ selector: 'app-toast-container', imports: [AsyncPipe], templateUrl: './toast-container.component.html', styleUrl: './toast-container.component.css' })
export class ToastContainerComponent {
  readonly mensajes$;
  constructor(private readonly toast: ToastService) { this.mensajes$ = this.toast.mensajes$; }
  cerrar(mensaje: MensajeToast): void { this.toast.cerrar(mensaje.id); }
  simbolo(tipo: MensajeToast['tipo']): string { return ({ exito: '✓', error: '!', informacion: 'i', advertencia: '!' })[tipo]; }
}
