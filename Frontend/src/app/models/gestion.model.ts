export interface RegistroBase {
  id?: number;
  [clave: string]: unknown;
}

export interface CampoFormulario {
  clave: string;
  etiqueta: string;
  tipo: 'text' | 'number' | 'email' | 'password' | 'time' | 'select' | 'checkbox';
  opciones?: { valor: string; etiqueta: string }[];
  referencia?: ReferenciaCampo;
}

export interface ReferenciaCampo {
  endpoint: string;
  camposEtiqueta: string[];
}

export interface ConfiguracionCatalogo {
  titulo: string;
  descripcion: string;
  endpoint: string;
  icono: string;
  campos: CampoFormulario[];
  columnaPrincipal: string;
  ayuda?: string;
}

export interface Horario extends RegistroBase {
  nombre: string;
  semestreId: number;
  estado: string;
  fechaCreacion?: string;
}

export interface SesionClase extends RegistroBase {
  horarioId: number;
  grupoId: number;
  aulaId: number;
  dia: string;
  horaInicio: string;
  horaFin: string;
}
