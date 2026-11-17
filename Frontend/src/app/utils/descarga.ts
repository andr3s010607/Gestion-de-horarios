import { HttpResponse } from '@angular/common/http';

export function descargarArchivo(respuesta: HttpResponse<Blob>, nombrePorDefecto: string): void {
  const encabezado = respuesta.headers.get('Content-Disposition') ?? '';
  const coincidencia = /filename\*?=(?:UTF-8'')?"?([^";]+)"?/i.exec(encabezado);
  const nombre = coincidencia ? decodeURIComponent(coincidencia[1]) : nombrePorDefecto;
  const url = URL.createObjectURL(respuesta.body ?? new Blob());
  const enlace = document.createElement('a');
  enlace.href = url;
  enlace.download = nombre;
  enlace.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}
