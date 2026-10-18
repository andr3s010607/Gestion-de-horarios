import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { CampoFormulario, ConfiguracionCatalogo, ReferenciaCampo, RegistroBase } from '../../models/gestion.model';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { CargaCatalogo } from '../../services/datos.resolver';
import { materiasPensum } from '../../data/plan-estudios-ueb';

const campo = (clave: string, etiqueta: string, tipo: CampoFormulario['tipo'] = 'text', opciones?: CampoFormulario['opciones'], referencia?: ReferenciaCampo): CampoFormulario => ({ clave, etiqueta, tipo, opciones, referencia });
const siNo = [{ valor: 'true', etiqueta: 'Activo' }, { valor: 'false', etiqueta: 'Inactivo' }];
const referencia = (endpoint: string, ...camposEtiqueta: string[]): ReferenciaCampo => ({ endpoint, camposEtiqueta });

export const catalogos: Record<string, ConfiguracionCatalogo> = {
  asignaturas: { titulo: 'Plan de estudios', descripcion: 'Materias que alimentan la programación académica.', endpoint: 'asignaturas', icono: '◌', columnaPrincipal: 'nombre', ayuda: 'Puedes importar la malla 2025 de Ingeniería de Sistemas como punto de partida.', campos: [campo('codigo', 'Código'), campo('nombre', 'Nombre'), campo('sesionesSemanales', 'Sesiones por semana', 'number'), campo('activa', 'Estado', 'select', siNo)] },
  semestres: { titulo: 'Períodos académicos', descripcion: 'Define los períodos sobre los que se construyen los horarios.', endpoint: 'semestres', icono: '◒', columnaPrincipal: 'anio', campos: [campo('anio', 'Año', 'number'), campo('periodo', 'Período', 'number'), campo('activo', 'Estado', 'select', siNo)] },
  grupos: { titulo: 'Grupos y carga', descripcion: 'Relaciona una asignatura, período y docente para programarla.', endpoint: 'grupos', icono: '◇', columnaPrincipal: 'codigo', campos: [campo('codigo', 'Código'), campo('cupoPlaneado', 'Cupo planeado', 'number'), campo('asignaturaId', 'Asignatura', 'select', undefined, referencia('asignaturas', 'codigo', 'nombre')), campo('semestreId', 'Período académico', 'select', undefined, referencia('semestres', 'anio', 'periodo')), campo('docenteId', 'Docente', 'select', undefined, referencia('docentes', 'nombres', 'apellidos', 'correo')), campo('activo', 'Estado', 'select', siNo)] },
  docentes: { titulo: 'Docentes', descripcion: 'Registro de personas que pueden asumir la carga académica.', endpoint: 'docentes', icono: '♧', columnaPrincipal: 'nombres', campos: [campo('nombres', 'Nombres'), campo('apellidos', 'Apellidos'), campo('correo', 'Correo institucional', 'email'), campo('tipoVinculacion', 'Vinculación', 'select', [{ valor: 'TIEMPO_COMPLETO', etiqueta: 'Tiempo completo' }, { valor: 'TRES_CUARTOS', etiqueta: 'Tres cuartos' }, { valor: 'MEDIO_TIEMPO', etiqueta: 'Medio tiempo' }, { valor: 'CUARTO_TIEMPO', etiqueta: 'Cuarto tiempo' }]), campo('activo', 'Estado', 'select', siNo)] },
  aulas: { titulo: 'Aulas y espacios', descripcion: 'Espacios físicos disponibles para las sesiones de clase.', endpoint: 'aulas', icono: '⌑', columnaPrincipal: 'codigo', campos: [campo('codigo', 'Código'), campo('nombre', 'Nombre'), campo('capacidad', 'Capacidad', 'number'), campo('ubicacion', 'Ubicación'), campo('activa', 'Estado', 'select', siNo)] },
  recursos: { titulo: 'Recursos de aula', descripcion: 'Equipos y condiciones que pueden requerir las asignaturas.', endpoint: 'recursos-aula', icono: '✧', columnaPrincipal: 'nombre', campos: [campo('nombre', 'Nombre'), campo('descripcion', 'Descripción')] },
  'docentes-asignaturas': { titulo: 'Carga habilitada', descripcion: 'Materias que cada docente está habilitado para orientar.', endpoint: 'docentes-asignaturas', icono: '↔', columnaPrincipal: 'docenteId', campos: [campo('docenteId', 'Docente', 'select', undefined, referencia('docentes', 'nombres', 'apellidos', 'correo')), campo('asignaturaId', 'Asignatura', 'select', undefined, referencia('asignaturas', 'codigo', 'nombre'))] },
  restricciones: { titulo: 'Restricciones docentes', descripcion: 'Franjas que el motor debe reservar al generar un horario.', endpoint: 'restricciones-horarias-docente', icono: '◷', columnaPrincipal: 'docenteId', campos: [campo('docenteId', 'Docente', 'select', undefined, referencia('docentes', 'nombres', 'apellidos', 'correo')), campo('dia', 'Día', 'select', ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'].map((valor) => ({ valor, etiqueta: valor.charAt(0) + valor.slice(1).toLowerCase() }))), campo('horaInicio', 'Desde', 'time'), campo('horaFin', 'Hasta', 'time'), campo('tipo', 'Tipo', 'select', [{ valor: 'NO_DISPONIBLE', etiqueta: 'No disponible' }, { valor: 'DISPONIBLE', etiqueta: 'Disponible' }])] },
  requisitos: { titulo: 'Requisitos por materia', descripcion: 'Recursos indispensables para poder ubicar una asignatura.', endpoint: 'asignaturas-recursos-requeridos', icono: '⊹', columnaPrincipal: 'asignaturaId', campos: [campo('asignaturaId', 'Asignatura', 'select', undefined, referencia('asignaturas', 'codigo', 'nombre')), campo('recursoId', 'Recurso', 'select', undefined, referencia('recursos-aula', 'nombre'))] },
  'aulas-recursos': { titulo: 'Inventario de aulas', descripcion: 'Recursos disponibles en cada aula.', endpoint: 'aulas-recursos', icono: '⊹', columnaPrincipal: 'aulaId', campos: [campo('aulaId', 'Aula', 'select', undefined, referencia('aulas', 'codigo', 'nombre')), campo('recursoId', 'Recurso', 'select', undefined, referencia('recursos-aula', 'nombre'))] },
  parametros: { titulo: 'Parámetros del sistema', descripcion: 'Reglas de operación utilizadas por el motor de horarios.', endpoint: 'parametros-sistema', icono: '⚙', columnaPrincipal: 'clave', campos: [campo('clave', 'Clave'), campo('valor', 'Valor'), campo('tipoDato', 'Tipo de dato', 'select', [{ valor: 'ENTERO', etiqueta: 'Entero' }, { valor: 'TEXTO', etiqueta: 'Texto' }, { valor: 'HORA', etiqueta: 'Hora' }, { valor: 'DECIMAL', etiqueta: 'Decimal' }]), campo('descripcion', 'Descripción'), campo('activo', 'Estado', 'select', siNo)] },
  usuarios: { titulo: 'Usuarios', descripcion: 'Accesos internos y responsabilidades dentro del sistema.', endpoint: 'usuarios', icono: '♙', columnaPrincipal: 'nombreCompleto', campos: [campo('nombreCompleto', 'Nombre completo'), campo('correo', 'Correo', 'email'), campo('contrasena', 'Contraseña', 'password'), campo('rol', 'Rol', 'select', [{ valor: 'ADMINISTRADOR', etiqueta: 'Administrador' }, { valor: 'PLANIFICADOR', etiqueta: 'Planificador' }, { valor: 'ESTUDIANTE', etiqueta: 'Estudiante' }]), campo('activo', 'Estado', 'select', siNo)] },
};

@Component({ selector: 'app-catalogo', imports: [CommonModule, FormsModule, RouterLink], templateUrl: './catalogo.component.html', styleUrl: './catalogo.component.css' })
export class CatalogoComponent implements OnInit {
  configuracion!: ConfiguracionCatalogo;
  registros: RegistroBase[] = [];
  borrador: RegistroBase = {};
  editando?: RegistroBase;
  cargando = true;
  guardando = false;
  mensaje = '';
  error = '';
  soloLectura = false;
  referencias: Record<string, RegistroBase[]> = {};
  asignaturasDocente: RegistroBase[] = [];
  relacionesDocente: RegistroBase[] = [];
  materiasSeleccionadas = new Set<number>();
  cargandoMateriasDocente = false;
  readonly materiasPensum = materiasPensum;

  constructor(private readonly route: ActivatedRoute, private readonly api: ApiService, private readonly toast: ToastService, private readonly auth: AuthService) {}

  ngOnInit(): void {
    const clave = this.route.snapshot.data['catalogo'] as string;
    this.configuracion = catalogos[clave];
    this.soloLectura = clave === 'asignaturas' && !this.auth.tieneAlgunoDeLosRoles('ADMINISTRADOR', 'PLANIFICADOR');
    this.nuevo();
    this.aplicarCargaInicial(this.route.snapshot.data['carga'] as CargaCatalogo | undefined);
  }

  cargar(): void { const dependencias = (this.route.snapshot.data['dependencias'] as string[] | undefined) ?? [...new Set(this.configuracion.campos.map((campo) => campo.referencia?.endpoint).filter((endpoint): endpoint is string => !!endpoint))]; this.cargando = true; this.error = ''; forkJoin({ registros: this.api.listar<RegistroBase>(this.configuracion.endpoint), dependencias: forkJoin(dependencias.map((endpoint) => this.api.listar<RegistroBase>(endpoint))) }).subscribe({ next: (datos) => { this.registros = datos.registros; dependencias.forEach((endpoint, indice) => this.referencias[endpoint] = datos.dependencias[indice]); this.asignaturasDocente = this.referencias['asignaturas'] ?? this.asignaturasDocente; this.relacionesDocente = this.referencias['docentes-asignaturas'] ?? this.relacionesDocente; this.cargando = false; }, error: () => { this.error = 'No fue posible consultar esta información. Verifica que el backend esté activo.'; this.cargando = false; } }); }
  nuevo(): void { this.editando = undefined; this.materiasSeleccionadas = new Set<number>(); this.borrador = this.configuracion?.campos?.reduce((valor, item) => ({ ...valor, [item.clave]: item.tipo === 'select' && item.opciones?.[0]?.valor === 'true' ? 'true' : '' }), {} as RegistroBase) ?? {}; this.mensaje = ''; this.error = ''; }
  guardar(): void {
    if (this.soloLectura) return;
    this.guardando = true; this.mensaje = ''; this.error = '';
    const datos = this.normalizar(this.borrador);
    const solicitud = this.editando?.id ? this.api.actualizar(this.configuracion.endpoint, this.editando.id, datos) : this.api.crear(this.configuracion.endpoint, datos);
    solicitud.subscribe({ next: (respuesta) => { this.guardando = false; this.mensaje = respuesta; this.toast.exito('Cambios guardados', respuesta); if (this.esDocentes && !this.editando) { this.abrirAsignacionDocente(datos, respuesta); return; } this.nuevo(); this.cargar(); }, error: (respuesta) => { this.guardando = false; this.error = respuesta.error || 'No fue posible guardar el registro. Revisa los datos e inténtalo de nuevo.'; this.toast.error('No se guardó el registro', this.error); } });
  }
  editar(registro: RegistroBase): void { if (this.soloLectura) return; this.editando = registro; this.borrador = { ...registro }; this.configuracion.campos.forEach((item) => { if (item.tipo === 'select' && typeof this.borrador[item.clave] === 'boolean') this.borrador[item.clave] = String(this.borrador[item.clave]); }); if (this.esDocentes && registro.id) this.prepararMateriasDocente(registro.id); window.scrollTo({ top: 0, behavior: 'smooth' }); }
  eliminar(registro: RegistroBase): void { if (this.soloLectura || !registro.id || !confirm('¿Eliminar este registro?')) return; this.api.eliminar(this.configuracion.endpoint, registro.id).subscribe({ next: (respuesta) => { this.mensaje = respuesta; this.toast.exito('Registro eliminado', respuesta); this.cargar(); }, error: (respuesta) => { this.error = respuesta.error || 'No fue posible eliminar el registro.'; this.toast.error('No se eliminó el registro', this.error); } }); }
  importarPensum(): void {
    if (this.soloLectura) return;
    const existentes = new Set(this.registros.map((r) => String(r['codigo']))); const faltantes = this.materiasPensum.filter((m) => !existentes.has(m.codigo));
    if (!faltantes.length) { this.mensaje = 'Las materias del pensum ya están cargadas.'; this.toast.informacion('Pensum disponible', this.mensaje); return; }
    this.guardando = true; let completadas = 0; let fallidas = 0;
    const terminarImportacion = (fallo = false): void => { if (fallo) fallidas++; completadas++; if (completadas === faltantes.length) { this.guardando = false; this.mensaje = `${faltantes.length - fallidas} materias del pensum quedaron disponibles.`; this.toast.exito('Importación terminada', this.mensaje); if (fallidas) { this.error = `${fallidas} materias no se pudieron cargar; probablemente ya existen con otro código.`; this.toast.advertencia('Importación parcial', this.error); } this.cargar(); } };
    faltantes.forEach((materia) => this.api.crear('asignaturas', { codigo: materia.codigo, nombre: materia.nombre, sesionesSemanales: 2, activa: true }).subscribe({ next: () => terminarImportacion(), error: () => terminarImportacion(true) }));
  }
  opciones(campo: CampoFormulario): RegistroBase[] { const opciones = campo.referencia ? this.referencias[campo.referencia.endpoint] ?? [] : []; if (!this.esGrupos || campo.clave !== 'docenteId') return opciones; const asignaturaId = Number(this.borrador['asignaturaId']); if (!asignaturaId) return []; const docentesHabilitados = new Set(this.referencias['docentes-asignaturas']?.filter((relacion) => Number(relacion['asignaturaId']) === asignaturaId).map((relacion) => Number(relacion['docenteId'])) ?? []); return opciones.filter((docente) => docentesHabilitados.has(Number(docente.id))); }
  alCambiarCampo(campo: CampoFormulario, valor: unknown): void { if (!this.esGrupos || campo.clave !== 'asignaturaId') return; const docentesHabilitados = new Set(this.referencias['docentes-asignaturas']?.filter((relacion) => Number(relacion['asignaturaId']) === Number(valor)).map((relacion) => Number(relacion['docenteId'])) ?? []); if (!docentesHabilitados.has(Number(this.borrador['docenteId']))) this.borrador['docenteId'] = ''; }
  etiquetaReferencia(campo: CampoFormulario, registro: RegistroBase): string { return campo.referencia?.camposEtiqueta.map((clave) => String(registro[clave] ?? '')).filter(Boolean).join(' · ') ?? ''; }
  valor(registro: RegistroBase, campo: CampoFormulario): string { const valor = registro[campo.clave]; if (campo.referencia) { const opcion = this.opciones(campo).find((item) => item.id === Number(valor)); return opcion ? this.etiquetaReferencia(campo, opcion) : '—'; } return typeof valor === 'boolean' ? (valor ? 'Activo' : 'Inactivo') : String(valor ?? '—'); }
  get esDocentes(): boolean { return this.configuracion.endpoint === 'docentes'; }
  get esGrupos(): boolean { return this.configuracion.endpoint === 'grupos'; }
  materiaSeleccionada(id: number): boolean { return this.materiasSeleccionadas.has(id); }
  alternarMateria(id: number, seleccionada: boolean): void { if (seleccionada) this.materiasSeleccionadas.add(id); else this.materiasSeleccionadas.delete(id); }
  etiquetaMateria(materia: RegistroBase): string { return `${materia['codigo']} · ${materia['nombre']}`; }
  guardarMateriasDocente(): void {
    if (!this.editando?.id) return;
    const docenteId = this.editando.id;
    const actuales = this.relacionesDocente.filter((relacion) => Number(relacion['docenteId']) === docenteId);
    const asignaturasActuales = new Set(actuales.map((relacion) => Number(relacion['asignaturaId'])));
    const crear = [...this.materiasSeleccionadas].filter((asignaturaId) => !asignaturasActuales.has(asignaturaId));
    const eliminar = actuales.filter((relacion) => !this.materiasSeleccionadas.has(Number(relacion['asignaturaId'])));
    if (!crear.length && !eliminar.length) { this.toast.informacion('Sin cambios', 'Las materias habilitadas ya están actualizadas.'); return; }
    this.guardando = true;
    forkJoin([...crear.map((asignaturaId) => this.api.crear('docentes-asignaturas', { docenteId, asignaturaId })), ...eliminar.map((relacion) => this.api.eliminar('docentes-asignaturas', Number(relacion.id)))]).subscribe({ next: () => { this.mensaje = 'Materias habilitadas actualizadas correctamente.'; this.toast.exito('Carga docente actualizada', this.mensaje); this.actualizarRelacionesDocente(docenteId); }, error: () => { this.guardando = false; this.error = 'No se pudieron actualizar todas las materias del docente.'; this.toast.error('No se actualizó la carga docente', this.error); } });
  }
  private abrirAsignacionDocente(datos: RegistroBase, respuesta: string): void { const correo = String(datos['correo'] ?? ''); this.api.obtener<RegistroBase>(`docentes/correo/${encodeURIComponent(correo)}`).subscribe({ next: (docente) => { this.editando = docente; this.borrador = { ...docente }; this.mensaje = `${respuesta}. Ahora selecciona las materias que puede orientar.`; this.prepararMateriasDocente(docente.id!); this.cargar(); }, error: () => { this.nuevo(); this.cargar(); } }); }
  private prepararMateriasDocente(docenteId: number): void { this.materiasSeleccionadas = new Set(this.relacionesDocente.filter((relacion) => Number(relacion['docenteId']) === docenteId).map((relacion) => Number(relacion['asignaturaId']))); }
  private actualizarRelacionesDocente(docenteId: number): void { this.api.listar<RegistroBase>('docentes-asignaturas').subscribe({ next: (relaciones) => { this.relacionesDocente = relaciones; this.referencias['docentes-asignaturas'] = relaciones; this.prepararMateriasDocente(docenteId); this.guardando = false; }, error: () => { this.guardando = false; this.error = 'Las materias se guardaron, pero no se pudo actualizar la vista.'; this.toast.advertencia('Actualización pendiente', this.error); } }); }
  private aplicarCargaInicial(carga?: CargaCatalogo): void { if (!carga) { this.cargar(); return; } this.registros = carga.registros; this.referencias = carga.dependencias; this.asignaturasDocente = carga.dependencias['asignaturas'] ?? []; this.relacionesDocente = carga.dependencias['docentes-asignaturas'] ?? []; this.error = carga.error ?? ''; this.cargando = false; }
  private normalizar(origen: RegistroBase): RegistroBase { const datos: RegistroBase = {}; this.configuracion.campos.forEach((campo) => { const valor = origen[campo.clave]; datos[campo.clave] = campo.tipo === 'number' || campo.referencia ? Number(valor) : campo.tipo === 'select' && (valor === 'true' || valor === 'false') ? valor === 'true' : valor; }); return datos; }
}
