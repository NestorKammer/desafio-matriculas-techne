import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turma } from '../core/models';

@Injectable({ providedIn: 'root' })
export class TurmaService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/turmas';

  listar(disciplinaId?: number): Observable<Turma[]> {
    let params = new HttpParams();
    if (disciplinaId != null) {
      params = params.set('disciplinaId', disciplinaId);
    }
    return this.http.get<Turma[]>(this.base, { params });
  }

  criar(payload: {
    disciplinaId: number;
    codigo: string;
    periodo: string;
    vagasTotais: number;
  }): Observable<Turma> {
    return this.http.post<Turma>(this.base, payload);
  }

  atualizar(
    id: number,
    payload: { codigo: string; periodo: string; vagasTotais: number }
  ): Observable<Turma> {
    return this.http.put<Turma>(`${this.base}/${id}`, payload);
  }

  abrir(id: number): Observable<Turma> {
    return this.http.post<Turma>(`${this.base}/${id}/abrir`, {});
  }

  fechar(id: number): Observable<Turma> {
    return this.http.post<Turma>(`${this.base}/${id}/fechar`, {});
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
