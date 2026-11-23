import { DIAS_SEMANA, DiaSemana } from '../models/enums';
import { SesionClase } from '../models/sesion';

export interface OpcionesPdfHorario {
  titulo: string;
  subtitulo: string;
  sesiones: SesionClase[];
  nombreArchivo: string;
}

type Rgb = [number, number, number];

const ANCHO = 842;
const ALTO = 595;
const MARGEN = 36;
const HORA_INICIO = 7;
const HORA_FIN = 22;

const COLOR_TEXTO: Rgb = hex('#243028');
const COLOR_SUAVE: Rgb = hex('#647066');
const COLOR_LIENZO: Rgb = hex('#F8F6F0');
const COLOR_COLUMNA: Rgb = hex('#EFECE4');
const COLOR_LINEA: Rgb = hex('#E2DDD2');
const COLOR_BOSQUE: Rgb = hex('#2F4536');
const COLOR_CLARO: Rgb = hex('#F5F2EB');
const TONOS: Rgb[] = ['#2F4536', '#4E6B52', '#5F7D7A', '#7C6A58', '#B0695D', '#3A5240', '#6E8768'].map(hex);

interface Bloque {
  sesion: SesionClase;
  inicio: number;
  fin: number;
  carril: number;
  carriles: number;
}

export function descargarHorarioPdf(opciones: OpcionesPdfHorario): void {
  const sesiones = opciones.sesiones.filter(sesion => sesion.ubicada && sesion.dia && sesion.horaInicio && sesion.horaFin);
  const paginas = [paginaCuadricula(opciones, sesiones), ...paginasDetalle(opciones, sesiones)];
  const bytes = construirPdf(paginas.map((contenido, indice) => contenido + pie(indice + 1, paginas.length)));
  const url = URL.createObjectURL(new Blob([bytes], { type: 'application/pdf' }));
  const enlace = document.createElement('a');
  enlace.href = url;
  enlace.download = opciones.nombreArchivo.endsWith('.pdf') ? opciones.nombreArchivo : `${opciones.nombreArchivo}.pdf`;
  enlace.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

function paginaCuadricula(opciones: OpcionesPdfHorario, sesiones: SesionClase[]): string {
  const p = new Lienzo();
  p.rect(0, 0, ANCHO, ALTO, COLOR_LIENZO);
  encabezado(p, opciones, `${sesiones.length} ${sesiones.length === 1 ? 'sesión ubicada' : 'sesiones ubicadas'}`);

  const arriba = 118;
  const abajo = ALTO - 44;
  const anchoHoras = 40;
  const izquierda = MARGEN + anchoHoras;
  const separacion = 6;
  const dias = DIAS_SEMANA;
  const anchoColumna = (ANCHO - MARGEN - izquierda - separacion * (dias.length - 1)) / dias.length;
  const altoCabecera = 22;
  const topeGrilla = arriba + altoCabecera;
  const altoHora = (abajo - topeGrilla) / (HORA_FIN - HORA_INICIO);
  const yDe = (minutos: number) => topeGrilla + ((minutos - HORA_INICIO * 60) / 60) * altoHora;

  dias.forEach((dia, i) => {
    const x = izquierda + i * (anchoColumna + separacion);
    p.texto(dia.corta, x + anchoColumna / 2, arriba + 14, 9.5, 'F2', COLOR_SUAVE, 'centro');
    p.rectRedondeado(x, topeGrilla, anchoColumna, abajo - topeGrilla, 6, COLOR_COLUMNA);
  });

  for (let hora = HORA_INICIO; hora <= HORA_FIN; hora++) {
    const y = yDe(hora * 60);
    p.texto(`${String(hora).padStart(2, '0')}:00`, MARGEN, y + 3, 8, 'F1', COLOR_SUAVE);
    if (hora > HORA_INICIO && hora < HORA_FIN) {
      dias.forEach((_, i) => {
        const x = izquierda + i * (anchoColumna + separacion);
        p.linea(x + 3, y, x + anchoColumna - 3, y, COLOR_LINEA, 0.6);
      });
    }
  }

  const tonos = new Map<number, Rgb>();
  dias.forEach((dia, i) => {
    const x = izquierda + i * (anchoColumna + separacion);
    for (const bloque of carrilesDelDia(sesiones, dia.valor)) {
      const ancho = (anchoColumna - 4) / bloque.carriles;
      const bx = x + 2 + bloque.carril * ancho;
      const by = yDe(Math.max(bloque.inicio, HORA_INICIO * 60)) + 1.5;
      const alto = yDe(Math.min(bloque.fin, HORA_FIN * 60)) - by - 1.5;
      if (!tonos.has(bloque.sesion.asignaturaId)) {
        tonos.set(bloque.sesion.asignaturaId, TONOS[tonos.size % TONOS.length]);
      }
      p.rectRedondeado(bx + 1, by, ancho - 2, alto, 5, tonos.get(bloque.sesion.asignaturaId)!);
      textoBloque(p, bloque.sesion, bx + 1, by, ancho - 2, alto);
    }
  });

  if (sesiones.length === 0) {
    p.texto('No hay sesiones ubicadas para mostrar.', ANCHO / 2, (topeGrilla + abajo) / 2, 12, 'F1', COLOR_SUAVE, 'centro');
  }
  return p.contenido;
}

function textoBloque(p: Lienzo, sesion: SesionClase, x: number, y: number, ancho: number, alto: number): void {
  const centro = x + ancho / 2;
  const disponible = ancho - 6;
  const tamano = Math.max(5, Math.min(9, disponible / 4.2));
  const lineas = [
    { texto: sesion.codigoAsignatura, tamano, fuente: 'F2' as const },
    { texto: `G${sesion.numeroGrupo} · ${sesion.codigoAula ?? ''}`, tamano: tamano * 0.82, fuente: 'F1' as const },
    { texto: `${hora(sesion.horaInicio)}–${hora(sesion.horaFin)}`, tamano: tamano * 0.78, fuente: 'F1' as const }
  ];
  const caben = lineas.filter((_, i) => (i + 1) * tamano * 1.25 + 4 <= alto);
  const altoTexto = caben.reduce((total, linea) => total + linea.tamano * 1.25, 0);
  let cursor = y + (alto - altoTexto) / 2 + caben[0]?.tamano;
  for (const linea of caben) {
    p.texto(recortar(linea.texto, disponible, linea.tamano, linea.fuente), centro, cursor, linea.tamano, linea.fuente, COLOR_CLARO, 'centro');
    cursor += linea.tamano * 1.25;
  }
}

function paginasDetalle(opciones: OpcionesPdfHorario, sesiones: SesionClase[]): string[] {
  if (sesiones.length === 0) {
    return [];
  }
  const orden = DIAS_SEMANA.map(dia => dia.valor);
  const filas = [...sesiones].sort((a, b) =>
    orden.indexOf(a.dia!) - orden.indexOf(b.dia!) || minutos(a.horaInicio!) - minutos(b.horaInicio!) || a.codigoAsignatura.localeCompare(b.codigoAsignatura));
  const columnas = [
    { titulo: 'Día', ancho: 78 },
    { titulo: 'Horario', ancho: 90 },
    { titulo: 'Código', ancho: 72 },
    { titulo: 'Asignatura', ancho: 222 },
    { titulo: 'Grupo', ancho: 52 },
    { titulo: 'Docente', ancho: 176 },
    { titulo: 'Aula', ancho: 80 }
  ];
  const altoFila = 22;
  const inicioTabla = 128;
  const porPagina = Math.floor((ALTO - 50 - inicioTabla) / altoFila);
  const paginas: string[] = [];
  for (let desde = 0; desde < filas.length; desde += porPagina) {
    const p = new Lienzo();
    p.rect(0, 0, ANCHO, ALTO, COLOR_LIENZO);
    encabezado(p, opciones, 'Detalle de las sesiones');
    let x = MARGEN;
    p.rectRedondeado(MARGEN, inicioTabla - 18, ANCHO - 2 * MARGEN, altoFila, 6, COLOR_BOSQUE);
    for (const columna of columnas) {
      p.texto(columna.titulo.toUpperCase(), x + 10, inicioTabla - 3.5, 7.5, 'F2', COLOR_CLARO);
      x += columna.ancho;
    }
    filas.slice(desde, desde + porPagina).forEach((sesion, i) => {
      const y = inicioTabla + 4 + i * altoFila;
      if (i % 2 === 1) {
        p.rect(MARGEN, y, ANCHO - 2 * MARGEN, altoFila, COLOR_COLUMNA);
      }
      p.linea(MARGEN, y + altoFila, ANCHO - MARGEN, y + altoFila, COLOR_LINEA, 0.5);
      const valores = [
        DIAS_SEMANA.find(dia => dia.valor === sesion.dia)?.etiqueta ?? '',
        `${hora(sesion.horaInicio)}–${hora(sesion.horaFin)}`,
        sesion.codigoAsignatura,
        sesion.nombreAsignatura,
        `G${sesion.numeroGrupo}`,
        sesion.nombreDocente ?? 'Sin docente',
        sesion.codigoAula ?? ''
      ];
      let cx = MARGEN;
      valores.forEach((valor, c) => {
        const fuente = c === 2 ? 'F2' : 'F1';
        p.texto(recortar(valor, columnas[c].ancho - 16, 8.5, fuente), cx + 10, y + 14.5, 8.5, fuente, COLOR_TEXTO);
        cx += columnas[c].ancho;
      });
    });
    paginas.push(p.contenido);
  }
  return paginas;
}

function encabezado(p: Lienzo, opciones: OpcionesPdfHorario, resumen: string): void {
  p.rectRedondeado(MARGEN, 30, 30, 30, 9, COLOR_BOSQUE);
  p.texto('H', MARGEN + 15, 50.5, 14, 'F3', COLOR_CLARO, 'centro');
  p.texto(opciones.titulo, MARGEN + 42, 48, 20, 'F3', COLOR_TEXTO);
  p.texto(opciones.subtitulo, MARGEN + 42, 64, 9.5, 'F1', COLOR_SUAVE);
  p.texto(resumen, ANCHO - MARGEN, 48, 9.5, 'F2', COLOR_BOSQUE, 'derecha');
  p.texto(`Generado el ${new Date().toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' })}`, ANCHO - MARGEN, 64, 8.5, 'F1', COLOR_SUAVE, 'derecha');
  p.linea(MARGEN, 84, ANCHO - MARGEN, 84, COLOR_LINEA, 0.8);
}

function pie(numero: number, total: number): string {
  const p = new Lienzo();
  p.texto('Gestor de horarios', MARGEN, ALTO - 18, 8, 'F1', COLOR_SUAVE);
  p.texto(`Página ${numero} de ${total}`, ANCHO - MARGEN, ALTO - 18, 8, 'F1', COLOR_SUAVE, 'derecha');
  return p.contenido;
}

function carrilesDelDia(sesiones: SesionClase[], dia: DiaSemana): Bloque[] {
  const bloques: Bloque[] = sesiones
    .filter(sesion => sesion.dia === dia)
    .map(sesion => ({ sesion, inicio: minutos(sesion.horaInicio!), fin: minutos(sesion.horaFin!), carril: 0, carriles: 1 }))
    .sort((a, b) => a.inicio - b.inicio || a.fin - b.fin);
  let grupo: Bloque[] = [];
  let finGrupo = -1;
  const cerrar = () => {
    const carriles = Math.max(1, ...grupo.map(bloque => bloque.carril + 1));
    grupo.forEach(bloque => (bloque.carriles = carriles));
  };
  for (const bloque of bloques) {
    if (bloque.inicio >= finGrupo && grupo.length) {
      cerrar();
      grupo = [];
    }
    const ocupados = grupo.filter(otro => otro.fin > bloque.inicio).map(otro => otro.carril);
    let carril = 0;
    while (ocupados.includes(carril)) {
      carril++;
    }
    bloque.carril = carril;
    grupo.push(bloque);
    finGrupo = Math.max(finGrupo, bloque.fin);
  }
  if (grupo.length) {
    cerrar();
  }
  return bloques;
}

class Lienzo {
  contenido = '';

  rect(x: number, y: number, ancho: number, alto: number, color: Rgb): void {
    this.contenido += `${rgb(color)} rg ${n(x)} ${n(ALTO - y - alto)} ${n(ancho)} ${n(alto)} re f\n`;
  }

  rectRedondeado(x: number, y: number, ancho: number, alto: number, radio: number, color: Rgb): void {
    const r = Math.min(radio, ancho / 2, alto / 2);
    const k = r * 0.5523;
    const x0 = x, x1 = x + ancho, y0 = ALTO - y - alto, y1 = ALTO - y;
    this.contenido += `${rgb(color)} rg ${n(x0 + r)} ${n(y0)} m ${n(x1 - r)} ${n(y0)} l `
      + `${n(x1 - r + k)} ${n(y0)} ${n(x1)} ${n(y0 + r - k)} ${n(x1)} ${n(y0 + r)} c ${n(x1)} ${n(y1 - r)} l `
      + `${n(x1)} ${n(y1 - r + k)} ${n(x1 - r + k)} ${n(y1)} ${n(x1 - r)} ${n(y1)} c ${n(x0 + r)} ${n(y1)} l `
      + `${n(x0 + r - k)} ${n(y1)} ${n(x0)} ${n(y1 - r + k)} ${n(x0)} ${n(y1 - r)} c ${n(x0)} ${n(y0 + r)} l `
      + `${n(x0)} ${n(y0 + r - k)} ${n(x0 + r - k)} ${n(y0)} ${n(x0 + r)} ${n(y0)} c f\n`;
  }

  linea(x1: number, y1: number, x2: number, y2: number, color: Rgb, grosor: number): void {
    this.contenido += `${rgb(color)} RG ${n(grosor)} w ${n(x1)} ${n(ALTO - y1)} m ${n(x2)} ${n(ALTO - y2)} l S\n`;
  }

  texto(valor: string, x: number, y: number, tamano: number, fuente: 'F1' | 'F2' | 'F3', color: Rgb, alineacion: 'izquierda' | 'centro' | 'derecha' = 'izquierda'): void {
    const ancho = medir(valor, tamano, fuente);
    const inicio = alineacion === 'centro' ? x - ancho / 2 : alineacion === 'derecha' ? x - ancho : x;
    this.contenido += `BT ${rgb(color)} rg /${fuente} ${n(tamano)} Tf ${n(inicio)} ${n(ALTO - y)} Td (${escapar(valor)}) Tj ET\n`;
  }
}

function construirPdf(paginas: string[]): Uint8Array<ArrayBuffer> {
  const objetos: string[] = [];
  const agregar = (cuerpo: string) => objetos.push(cuerpo);
  agregar('<< /Type /Catalog /Pages 2 0 R >>');
  agregar('');
  agregar('<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>');
  agregar('<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>');
  agregar('<< /Type /Font /Subtype /Type1 /BaseFont /Times-Bold /Encoding /WinAnsiEncoding >>');
  const hijos: number[] = [];
  for (const contenido of paginas) {
    const flujo = aWinAnsi(contenido);
    agregar(`<< /Length ${flujo.length} >>\nstream\n${flujo}\nendstream`);
    const idContenido = objetos.length;
    agregar(`<< /Type /Page /Parent 2 0 R /MediaBox [0 0 ${ANCHO} ${ALTO}] /Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R >> >> /Contents ${idContenido} 0 R >>`);
    hijos.push(objetos.length);
  }
  objetos[1] = `<< /Type /Pages /Kids [${hijos.map(id => `${id} 0 R`).join(' ')}] /Count ${hijos.length} >>`;

  let salida = '%PDF-1.4\n%\xE2\xE3\xCF\xD3\n';
  const desplazamientos: number[] = [];
  objetos.forEach((cuerpo, i) => {
    desplazamientos.push(salida.length);
    salida += `${i + 1} 0 obj\n${cuerpo.startsWith('<< /Length') ? cuerpo : aWinAnsi(cuerpo)}\nendobj\n`;
  });
  const inicioXref = salida.length;
  salida += `xref\n0 ${objetos.length + 1}\n0000000000 65535 f \n`;
  salida += desplazamientos.map(d => `${String(d).padStart(10, '0')} 00000 n \n`).join('');
  salida += `trailer\n<< /Size ${objetos.length + 1} /Root 1 0 R >>\nstartxref\n${inicioXref}\n%%EOF`;
  const bytes = new Uint8Array(new ArrayBuffer(salida.length));
  for (let i = 0; i < salida.length; i++) {
    bytes[i] = salida.charCodeAt(i) & 0xff;
  }
  return bytes;
}

const WIN_ANSI: Record<string, number> = { '–': 0x96, '—': 0x97, '‘': 0x91, '’': 0x92, '“': 0x93, '”': 0x94, '•': 0x95, '…': 0x85, '€': 0x80 };

function aWinAnsi(texto: string): string {
  let salida = '';
  for (const caracter of texto) {
    const codigo = caracter.charCodeAt(0);
    salida += WIN_ANSI[caracter] !== undefined ? String.fromCharCode(WIN_ANSI[caracter]) : codigo < 256 ? caracter : '?';
  }
  return salida;
}

function escapar(texto: string): string {
  return texto.replace(/\\/g, '\\\\').replace(/\(/g, '\\(').replace(/\)/g, '\\)').replace(/[\r\n]+/g, ' ');
}

function medir(texto: string, tamano: number, fuente: 'F1' | 'F2' | 'F3'): number {
  let unidades = 0;
  for (const caracter of texto) {
    if (caracter === ' ') unidades += 278;
    else if ('iljtfI.,:;|!\''.includes(caracter)) unidades += 278;
    else if ('mwMW'.includes(caracter)) unidades += 833;
    else if (caracter >= 'A' && caracter <= 'Z') unidades += 667;
    else if (caracter >= '0' && caracter <= '9') unidades += 556;
    else unidades += 540;
  }
  const factor = fuente === 'F1' ? 1 : fuente === 'F2' ? 1.06 : 0.98;
  return (unidades / 1000) * tamano * factor;
}

function recortar(texto: string, ancho: number, tamano: number, fuente: 'F1' | 'F2' | 'F3'): string {
  if (medir(texto, tamano, fuente) <= ancho) {
    return texto;
  }
  let recortado = texto;
  while (recortado.length > 1 && medir(`${recortado}…`, tamano, fuente) > ancho) {
    recortado = recortado.slice(0, -1);
  }
  return `${recortado.trimEnd()}…`;
}

function minutos(valor: string): number {
  const [h, m] = valor.split(':').map(Number);
  return h * 60 + (m || 0);
}

function hora(valor: string | null): string {
  return valor ? valor.slice(0, 5) : '';
}

function hex(valor: string): Rgb {
  return [1, 3, 5].map(i => parseInt(valor.slice(i, i + 2), 16) / 255) as Rgb;
}

function rgb(color: Rgb): string {
  return color.map(c => c.toFixed(3)).join(' ');
}

function n(valor: number): string {
  return Number(valor.toFixed(2)).toString();
}
