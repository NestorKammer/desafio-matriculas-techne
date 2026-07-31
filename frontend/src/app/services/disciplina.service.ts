import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Disciplina } from '../core/models';

@Injectable({ providedIn: 'root' })
export class DisciplinaService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/disciplinas';

  listar(cursoId?: number): Observable<Disciplina[]> {
    let params = new HttpParams();
    if (cursoId != null) {
      params = params.set('cursoId', cursoId);
    }
    return this.http.get<Disciplina[]>(this.base, { params });
  }

  criar(payload: {
    cursoId: number;
    codigo: string;
    nome: string;
    cargaHoraria: number;
  }): Observable<Disciplina> {
    return this.http.post<Disciplina>(this.base, payload);
  }

  atualizar(
    id: number,
    payload: { cursoId: number; codigo: string; nome: string; cargaHoraria: number }
  ): Observable<Disciplina> {
    return this.http.put<Disciplina>(`${this.base}/${id}`, payload);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
