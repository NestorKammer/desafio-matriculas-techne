import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Aluno } from '../core/models';

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/alunos';

  listar(): Observable<Aluno[]> {
    return this.http.get<Aluno[]>(this.base);
  }

  criar(payload: { nome: string; email: string; ra: string }): Observable<Aluno> {
    return this.http.post<Aluno>(this.base, payload);
  }

  atualizar(id: number, payload: { nome: string; email: string; ra: string }): Observable<Aluno> {
    return this.http.put<Aluno>(`${this.base}/${id}`, payload);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
