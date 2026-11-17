import { HttpContext, HttpContextToken } from '@angular/common/http';

export const MANEJO_ERROR_PROPIO = new HttpContextToken<boolean>(() => false);

export function conManejoDeErrorPropio(): { context: HttpContext } {
  return { context: new HttpContext().set(MANEJO_ERROR_PROPIO, true) };
}
