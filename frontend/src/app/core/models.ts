export interface Aluno {
  id: number;
  nome: string;
  email: string;
  ra: string;
  ativo: boolean;
  criadoEm?: string;
  atualizadoEm?: string;
}

export interface Curso {
  id: number;
  codigo: string;
  nome: string;
  cargaHoraria: number;
  ativo: boolean;
}

export interface Disciplina {
  id: number;
  cursoId: number;
  cursoCodigo: string;
  codigo: string;
  nome: string;
  cargaHoraria: number;
}

export interface Turma {
  id: number;
  disciplinaId: number;
  disciplinaCodigo: string;
  codigo: string;
  periodo: string;
  vagasTotais: number;
  vagasOcupadas: number;
  vagasDisponiveis: number;
  status: 'ABERTA' | 'FECHADA';
  version: number;
}

export interface Matricula {
  id: number;
  alunoId: number;
  alunoNome: string;
  alunoRa: string;
  turmaId: number;
  turmaCodigo: string;
  turmaPeriodo: string;
  status: 'PENDENTE' | 'CONFIRMADA' | 'CANCELADA';
  criadoEm?: string;
  confirmadoEm?: string | null;
  canceladoEm?: string | null;
}

export interface ApiError {
  timestamp?: string;
  status: number;
  erro: string;
  codigo: string;
  mensagem: string;
  path?: string;
  campos?: { campo: string; mensagem: string }[];
}
