import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Curso } from '../core/models';

@Injectable({ providedIn: 'root' })
export class CursoService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/cursos';

  listar(): Observable<Curso[]> {
    return this.http.get<Curso[]>(this.base);
  }

  criar(payload: { codigo: string; nome: string; cargaHoraria: number }): Observable<Curso> {
    return this.http.post<Curso>(this.base, payload);
  }

  atualizar(
    id: number,
    payload: { codigo: string; nome: string; cargaHoraria: number }
  ): Observable<Curso> {
    return this.http.put<Curso>(`${this.base}/${id}`, payload);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
