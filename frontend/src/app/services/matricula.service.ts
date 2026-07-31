import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Matricula } from '../core/models';

@Injectable({ providedIn: 'root' })
export class MatriculaService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/matriculas';

  consultar(filtro: { alunoId?: number; turmaId?: number }): Observable<Matricula[]> {
    let params = new HttpParams();
    if (filtro.alunoId != null) {
      params = params.set('alunoId', filtro.alunoId);
    }
    if (filtro.turmaId != null) {
      params = params.set('turmaId', filtro.turmaId);
    }
    return this.http.get<Matricula[]>(this.base, { params });
  }

  matricular(alunoId: number, turmaId: number): Observable<Matricula> {
    return this.http.post<Matricula>(this.base, { alunoId, turmaId });
  }

  confirmar(id: number): Observable<Matricula> {
    return this.http.post<Matricula>(`${this.base}/${id}/confirmar`, {});
  }

  cancelar(id: number): Observable<Matricula> {
    return this.http.post<Matricula>(`${this.base}/${id}/cancelar`, {});
  }
}
