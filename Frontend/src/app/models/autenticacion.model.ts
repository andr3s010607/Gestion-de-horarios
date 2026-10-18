export type RolUsuario = 'ADMINISTRADOR' | 'PLANIFICADOR' | 'CONSULTA';

export interface LoginRequest {
  correo: string;
  contrasena: string;
}

export interface RespuestaAutenticacion {
  token: string;
  tipoToken: string;
  id: number;
  correo: string;
  rol: RolUsuario;
}
