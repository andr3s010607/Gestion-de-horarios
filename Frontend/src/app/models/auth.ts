import { RolUsuario } from './enums';

export interface Login {
  nombreUsuario: string;
  contrasena: string;
}

export interface RespuestaAutenticacion {
  token: string;
  tipoToken: string;
  id: number;
  nombreUsuario: string;
  rol: RolUsuario;
  docenteId: number | null;
}
