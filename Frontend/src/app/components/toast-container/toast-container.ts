import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { TipoToast, ToastService } from '../../services/toast.service';
import { Icono } from '../icono/icono';

const ICONO_POR_TIPO: Record<TipoToast, string> = {
  exito: 'check',
  error: 'alerta',
  info: 'info',
  advertencia: 'alerta'
};

@Component({
  selector: 'app-toast-container',
  imports: [Icono],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './toast-container.css',
  templateUrl: './toast-container.html'
})
export class ToastContainer {
  protected readonly toast = inject(ToastService);
  protected readonly iconoPorTipo = ICONO_POR_TIPO;
}
