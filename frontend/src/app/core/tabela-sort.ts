export type SortDir = 'asc' | 'desc';

function compararValores(a: unknown, b: unknown): number {
  if (a == null && b == null) {
    return 0;
  }
  if (a == null) {
    return -1;
  }
  if (b == null) {
    return 1;
  }
  if (typeof a === 'number' && typeof b === 'number') {
    return a - b;
  }
  if (typeof a === 'boolean' && typeof b === 'boolean') {
    return Number(a) - Number(b);
  }
  return String(a).localeCompare(String(b), 'pt-BR', { numeric: true, sensitivity: 'base' });
}

/** Ordenacao client-side para tabelas de listagem. */
export class TabelaSort<T extends object> {
  coluna: keyof T & string;
  direcao: SortDir = 'asc';

  constructor(colunaInicial: keyof T & string) {
    this.coluna = colunaInicial;
  }

  aplicar(lista: T[]): T[] {
    const col = this.coluna;
    const dir = this.direcao === 'asc' ? 1 : -1;
    return [...lista].sort((a, b) => compararValores(a[col], b[col]) * dir);
  }

  toggle(coluna: keyof T & string): void {
    if (this.coluna === coluna) {
      this.direcao = this.direcao === 'asc' ? 'desc' : 'asc';
      return;
    }
    this.coluna = coluna;
    this.direcao = 'asc';
  }

  aria(coluna: string): 'ascending' | 'descending' | 'none' {
    if (this.coluna !== coluna) {
      return 'none';
    }
    return this.direcao === 'asc' ? 'ascending' : 'descending';
  }
}
