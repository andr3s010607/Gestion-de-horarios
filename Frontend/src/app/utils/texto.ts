export function normalizar(texto: string | null | undefined): string {
  return (texto ?? '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase().trim();
}

export function coincide(termino: string, ...campos: (string | number | null | undefined)[]): boolean {
  const buscado = normalizar(termino);
  if (!buscado) {
    return true;
  }
  return normalizar(campos.filter(campo => campo !== null && campo !== undefined).join(' ')).includes(buscado);
}

export function horaCorta(hora: string | null | undefined): string {
  return hora ? hora.slice(0, 5) : '';
}

export function aMinutos(hora: string): number {
  const [h, m] = hora.split(':').map(Number);
  return h * 60 + (m || 0);
}

export function conSegundos(hora: string): string {
  return hora.length === 5 ? `${hora}:00` : hora;
}
