import { RolUsuario } from './enums';

export interface Usuario {
  id?: number;
  nombreUsuario: string;
  contrasena?: string | null;
  rol: RolUsuario;
  docenteId: number | null;
}
